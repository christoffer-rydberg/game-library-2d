package com.gamelibrary2d.network;

import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;

public abstract class AbstractNetworkFrame<T extends Client> extends AbstractFrame {
    private final Object clientContextKey = new Object();
    private T client;

    protected AbstractNetworkFrame() {

    }

    protected AbstractNetworkFrame(T client) {
        this.client = client;
    }

    public T getClient() {
        return client;
    }

    protected void setClient(T client) {
        this.client = client;
    }

    @Override
    protected void handleLoad(InitializationContext context) throws InitializationException {
        initializeClient(client, context);
        super.handleLoad(context);
    }

    private void initializeClient(Client client, InitializationContext context) throws InitializationException {
        try {
            client.clearInbox();
            var clientContext = client.initialize();
            context.register(clientContextKey, clientContext);
        } catch (NetworkInitializationException | NetworkConnectionException | NetworkAuthenticationException e) {
            throw new InitializationException("Failed to initialize client", e);
        }
    }

    @Override
    public void loaded(InitializationContext context) {
        super.loaded(context);
        client.initialized(context.get(CommunicationContext.class, clientContextKey));
    }

    @Override
    protected final void handleUpdate(float deltaTime) {
        client.update(deltaTime, this::onUpdate);
    }

    protected void onUpdate(float deltaTime) {
        super.handleUpdate(deltaTime);
    }
}