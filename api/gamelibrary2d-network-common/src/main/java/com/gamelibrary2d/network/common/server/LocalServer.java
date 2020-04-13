package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;

public interface LocalServer extends Server {

    void connectCommunicator(Communicator communicator) throws IOException;

    void configureClientAuthentication(CommunicationSteps steps);

}
