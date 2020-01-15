package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.AbstractNetworkCommunicator;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.ConnectionOperations;
import com.gamelibrary2d.network.common.UdpTransmitter;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ServerSideCommunicator extends AbstractNetworkCommunicator implements UdpTransmitter {

    private final String endpoint;

    public ServerSideCommunicator(CommunicationServer communicationServer, SocketChannel socketChannel) {
        super(communicationServer, 1, true);
        setSocketChannel(socketChannel);
        endpoint = socketChannel.socket().getInetAddress().getHostAddress();
        getCommunicationServer().connect(getSocketChannel(), this, this::onSocketChannelDisconnected);
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public void connectUdpTransmitter(int hostPort) throws IOException {
        super.connectUdp(ConnectionOperations.WRITE, 0, hostPort);
    }

    @Override
    public void disconnectUdpTransmitter() {
        super.disconnectUdp();
    }

    @Override
    public void sendUpdate(DataBuffer buffer) throws IOException {
        if (!datagramChannelConnected()) {
            super.sendUpdate(buffer);
        } else {
            getCommunicationServer().send(getDatagramChannel(), buffer);
        }
    }
}