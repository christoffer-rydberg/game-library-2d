package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface ServerContext {

    void configureClientAuthentication(CommunicationSteps steps);

    boolean acceptConnection(String endpoint);

    void onConnectionFailed(String endpoint, Exception e);

    void onClientAuthenticated(Communicator communicator);

    void update(float deltaTime);

    void onConnected(Communicator communicator);

    void configureClientInitialization(CommunicationSteps steps);

    void onClientInitialized(Communicator communicator);

    void onDisconnected(Communicator communicator, boolean pending);

    void onMessage(Communicator communicator, DataBuffer buffer);

    void stop();
}
