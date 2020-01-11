package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

public interface LocalServer extends Server {

    void addCommunicator(Communicator communicator) throws InitializationException;

}
