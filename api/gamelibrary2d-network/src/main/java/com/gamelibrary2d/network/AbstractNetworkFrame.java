package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
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
    public void load(LoadingContext context) throws LoadFailedException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            System.err.println("Must call initialize prior to load");
            return;
        }

        if (!client.isConnected()) {
            try {
                client.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LoadFailedException("Failed to connect to server", e);
            }
        }


        try {
            client.clearInbox();
            var clientContext = new DefaultCommunicationContext();
            client.authenticate(clientContext);
            super.load(context);
            client.initialize(clientContext);
            context.register(clientContextKey, clientContext);
        } catch (InitializationException e) {
            unload();
            throw new LoadFailedException("Client/server communication failed", e);
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