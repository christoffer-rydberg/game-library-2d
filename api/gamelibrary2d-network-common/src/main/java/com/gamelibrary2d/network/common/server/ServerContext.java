package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface ServerContext {

    void start();

    void stop();

    boolean acceptConnection(String endpoint);

    void onConnectionFailed(String endpoint, Exception e);

    void onConnected(Communicator communicator);

    void configureClientAuthentication(CommunicationSteps steps);

    void configureClientInitialization(CommunicationSteps steps);

    void onClientAuthenticated(Communicator communicator);

    void onClientInitialized(Communicator communicator);

    void update(float deltaTime);

    void onMessage(Communicator communicator, DataBuffer buffer);

    void onDisconnected(Communicator communicator, boolean pending);
}
