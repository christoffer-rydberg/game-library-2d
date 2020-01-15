package com.gamelibrary2d.network.common;

import java.io.IOException;

public interface UdpReceiver {

    void connectUdpReceiver(int localPort) throws IOException;

    void disconnectUdpReceiver();
}