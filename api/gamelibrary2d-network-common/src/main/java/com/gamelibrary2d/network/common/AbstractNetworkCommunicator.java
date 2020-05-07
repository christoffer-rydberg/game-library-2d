package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public abstract class AbstractNetworkCommunicator extends AbstractCommunicator implements NetworkCommunicator {
    private final NetworkService networkService;
    private final boolean ownsNetworkService;

    private volatile UdpConnection udpConnection;
    private volatile SocketChannel socketChannel;

    protected AbstractNetworkCommunicator(NetworkService networkService, int incomingChannels, boolean ownsNetworkService) {
        super(incomingChannels);
        this.networkService = networkService;
        this.ownsNetworkService = ownsNetworkService;
    }

    protected NetworkService getNetworkService() {
        return networkService;
    }

    protected SocketChannel getSocketChannel() {
        return socketChannel;
    }

    protected void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    protected void onSocketChannelDisconnected(IOException error) {
        disconnect(error);
    }

    protected void onDatagramChannelDisconnected(IOException error) {
        if (error != null) {
            disconnect(error);
        }
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        disconnectTcp();
        disableUdp();
        if (ownsNetworkService) {
            try {
                networkService.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void send(DataBuffer buffer) throws IOException {
        var socketChannel = this.socketChannel;
        if (socketChannel == null)
            throw new IOException("Socket channel not connected");
        networkService.send(socketChannel, buffer);
    }


    @Override
    public void sendUpdate(DataBuffer buffer) throws IOException {
        if (udpConnection != null && udpConnection.connectionType != ConnectionType.READ) {
            getNetworkService().send(udpConnection.channel, buffer);
        } else {
            super.sendUpdate(buffer);
        }
    }

    protected void disconnectTcp() {
        if (socketChannelConnected()) {
            var socketChannel = this.socketChannel;
            setSocketChannel(null);
            networkService.closeAfterLastScheduledSend(socketChannel);
        }
    }

    @Override
    public void enableUdp(ConnectionType connectionType, int localPort, int hostPort) throws IOException {
        if (!isConnected()) {
            throw new IOException("Communicator is not connected");
        }

        disableUdp();

        var channel = networkService.openDatagramChannel(this, connectionType,
                this::onDatagramChannelDisconnected, localPort, hostPort);

        this.udpConnection = new UdpConnection(channel, connectionType);
    }

    @Override
    public void disableUdp() {
        if (this.udpConnection != null) {
            var connection = this.udpConnection;
            this.udpConnection = null;
            networkService.disconnect(connection.channel);
        }
    }

    protected boolean socketChannelConnected() {
        return socketChannel != null;
    }

    private class UdpConnection {
        private final DatagramChannel channel;
        private final ConnectionType connectionType;

        public UdpConnection(DatagramChannel channel, ConnectionType connectionType) {
            this.channel = channel;
            this.connectionType = connectionType;
        }
    }
}