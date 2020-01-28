package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.updating.UpdateTarget;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.ConsumerPhase;
import com.gamelibrary2d.network.common.initialization.InitializationPhase;
import com.gamelibrary2d.network.common.initialization.ProducerPhase;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper.InitializationResult;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractClient implements UpdateTarget {

    private final DataBuffer inbox;
    CommunicatorWrapper communicator = new CommunicatorWrapper();
    private int initializationRetries = 10;
    private int initializationRetryDelay = 1000;
    private boolean sendingDataOnUpdate = true;
    private volatile CommunicatorDisconnected disconnectedEvent;
    private CommunicatorDisconnectedListener disconnectedListener = x -> disconnectedEvent = x;

    protected AbstractClient() {
        this.inbox = new DynamicByteBuffer();
        inbox.flip();
    }

    protected AbstractClient(Communicator communicator) {
        this();
        setCommunicator(communicator);
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

    public int getInitializationRetries() {
        return initializationRetries;
    }

    public void setInitializationRetries(int initializationRetries) {
        this.initializationRetries = initializationRetries;
    }

    public int getInitializationRetryDelay() {
        return initializationRetryDelay;
    }

    public void setInitializationRetryDelay(int initializationRetryDelay) {
        this.initializationRetryDelay = initializationRetryDelay;
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

    public void connect(Action onSuccess, ParameterizedAction<Throwable> onFail) {
        var communicator = getCommunicator();
        if (communicator instanceof Connectable) {
            ((Connectable) getCommunicator()).connect(onSuccess, onFail);
        }
    }

    public void disconnect() {
        var communicator = getCommunicator();
        if (communicator != null)
            communicator.disconnect();
    }

    public Communicator getCommunicator() {
        return this.communicator.getWrappedCommunicator();
    }

    public void setCommunicator(Communicator communicator) {
        var current = getCommunicator();

        if (current != communicator) {
            if (current != null)
                current.removeDisconnectedListener(disconnectedListener);

            disconnectedEvent = null;

            this.communicator.setWrappedCommunicator(communicator);

            if (communicator != null)
                communicator.addDisconnectedListener(disconnectedListener);
        }
    }

    public void initialize() throws InitializationException {
        var initializer = new InternalCommunicatorInitializer();
        configureInitialization(initializer);
        communicator.clearInitializationPhases();
        communicator.addInitializationPhases(initializer.getInitializationPhases());
        runInitializationPhases();
    }

    @Override
    public void update(float deltaTime) {
        if (communicator == null) {
            return;
        }

        if (communicator.isConnected()) {
            readMessages();
            if (sendingDataOnUpdate) {
                try {
                    communicator.sendOutgoing();
                } catch (IOException e) {
                    communicator.disconnect(e);
                }
            }
        } else if (disconnectedEvent != null) {
            try {
                // Read any final messages from the server
                readMessages();
                onDisconnected(disconnectedEvent.getCause());
            } finally {
                disconnectedEvent = null;
            }
        }
    }

    protected void readMessages() {
        handleMessages();

        boolean hasIncoming;
        try {
            hasIncoming = refreshInboxIfEmpty();
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

    private boolean refreshInboxIfEmpty() throws IOException {
        return inbox.remaining() > 0 || getCommunicator().readIncoming(inbox);
    }

    public boolean isLocalServer() {
        return communicator.unwrap() instanceof LocalCommunicator;
    }

    protected void runInitializationPhases() throws InitializationException {
        InitializationResult result;

        int retries = 0;
        do {

            result = communicator.runInitializationPhase(this::runInitializationPhase);
            if (result == InitializationResult.AWAITING_DATA) {

                if (retries == initializationRetries) {
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

                if (!triggerLocalServerUpdate()) {
                    try {
                        Thread.sleep(initializationRetryDelay);
                    } catch (InterruptedException e) {
                        throw new InitializationException("Loading thread interrupted");
                    }
                }
            } else {
                retries = 0;
            }

        } while (result != InitializationResult.FINISHED);

        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
            throw new InitializationException("Connection has been lost", e);
        }

        triggerLocalServerUpdate();
    }

    private boolean runInitializationPhase(Communicator communicator, InitializationPhase phase)
            throws InitializationException {
        if (phase instanceof ConsumerPhase) {
            try {
                return refreshInboxIfEmpty() && ((ConsumerPhase) phase).run(communicator, inbox);
            } catch (IOException e) {
                communicator.disconnect(e);
                throw new InitializationException("Connection has been lost", e);
            }
        } else if (phase instanceof ProducerPhase) {
            ((ProducerPhase) phase).run(communicator);
            return true;
        } else {
            throw new InitializationException("Unknown initialization phase");
        }
    }

    /**
     * Updates local server to trigger delivery of messages.
     *
     * @return True the server is local, false otherwise.
     */
    private boolean triggerLocalServerUpdate() {
        if (isLocalServer()) {
            ((LocalCommunicator) communicator.unwrap()).getLocalServer().update(0);
            return true;
        }

        return false;
    }

    protected abstract void configureInitialization(CommunicationInitializer initializer);

    protected abstract void onMessage(DataBuffer buffer);

    protected abstract void onDisconnected(Throwable cause);
}