package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

class InternalNetworkClient<TFrameClient extends FrameClient> extends AbstractClient {
    private TFrameClient context;

    void setContext(TFrameClient context) {
        this.context = context;
    }

    TFrameClient getContext() {
        return context;
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {
        context.configureInitialization(initializer);
    }

    @Override
    protected void onMessage(DataBuffer buffer) {
        context.onMessage(buffer);
    }

    @Override
    public Communicator getCommunicator() {
        return context.getCommunicator();
    }

    @Override
    protected int getInitializationRetries() {
        return context.getInitializationRetries();
    }

    @Override
    protected int getInitializationRetryDelay() {
        return context.getInitializationRetryDelay();
    }
}