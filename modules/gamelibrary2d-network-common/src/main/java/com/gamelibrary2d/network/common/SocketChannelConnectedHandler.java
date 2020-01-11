package com.gamelibrary2d.network.common;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface SocketChannelConnectedHandler {
    void onConnected(SocketChannel socketChannel) throws IOException;
}
