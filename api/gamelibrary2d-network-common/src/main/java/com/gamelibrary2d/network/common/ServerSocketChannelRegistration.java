package com.gamelibrary2d.network.common;

import java.nio.channels.ServerSocketChannel;

public class ServerSocketChannelRegistration {

    private final ServerSocketChannel serverSocketChannel;

    private final int localPort;

    ServerSocketChannelRegistration(ServerSocketChannel serverSocketChannel, int localPort) {
        this.serverSocketChannel = serverSocketChannel;
        this.localPort = localPort;
    }

    ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public int getLocalPort() {
        return localPort;
    }

}
