package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.client.AbstractSecureClient;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

class InternalNetworkClient extends AbstractSecureClient {
    private FrameClient context;

    void setContext(FrameClient context) {
        this.context = context;
    }

    @Override
    protected void onConfigureAuthentication(CommunicationInitializer initializer) {
        context.configureAuthentication(initializer);
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
    protected void onDisconnected(Throwable cause) {
        context.onDisconnected(cause);
    }

    float getServerUpdateRate() {
        return context.getServerUpdateRate();
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