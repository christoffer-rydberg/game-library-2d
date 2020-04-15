package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.DefaultCommunicationContext;

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

        initializeClient(client, context);

        super.load(context);
    }

    private void initializeClient(Client client, LoadingContext context) throws InitializationException {
        try {
            client.clearInbox();
            var clientContext = new DefaultCommunicationContext();
            context.register(clientContextKey, clientContext);
            client.prepare(clientContext);
        } catch (NetworkInitializationException | NetworkConnectionException | NetworkAuthenticationException e) {
            throw new InitializationException("Failed to initialize client", e);
        }
    }

    @Override
    public void loaded(LoadingContext context) {
        client.prepared(context.get(CommunicationContext.class, clientContextKey));
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