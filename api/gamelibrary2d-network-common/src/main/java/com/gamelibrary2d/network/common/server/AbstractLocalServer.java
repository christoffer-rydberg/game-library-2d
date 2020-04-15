package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;

public abstract class AbstractLocalServer extends InternalAbstractServer implements LocalServer {

    private Communicator communicator;

    @Override
    public void connectCommunicator(Communicator communicator) {
        super.addConnectedCommunicator(communicator);
        this.communicator = communicator;
    }

    @Override
    protected void onStop() {
        if (communicator != null) {
            communicator.disconnect();
        }
    }
}
