package com.gamelibrary2d.network;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.client.AbstractSecureClient;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

class InternalNetworkClient extends AbstractSecureClient {

    private final AbstractGenericNetworkFrame<?, ?> frame;

    private ParameterizedAction<Throwable> onDisconnect;

    InternalNetworkClient(AbstractGenericNetworkFrame<?, ?> frame) {
        this.frame = frame;
    }

    void onDisconnect(ParameterizedAction<Throwable> onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    @Override
    protected void onConfigureAuthentication(CommunicationInitializer initializer) {
        frame.configureAuthentication(initializer);
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {
        frame.configureInitialization(initializer);
    }

    protected void onMessage(DataBuffer buffer) {
        frame.onMessage(buffer);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        if (onDisconnect != null) {
            onDisconnect.invoke(cause);
        }
    }
}