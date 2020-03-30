package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface LocalServer extends Server {

    void initialize() throws InitializationException;

    void connectCommunicator(Communicator communicator) throws InitializationException;

    void configureClientAuthentication(CommunicationSteps steps);

}
