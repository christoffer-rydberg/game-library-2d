package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class AbstractNetworkFrame<TFrameClient extends FrameClient>
        extends AbstractFrame {

    private final InternalNetworkClient<TFrameClient> networkClient;

    protected AbstractNetworkFrame(Game game, TFrameClient frameClient) {
        super(game);
        networkClient = new InternalNetworkClient<>();
        networkClient.setFrameClient(frameClient);
        networkClient.setSendingDataOnUpdate(false); // Send data after frame update instead
    }

    public TFrameClient getClient() {
        return networkClient.getFrameClient();
    }

    @Override
    public void load() throws LoadFailedException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            System.err.println("Must call initialize prior to load");
            return;
        }

        if (!networkClient.isConnected()) {
            try {
                networkClient.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LoadFailedException("Failed to connect to server", e);
            }
        }

        try {
            networkClient.clearInbox();
            networkClient.authenticate();
            super.load();
            networkClient.initialize();
        } catch (InitializationException e) {
            unload();
            throw new LoadFailedException("Client/server communication failed", e);
        }
    }

    @Override
    protected void unload() {
        networkClient.deinitialize();
        super.unload();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        networkClient.update(deltaTime);
        super.onUpdate(deltaTime);
        var communicator = networkClient.getCommunicator();
        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
        }
    }

    static class InternalNetworkClient<TFrameClient extends FrameClient> extends AbstractClient {
        private TFrameClient frameClient;

        TFrameClient getFrameClient() {
            return frameClient;
        }

        void setFrameClient(TFrameClient frameClient) {
            this.frameClient = frameClient;
        }

        @Override
        protected void onConfigureInitialization(CommunicationSteps steps) {
            frameClient.configureInitialization(steps);
        }

        @Override
        protected void onMessage(DataBuffer buffer) {
            frameClient.onMessage(buffer);
        }

        @Override
        public Communicator getCommunicator() {
            return frameClient.getCommunicator();
        }

        @Override
        protected int getInitializationRetries() {
            return frameClient.getInitializationRetries();
        }

        @Override
        protected int getInitializationRetryDelay() {
            return frameClient.getInitializationRetryDelay();
        }

        @Override
        protected void onInitialized() {
            frameClient.onInitialized();
        }

        @Override
        protected boolean isUpdatingLocalServer() {
            return frameClient.isUpdatingLocalServer();
        }
    }
}