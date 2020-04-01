package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.util.concurrent.ExecutionException;

public abstract class AbstractNetworkFrame<T extends Client>
        extends AbstractFrame {

    private final T frameClient;

    protected AbstractNetworkFrame(Game game, T frameClient) {
        super(game);
        this.frameClient = frameClient;
    }

    public T getClient() {
        return frameClient;
    }

    @Override
    public void load() throws LoadFailedException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            System.err.println("Must call initialize prior to load");
            return;
        }

        if (!frameClient.isConnected()) {
            try {
                frameClient.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LoadFailedException("Failed to connect to server", e);
            }
        }

        try {
            frameClient.clearInbox();
            frameClient.authenticate();
            super.load();
            frameClient.initialize();
        } catch (InitializationException e) {
            unload();
            throw new LoadFailedException("Client/server communication failed", e);
        }
    }

    @Override
    protected final void onUpdate(float deltaTime) {
        frameClient.update(deltaTime, this::handleUpdate);
    }

    protected void handleUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
    }
}