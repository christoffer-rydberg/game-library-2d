package com.gamelibrary2d.network.common;

import java.io.IOException;

public interface UdpTransmitter {

    void connectUdpTransmitter(int hostPort) throws IOException;

    void disconnectUdpTransmitter();
}