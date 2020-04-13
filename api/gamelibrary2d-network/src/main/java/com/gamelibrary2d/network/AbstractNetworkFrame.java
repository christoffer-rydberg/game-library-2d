package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.DefaultCommunicationContext;

import java.util.concurrent.ExecutionException;

public abstract class AbstractNetworkFrame<T extends Client>
        extends AbstractFrame {

    private final T client;
    private final Object clientContextKey = new Object();

    protected AbstractNetworkFrame(Game game, T client) {
        super(game);
        this.client = client;
    }

    public T getClient() {
        return client;
    }

    @Override
    public void load(LoadingContext context) throws InitializationException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            System.err.println("Must call initialize prior to load");
            return;
        }

        // TODO: How to guarantee that connect is always threadsafe?
        if (!client.isConnected()) {
            try {
                client.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new InitializationException("Failed to connect to server", e);
            }
        }

        client.clearInbox();
        var clientContext = new DefaultCommunicationContext();
        authenticate(client, clientContext);
        super.load(context);
        initialize(client, clientContext);
        context.register(clientContextKey, clientContext);
    }

    private void authenticate(Client client, CommunicationContext context) throws InitializationException {
        try {
            client.authenticate(context);
        } catch (NetworkAuthenticationException e) {
            throw new InitializationException("Failed to authenticate client", e);
        }
    }

    private void initialize(Client client, CommunicationContext context) throws InitializationException {
        try {
            client.initialize(context);
        } catch (NetworkInitializationException e) {
            throw new InitializationException("Failed to initialize client", e);
        }
    }

    @Override
    public void loaded(LoadingContext context) {
        client.initialized(context.get(CommunicationContext.class, clientContextKey));
        super.loaded(context);

    }

    @Override
    protected final void onUpdate(float deltaTime) {
        client.update(deltaTime, this::handleUpdate);
    }

    protected void handleUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
    }
}