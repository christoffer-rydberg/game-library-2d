package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.frames.Frame;

import java.util.List;

public interface NetworkFrame extends Frame {

    /**
     * @return The communicator used to communicate with the server.
     */
    Communicator getCommunicator();

    /**
     * Sets the communicator used to communicate with the server.
     */
    void setCommunicator(Communicator communicator);

    /**
     * @return The update streaming rate of the server (updates per second)
     */
    float getServerUpdateRate();
}