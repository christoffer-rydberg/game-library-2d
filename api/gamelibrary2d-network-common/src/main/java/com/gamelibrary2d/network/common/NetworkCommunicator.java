package com.gamelibrary2d.network.common;

import java.io.IOException;

public interface NetworkCommunicator extends Communicator {
    /**
     * Enables the communicator to send and/or receive UDP packages.
     */
    void enableUdp(ConnectionType connectionType, int localPort, int hostPort) throws IOException;

    /**
     * Disables UDP.
     */
    void disableUdp();

}