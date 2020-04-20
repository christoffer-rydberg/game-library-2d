package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public abstract class AbstractNetworkCommunicator extends AbstractCommunicator {

    private final NetworkService networkService;
    private final boolean ownsNetworkService;

    private volatile DatagramChannel datagramChannel;
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

    protected DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }

    protected void setDatagramChannel(DatagramChannel datagramChannel) {
        this.datagramChannel = datagramChannel;
    }

    protected void onSocketChannelDisconnected(IOException ioException) {
        disconnect(ioException);
    }

    protected void onDatagramChannelDisconnected(IOException ioException) {
        disconnect(ioException);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        disconnectTcp();
        disconnectUdp();
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

    protected void disconnectTcp() {
        if (socketChannelConnected()) {
            var socketChannel = this.socketChannel;
            setSocketChannel(null);
            networkService.closeAfterLastScheduledSend(socketChannel);
        }
    }

    protected void connectUdp(ConnectionOperations allowedOperations, int localPort, int hostPort) throws IOException {
        if (!isConnected()) {
            throw new IOException("Communicator is not connected");
        }

        setDatagramChannel(networkService.openDatagramChannel(this, allowedOperations,
                this::onDatagramChannelDisconnected, localPort, hostPort));
    }

    protected void disconnectUdp() {
        if (datagramChannelConnected()) {
            var datagramChannel = this.datagramChannel;
            setDatagramChannel(null);
            networkService.disconnect(datagramChannel);
        }
    }

    protected boolean datagramChannelConnected() {
        return datagramChannel != null;
    }

    protected boolean socketChannelConnected() {
        return socketChannel != null;
    }
}