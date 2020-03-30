package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;

public abstract class AbstractFrameClient implements FrameClient {

    private int initializationRetries = 100;

    private int initializationRetryDelay = 100;

    private Communicator communicator;

    private CommunicatorDisconnectedListener disconnectedListener = this::onDisconnected;

    private boolean updateLocalServer;

    @Override
    public boolean isUpdatingLocalServer() {
        return updateLocalServer;
    }

    @Override
    public void setUpdateLocalServer(boolean updateLocalServer) {
        this.updateLocalServer = updateLocalServer;
    }

    @Override
    public int getInitializationRetries() {
        return initializationRetries;
    }

    protected void setInitializationRetries(int initializationRetries) {
        this.initializationRetries = initializationRetries;
    }

    @Override
    public int getInitializationRetryDelay() {
        return initializationRetryDelay;
    }

    protected void setInitializationRetryDelay(int initializationRetryDelay) {
        this.initializationRetryDelay = initializationRetryDelay;
    }

    @Override
    public Communicator getCommunicator() {
        return communicator;
    }

    @Override
    public void setCommunicator(Communicator communicator) {
        if (this.communicator != null) {
            this.communicator.removeDisconnectedListener(disconnectedListener);
        }

        this.communicator = communicator;

        if (this.communicator != null) {
            this.communicator.addDisconnectedListener(disconnectedListener);
        }
    }

    private void onDisconnected(CommunicatorDisconnected communicatorDisconnected) {
        onDisconnected(communicatorDisconnected.getCommunicator(), communicatorDisconnected.getCause());
    }

    protected abstract void onDisconnected(Communicator communicator, Throwable cause);

    public void disconnect() {
        if (communicator != null) {
            communicator.disconnect();
        }
    }
}
