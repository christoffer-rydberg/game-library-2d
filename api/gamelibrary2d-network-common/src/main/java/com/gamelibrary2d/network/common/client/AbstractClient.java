package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.TcpConnector;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.*;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractClient {
    private final DataBuffer inbox;
    private boolean sendingDataOnUpdate = true;

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

    public void initialize() throws InitializationException {
        var initializer = new InternalCommunicatorInitializer();
        var communicatorWrapper = new CommunicatorWrapper(getCommunicator());
        configureInitialization(initializer);
        communicatorWrapper.clearInitializationPhases();
        communicatorWrapper.addInitializationPhases(initializer.getInitializationPhases());
        runInitializationPhases(communicatorWrapper);
    }

    public void update() {
        var communicator = getCommunicator();

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

    private void runInitializationPhases(CommunicatorWrapper communicator) throws InitializationException {
        CommunicatorWrapper.InitializationResult result;

        int retries = 0;
        do {

            result = communicator.runInitializationPhase(this::runInitializationPhase);
            if (result == CommunicatorWrapper.InitializationResult.AWAITING_DATA) {

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

        } while (result != CommunicatorWrapper.InitializationResult.FINISHED);

        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
            throw new InitializationException("Connection has been lost", e);
        }

        triggerLocalServerUpdate(communicator);
    }

    private boolean runInitializationPhase(Communicator communicator, InitializationPhase phase)
            throws InitializationException {
        if (phase instanceof ConsumerPhase) {
            try {
                return refreshInboxIfEmpty(communicator) && ((ConsumerPhase) phase).run(communicator, inbox);
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
    private boolean triggerLocalServerUpdate(CommunicatorWrapper communicator) {
        if (communicator.unwrap() instanceof LocalCommunicator) {
            ((LocalCommunicator) communicator.unwrap()).getLocalServer().update(0);
            return true;
        }

        return false;
    }

    /**
     * The max number of retries for each initialization step.
     */
    protected int getInitializationRetries() {
        return 10;
    }

    /**
     * The delay between retries of initialization steps in milliseconds.
     */
    protected int getInitializationRetryDelay() {
        return 1000;
    }

    public void authenticate() throws InitializationException {
        var communicator = getCommunicator();
        if (!communicator.isAuthenticated()) {
            var initializer = new InternalCommunicatorInitializer();
            var communicatorWrapper = new CommunicatorWrapper(communicator);
            configureAuthentication(initializer, communicatorWrapper);
            communicatorWrapper.clearInitializationPhases();
            communicatorWrapper.addInitializationPhases(initializer.getInitializationPhases());
            runInitializationPhases(communicatorWrapper);
        }
    }

    private void configureInitialization(CommunicationInitializer initializer) {
        onConfigureInitialization(initializer);
    }

    private void configureAuthentication(CommunicationInitializer initializer, CommunicatorWrapper communicator) {
        initializer.add(x -> initializeConnection(x, false));
        initializer.add(new IdentityConsumer());
        communicator.configureAuthentication(initializer);
        initializer.add(this::onAuthenticated);
    }

    private void initializeConnection(Communicator communicator, boolean isReconnect) {
        communicator.getOutgoing().putBool(isReconnect);
    }

    private boolean onAuthenticated(Communicator communicator, DataBuffer inbox) throws InitializationException {
        var reconnect = inbox.getBool();

        if (reconnect) {
            int port = inbox.getInt();
            var wrappedCommunicator = communicator.unwrap();
            if (wrappedCommunicator instanceof TcpConnector) {
                var tcpConnector = (TcpConnector) wrappedCommunicator;
                tcpConnector.setTcpConnectionSettings(
                        new TcpConnectionSettings(wrappedCommunicator.getEndpoint(), port, false));
            }
        }

        communicator.setAuthenticated();

        if (reconnect) {
            var wrappedCommunicator = communicator.unwrap();
            if (wrappedCommunicator instanceof Reconnectable) {
                try {
                    ((Reconnectable) wrappedCommunicator).reconnect().get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new InitializationException(e);
                }
            }
            initializeConnection(communicator, true);
            var identityProducer = new IdentityProducer(communicator::getId);
            identityProducer.run(communicator);
        }

        return true;
    }

    public abstract Communicator getCommunicator();

    protected abstract void onConfigureInitialization(CommunicationInitializer initializer);

    protected abstract void onMessage(DataBuffer buffer);
}