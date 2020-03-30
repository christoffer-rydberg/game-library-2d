package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

public abstract class AbstractLocalServer extends InternalAbstractServer implements LocalServer {

    private Communicator communicator;

    @Override
    public void connectCommunicator(Communicator communicator) throws InitializationException {
        super.addCommunicator(communicator);
        this.communicator = communicator;
    }

    @Override
    public void stop() {
        if (communicator != null) {
            communicator.disconnect();
        }
    }
}
