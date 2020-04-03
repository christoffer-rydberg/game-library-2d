package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.*;
import com.gamelibrary2d.network.common.internal.CommunicatorInitializer;
import com.gamelibrary2d.network.common.internal.InternalCommunicationSteps;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractClient implements Client {
    private final DataBuffer inbox;
    private final CommunicatorDisconnectedListener disconnectedListener = this::onDisconnected;

    private Communicator communicator;
    private int initializationRetries = 100;
    private int initializationRetryDelay = 100;
    private boolean updateLocalServer;

    protected AbstractClient() {
        this.inbox = new DynamicByteBuffer();
        inbox.flip();
    }

    public boolean isUpdatingLocalServer() {
        return updateLocalServer;
    }

    public void setUpdateLocalServer(boolean updateLocalServer) {
        this.updateLocalServer = updateLocalServer;
    }

    @Override
    public void clearInbox() {
        inbox.clear();
        inbox.flip();
    }

    @Override
    public boolean isConnected() {
        var communicator = getCommunicator();
        return communicator != null && communicator.isConnected();
    }

    @Override
    public Future<Void> connect() {
        var communicator = getCommunicator();
        if (communicator instanceof Connectable) {
            return ((Connectable) getCommunicator()).connect();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void disconnect() {
        var communicator = getCommunicator();
        if (communicator != null)
            communicator.disconnect();
    }

    @Override
    public void authenticate(CommunicationContext context) throws InitializationException {
        var communicator = getCommunicator();
        if (!communicator.isAuthenticated()) {
            var steps = new InternalCommunicationSteps();
            configureAuthentication(communicator, steps);
            runCommunicationSteps(context, communicator, new CommunicatorInitializer(steps.getAll()));
        }
    }

    public void authenticateAndInitialize() throws InitializationException {
        var context = new DefaultCommunicationContext();
        authenticate(context);
        initialize(context);
        initialized(context);
    }

    @Override
    public void initialize(CommunicationContext context) throws InitializationException {
        var communicator = getCommunicator();
        var steps = new InternalCommunicationSteps();
        configureInitialization(steps);
        runCommunicationSteps(context, communicator, new CommunicatorInitializer(steps.getAll()));
    }

    @Override
    public void update(float deltaTime) {
        update(deltaTime, null);
    }

    @Override
    public void update(float deltaTime, UpdateAction updateAction) {
        var communicator = getCommunicator();
        if (communicator == null) {
            return;
        }

        if (communicator.isConnected()) {
            try {
                triggerLocalServerUpdate(communicator, deltaTime);

                readMessages();

                if (updateAction != null) {
                    updateAction.invoke(deltaTime);
                }

                communicator.sendOutgoing();
            } catch (Exception e) {
                communicator.disconnect(e);
            }
        } else if (updateAction != null) {
            updateAction.invoke(deltaTime);
        }
    }

    /**
     * The max number of retries for each communication step.
     */
    protected int getInitializationRetries() {
        return initializationRetries;
    }

    /**
     * Sets the number of {@link #getInitializationRetries() initialization retries}.
     */
    protected void setInitializationRetries(int initializationRetries) {
        this.initializationRetries = initializationRetries;
    }

    /**
     * The delay between retries of communication steps in milliseconds.
     */
    protected int getInitializationRetryDelay() {
        return initializationRetryDelay;
    }

    /**
     * Sets the {@link #getInitializationRetries() initialization retry delay}.
     */
    protected void setInitializationRetryDelay(int initializationRetryDelay) {
        this.initializationRetryDelay = initializationRetryDelay;
    }

    @Override
    public Communicator getCommunicator() {
        return communicator;
    }

    @Override
    public void setCommunicator(Communicator communicator) {
        if (this.communicator != null) {
            this.communicator.removeDisconnectedListener(disconnectedListener);
        }

        this.communicator = communicator;

        if (this.communicator != null) {
            this.communicator.addDisconnectedListener(disconnectedListener);
        }
    }

    private void readMessages() {
        handleMessages();

        var communicator = getCommunicator();
        boolean hasIncoming;
        try {
            hasIncoming = refreshInboxIfEmpty(communicator);
        } catch (IOException e) {
            communicator.disconnect(e);
            return;
        }

        if (hasIncoming) {
            handleMessages();
        }
    }

    private void handleMessages() {
        while (inbox.remaining() > 0) {
            onMessage(inbox);
        }
    }

    private boolean refreshInboxIfEmpty(Communicator communicator) throws IOException {
        return inbox.remaining() > 0 || communicator.readIncoming(inbox);
    }

    private void runCommunicationSteps(CommunicationContext context, Communicator communicator, CommunicatorInitializer initializer) throws InitializationException {
        CommunicatorInitializer.InitializationResult result;

        int retries = 0;
        do {

            result = initializer.runCommunicationStep(context, communicator, this::runCommunicationStep);
            if (result == CommunicatorInitializer.InitializationResult.AWAITING_DATA) {

                if (retries == getInitializationRetries()) {
                    throw new InitializationException("Reading server response timed out");
                }

                if (!communicator.isConnected()) {
                    throw new InitializationException("Connection has been lost");
                }

                ++retries;

                try {
                    communicator.sendOutgoing();
                } catch (IOException e) {
                    communicator.disconnect(e);
                    throw new InitializationException("Connection has been lost", e);
                }

                if (!triggerLocalServerUpdate(communicator, 0f)) {
                    try {
                        Thread.sleep(getInitializationRetryDelay());
                    } catch (InterruptedException e) {
                        throw new InitializationException("Loading thread interrupted");
                    }
                }
            } else {
                retries = 0;
            }

        } while (result != CommunicatorInitializer.InitializationResult.FINISHED);

        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
            throw new InitializationException("Connection has been lost", e);
        }
    }

    private boolean runCommunicationStep(CommunicationContext context, Communicator communicator, CommunicationStep step)
            throws InitializationException {
        if (step instanceof ConsumerStep) {
            try {
                return refreshInboxIfEmpty(communicator) && ((ConsumerStep) step).run(context, communicator, inbox);
            } catch (IOException e) {
                communicator.disconnect(e);
                throw new InitializationException("Connection has been lost", e);
            }
        } else if (step instanceof ProducerStep) {
            ((ProducerStep) step).run(context, communicator);
            return true;
        } else {
            throw new InitializationException("Unknown communication step");
        }
    }

    private boolean triggerLocalServerUpdate(Communicator communicator, float deltaTime) {
        if (updateLocalServer && communicator instanceof LocalCommunicator) {
            ((LocalCommunicator) communicator).getLocalServer().update(deltaTime);
            return true;
        }

        return false;
    }

    private void configureInitialization(CommunicationSteps steps) {
        onConfigureInitialization(steps);
    }

    private void configureAuthentication(Communicator communicator, CommunicationSteps steps) {
        steps.add(new IdentityConsumer());
        communicator.configureAuthentication(steps);
        steps.add(this::onAuthenticated);
    }

    private void onDisconnected(CommunicatorDisconnected communicatorDisconnected) {
        onDisconnected(communicatorDisconnected.getCommunicator(), communicatorDisconnected.getCause());
    }

    private void onAuthenticated(CommunicationContext context, Communicator communicator) {
        communicator.onAuthenticated();
    }

    protected abstract void onConfigureInitialization(CommunicationSteps steps);

    protected abstract void onMessage(DataBuffer buffer);

    protected abstract void onDisconnected(Communicator communicator, Throwable cause);
}