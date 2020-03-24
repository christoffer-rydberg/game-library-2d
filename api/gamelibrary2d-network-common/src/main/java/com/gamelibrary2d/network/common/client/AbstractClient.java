package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.*;
import com.gamelibrary2d.network.common.internal.CommunicatorInitializer;
import com.gamelibrary2d.network.common.internal.InternalCommunicationSteps;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractClient {
    private final DataBuffer inbox;
    private boolean sendingDataOnUpdate = true;
    private boolean initialized;

    protected AbstractClient() {
        this.inbox = new DynamicByteBuffer();
        inbox.flip();
    }

    public boolean isSendingDataOnUpdate() {
        return sendingDataOnUpdate;
    }

    public void setSendingDataOnUpdate(boolean sendingDataOnUpdate) {
        this.sendingDataOnUpdate = sendingDataOnUpdate;
    }

    public void clearInbox() {
        inbox.clear();
        inbox.flip();
    }

    public boolean isConnected() {
        return getCommunicator().isConnected();
    }

    public Future<Void> connect() {
        var communicator = getCommunicator();
        if (communicator instanceof Connectable) {
            return ((Connectable) getCommunicator()).connect();
        }
        return CompletableFuture.completedFuture(null);
    }

    public void disconnect() {
        var communicator = getCommunicator();
        if (communicator != null)
            communicator.disconnect();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void deinitialize() {
        this.initialized = false;
    }

    public void initialize() throws InitializationException {
        if (initialized) {
            throw new InitializationException("Client has already been initialized");
        }
        var communicator = getCommunicator();
        var steps = new InternalCommunicationSteps();
        configureInitialization(steps);
        runCommunicationSteps(communicator, new CommunicatorInitializer(steps.getAll()));
        initialized = true;
        onInitialized();
    }

    public void update() {
        var communicator = getCommunicator();
        if (communicator == null) {
            return;
        }

        if (communicator.isConnected()) {
            try {
                if (!communicator.isAuthenticated()) {
                    authenticate();
                }

                if (!initialized) {
                    initialize();
                }

                readMessages();
                if (sendingDataOnUpdate) {
                    try {
                        communicator.sendOutgoing();
                    } catch (IOException e) {
                        communicator.disconnect(e);
                    }
                }
            } catch (Exception e) {
                communicator.disconnect(e);
            }
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

    private void runCommunicationSteps(Communicator communicator, CommunicatorInitializer initializer) throws InitializationException {
        CommunicatorInitializer.InitializationResult result;

        int retries = 0;
        do {

            result = initializer.runCommunicationStep(communicator, this::runCommunicationStep);
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

                if (!triggerLocalServerUpdate(communicator)) {
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

        triggerLocalServerUpdate(communicator);
    }

    private boolean runCommunicationStep(Communicator communicator, CommunicationStep step)
            throws InitializationException {
        if (step instanceof ConsumerStep) {
            try {
                return refreshInboxIfEmpty(communicator) && ((ConsumerStep) step).run(communicator, inbox);
            } catch (IOException e) {
                communicator.disconnect(e);
                throw new InitializationException("Connection has been lost", e);
            }
        } else if (step instanceof ProducerStep) {
            ((ProducerStep) step).run(communicator);
            return true;
        } else {
            throw new InitializationException("Unknown communication step");
        }
    }

    /**
     * Updates local server to trigger delivery of messages.
     *
     * @return True the server is local, false otherwise.
     */
    private boolean triggerLocalServerUpdate(Communicator communicator) {
        if (communicator instanceof LocalCommunicator) {
            ((LocalCommunicator) communicator).getLocalServer().update(0);
            return true;
        }

        return false;
    }

    /**
     * The max number of retries for each communication step.
     */
    protected int getInitializationRetries() {
        return 10;
    }

    /**
     * The delay between retries of communication steps in milliseconds.
     */
    protected int getInitializationRetryDelay() {
        return 1000;
    }

    public void authenticate() throws InitializationException {
        var communicator = getCommunicator();
        if (!communicator.isAuthenticated()) {
            var steps = new InternalCommunicationSteps();
            configureAuthentication(communicator, steps);
            runCommunicationSteps(communicator, new CommunicatorInitializer(steps.getAll()));
        }
    }

    private void configureInitialization(CommunicationSteps steps) {
        onConfigureInitialization(steps);
    }

    private void configureAuthentication(Communicator communicator, CommunicationSteps steps) {
        steps.add(new IdentityConsumer());
        communicator.configureAuthentication(steps);
        steps.add(this::onAuthenticated);
    }

    private void onAuthenticated(Communicator communicator) {
        communicator.onAuthenticated();
    }

    protected abstract void onInitialized();

    public abstract Communicator getCommunicator();

    protected abstract void onConfigureInitialization(CommunicationSteps steps);

    protected abstract void onMessage(DataBuffer buffer);
}