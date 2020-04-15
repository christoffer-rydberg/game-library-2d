package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface LocalServer extends Server {

    void connectCommunicator(Communicator communicator);

    void configureClientAuthentication(CommunicationSteps steps);

}
