package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
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

    private static void sendOutgoing(Communicator com) throws IOException {
        try {
            com.sendOutgoing();
        } catch (IOException e) {
            com.disconnect(e);
            throw e;
        }
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
    public void authenticate(CommunicationContext context) throws NetworkAuthenticationException {
        var communicator = getCommunicator();
        if (!communicator.isAuthenticated()) {
            var steps = new InternalCommunicationSteps();
            configureAuthentication(communicator, steps);
            try {
                runCommunicationSteps(context, communicator, new CommunicatorInitializer(steps.getAll()));
            } catch (IOException e) {
                throw new NetworkAuthenticationException("Authentication failed", e);
            } catch (InterruptedException e) {
                throw new NetworkAuthenticationException("Authentication failed", e);
            }
        }
    }

    @Override
    public void initialize(CommunicationContext context) throws NetworkInitializationException {
        var communicator = getCommunicator();
        var steps = new InternalCommunicationSteps();
        configureInitialization(steps);
        try {
            runCommunicationSteps(context, communicator, new CommunicatorInitializer(steps.getAll()));
        } catch (IOException e) {
            throw new NetworkInitializationException("Initialization failed", e);
        } catch (InterruptedException e) {
            throw new NetworkInitializationException("Initialization failed", e);
        }
    }

    public void authenticateAndInitialize() throws NetworkInitializationException, NetworkAuthenticationException {
        var context = new DefaultCommunicationContext();
        authenticate(context);
        initialize(context);
        initialized(context);
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

    private void runCommunicationSteps(CommunicationContext context, Communicator communicator, CommunicatorInitializer initializer)
            throws IOException, InterruptedException {
        CommunicatorInitializer.InitializationResult result;

        int retries = 0;
        do {
            result = initializer.runCommunicationStep(context, communicator, this::runCommunicationStep);
            if (result == CommunicatorInitializer.InitializationResult.AWAITING_DATA) {
                if (retries == getInitializationRetries()) {
                    throw new IOException("Reading server response timed out");
                }

                if (!communicator.isConnected()) {
                    throw new IOException("Connection has been lost");
                }

                ++retries;

                sendOutgoing(communicator);

                if (!triggerLocalServerUpdate(communicator, 0f)) {
                    Thread.sleep(getInitializationRetryDelay());
                }
            } else {
                retries = 0;
            }

        } while (result != CommunicatorInitializer.InitializationResult.FINISHED);

        sendOutgoing(communicator);
    }

    private boolean runCommunicationStep(CommunicationContext context, Communicator communicator, CommunicationStep step)
            throws IOException {
        if (step instanceof ConsumerStep) {
            try {
                return refreshInboxIfEmpty(communicator) && ((ConsumerStep) step).run(context, communicator, inbox);
            } catch (IOException e) {
                communicator.disconnect(e);
                throw (e);
            }
        } else if (step instanceof ProducerStep) {
            ((ProducerStep) step).run(context, communicator);
            return true;
        } else {
            throw new GameLibrary2DRuntimeException("Unknown communication step");
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