package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public abstract class AbstractNetworkCommunicator extends AbstractCommunicator {

    private final CommunicationServer communicationServer;

    private DatagramChannel datagramChannel;

    private volatile SocketChannel socketChannel;

    private volatile ClosedSocketChannel closedSocketChannel;

    private volatile ClosedDatagramChannel closedDatagramChannel;

    protected AbstractNetworkCommunicator(CommunicationServer communicationServer, int incomingChannels,
                                          boolean connected) {
        super(incomingChannels, connected);
        this.communicationServer = communicationServer;
    }

    protected CommunicationServer getCommunicationServer() {
        return communicationServer;
    }

    protected SocketChannel getSocketChannel() {
        return socketChannel;
    }

    protected void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        closedSocketChannel = null;
    }

    protected DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }

    protected void setDatagramChannel(DatagramChannel datagramChannel) {
        this.datagramChannel = datagramChannel;
        closedDatagramChannel = null;
    }

    /**
     * This method is invoked from the {@link CommunicationServer communication
     * server} thread and must be thread safe.
     */
    protected void onSocketChannelDisconnected(IOException ioException) {
        this.closedSocketChannel = new ClosedSocketChannel(ioException);
    }

    /**
     * This method is invoked from the {@link CommunicationServer communication
     * server} thread and must be thread safe.
     */
    protected void onDatagramChannelDisconnected(IOException ioException) {
        this.closedDatagramChannel = new ClosedDatagramChannel(ioException);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        disconnectTcp();
        disconnectUdp();
    }

    @Override
    protected void send(DataBuffer buffer) throws IOException {
        var socketChannel = this.socketChannel;
        if (socketChannel == null)
            throw new IOException("Socket channel not connected");
        communicationServer.send(socketChannel, buffer);
    }

    @Override
    public boolean readIncoming(DataBuffer buffer) throws IOException {
        handleUdpDisconnect();
        handleTcpDisconnect();
        if (isConnected() && isCommunicationClosed()) {
            disconnect();
        }
        return super.readIncoming(buffer);
    }

    /**
     * Since UDP is a connection less protocol we won't know if the client closes the UDP socket.
     * A UDP disconnect can still occur if the {@link CommunicationServer} closes the UDP channel, e.g. due to an exception.
     *
     * @throws IOException The cause of the disconnect in the {@link CommunicationServer}.
     */
    private void handleUdpDisconnect() throws IOException {
        if (datagramChannelConnected() && closedDatagramChannel != null) {
            try {
                if (closedDatagramChannel.exception != null) {
                    throw closedDatagramChannel.exception;
                }
            } finally {
                setDatagramChannel(null);
            }
        }
    }

    /**
     * TCP is the only protocol that lets us know if the client (UDP is a connection less protocol).
     * Therefore, if TCP has been disconnected, the UDP connection will be closed as well.
     *
     * @throws IOException The cause of the disconnect in the {@link CommunicationServer}.
     */
    private void handleTcpDisconnect() throws IOException {
        if (socketChannelConnected() && closedSocketChannel != null) {
            try {
                if (datagramChannelConnected()) {
                    disconnectUdp();
                }
                if (closedSocketChannel.exception != null) {
                    throw closedSocketChannel.exception;
                }
            } finally {
                setSocketChannel(null);
            }
        }
    }

    protected boolean isCommunicationClosed() {
        return !socketChannelConnected() && !datagramChannelConnected();
    }

    protected void disconnectTcp() {
        if (socketChannelConnected()) {
            var socketChannel = this.socketChannel;
            setSocketChannel(null);
            communicationServer.closeAfterLastScheduledSend(socketChannel);
        }
    }

    protected void connectUdp(ConnectionOperations allowedOperations, int localPort, int hostPort) throws IOException {
        if (!isConnected()) {
            throw new IOException("Communicator is not connected");
        }

        setDatagramChannel(communicationServer.openDatagramChannel(this, allowedOperations,
                this::onDatagramChannelDisconnected, localPort, hostPort));
    }

    protected void disconnectUdp() {
        if (datagramChannelConnected()) {
            var datagramChannel = this.datagramChannel;
            setDatagramChannel(null);
            communicationServer.disconnect(datagramChannel);
        }
    }

    protected boolean datagramChannelConnected() {
        return datagramChannel != null;
    }

    protected boolean socketChannelConnected() {
        return socketChannel != null;
    }

    private static class ClosedSocketChannel {

        private final IOException exception;

        ClosedSocketChannel(IOException exception) {
            this.exception = exception;
        }
    }

    private static class ClosedDatagramChannel {

        private final IOException exception;

        ClosedDatagramChannel(IOException exception) {
            this.exception = exception;
        }
    }
}