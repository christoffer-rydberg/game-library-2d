package com.gamelibrary2d.network;

public abstract class AbstractFrameClient implements FrameClient {

    private int initializationRetries = 10;

    private int initializationRetryDelay = 1000;

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

}
