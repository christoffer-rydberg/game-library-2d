package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;

public abstract class AbstractFrameClient<T extends Communicator> implements FrameClient<T> {

    private int initializationRetries = 10;

    private int initializationRetryDelay = 1000;

    private T communicator;

    private CommunicatorDisconnectedListener disconnectedListener = this::onDisconnected;

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
    public T getCommunicator() {
        return communicator;
    }

    @Override
    public void setCommunicator(T communicator) {
        if (this.communicator != null) {
            this.communicator.removeDisconnectedListener(disconnectedListener);
        }

        this.communicator = communicator;

        if (this.communicator != null) {
            this.communicator.addDisconnectedListener(disconnectedListener);
        }
    }

    private void onDisconnected(CommunicatorDisconnected communicatorDisconnected) {
        onDisconnected((T) communicatorDisconnected.getCommunicator(), communicatorDisconnected.getCause());
    }

    protected abstract void onDisconnected(T communicator, Throwable cause);

}
