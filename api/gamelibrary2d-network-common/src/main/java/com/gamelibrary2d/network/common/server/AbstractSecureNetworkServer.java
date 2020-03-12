package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.ServerSocketChannelRegistration;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.IdentityConsumer;
import com.gamelibrary2d.network.common.initialization.IdentityProducer;
import com.gamelibrary2d.network.common.initialization.ProducerPhase;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractSecureNetworkServer extends InternalAbstractNetworkServer {

    private final Factory<Integer> communicatorIdFactory = () -> RandomInstance.get().nextInt();
    private final int reconnectionPort;
    private final List<ReconnectingCommunicator> reconnectingCommunicators;
    private final Set<Communicator> establishedConnections;
    private int port;
    private float reconnectionTimeLimit = 60;
    private ServerSocketChannelRegistration registration;

    private ServerSocketChannelRegistration reconnectionRegistration;

    /**
     * Creates a secure server using SSL for all TCP traffic.
     */
    protected AbstractSecureNetworkServer(int sslPort) {
        this.port = sslPort;
        reconnectionPort = -1;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * Creates a secure server using SSL for all TCP traffic.
     */
    protected AbstractSecureNetworkServer(int sslPort, CommunicationServer communicationServer) {
        super(communicationServer);
        this.port = sslPort;
        reconnectionPort = -1;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * Creates a secure server that authenticates clients over an SSL connection,
     * and then lets them reconnect to a plain port.
     */
    protected AbstractSecureNetworkServer(int sslPort, int reconnectionPort) {
        this.port = sslPort;
        this.reconnectionPort = reconnectionPort;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * Creates a secure server that authenticates clients over an SSL connection,
     * and then lets them reconnect to a plain port.
     */
    protected AbstractSecureNetworkServer(int sslPort, int reconnectionPort, CommunicationServer communicationServer) {
        super(communicationServer);
        this.port = sslPort;
        this.reconnectionPort = reconnectionPort;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * The maximum time limit in seconds for reconnection attempts.
     */
    public float getReconnectionTimeLimit() {
        return reconnectionTimeLimit;
    }

    /**
     * Sets the {@link #getReconnectionTimeLimit() reconnection time limit}.
     */
    public void setReconnectionTimeLimit(float reconnectionTimeLimit) {
        this.reconnectionTimeLimit = reconnectionTimeLimit;
    }

    public void start() throws IOException {
        super.startInternal();
    }

    public void stop() throws IOException {
        stopConnectionServer();
        super.stopInternal();
    }

    public void startConnectionServer() throws IOException {
        if (registration == null) {
            start();
            registration = registerConnectionListener("localhost", port, true);
            if (reconnectionPort >= 0) {
                try {
                    reconnectionRegistration = registerConnectionListener("localhost", reconnectionPort, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopConnectionServer() throws IOException {
        if (registration != null) {
            deregisterConnectionListener(registration);
            if (reconnectionRegistration != null) {
                try {
                    deregisterConnectionListener(reconnectionRegistration);
                    reconnectionRegistration = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            registration = null;
        }
    }

    @Override
    protected final void onDisconnected(Communicator communicator, boolean pending) {
        boolean establishedConnection = establishedConnections.remove(communicator);

        if (!pending || establishedConnection) {
            onConnectionLost(communicator, pending);
        } else {
            var error = new IOException("Failed to establish connection");
            onConnectionFailed(communicator.getEndpoint(), error);
        }
    }

    @Override
    protected void configureInitialization(CommunicatorWrapper communicator) {
        var initializer = new InternalCommunicatorInitializer();
        initializer.add(this::initializeConnection);
        communicator.addInitializationPhases(initializer.getInitializationPhases());
    }

    private void configureInitializationWhenAuthenticated(CommunicatorWrapper communicator)
            throws InitializationException {
        super.configureInitialization(communicator);
    }

    private boolean initializeConnection(Communicator communicator, DataBuffer inbox) throws InitializationException {
        var reconnected = inbox.getBool();
        if (reconnected) {
            new IdentityConsumer().run(communicator, inbox);
            onConnected(communicator, true);
        } else {
            onConnected(communicator, false);
        }
        return true;
    }

    private void onConnected(Communicator communicator, boolean reconnect) throws InitializationException {
        CommunicatorWrapper communicatorWrapper;

        if (reconnect) {
            ReconnectingCommunicator reconnecting = reconnectingCommunicators.stream()
                    .filter(x -> x.getCommunicator().getId() == communicator.getId()).findFirst().orElse(null);

            if (reconnecting == null)
                throw new InitializationException("Reconnection failed");

            reconnectingCommunicators.remove(reconnecting);

            // The reconnected communicator has not yet been exposed outside this
            // abstraction. By reusing the same instance that was temporarily disconnected,
            // derived classes does not need to be confused by a sudden change of
            // reference. This way, the reconnection flow remains an implementation detail.
            communicatorWrapper = reconnecting.getCommunicator();
            replacePendingCommunicatorWrapper(communicatorWrapper, (CommunicatorWrapper) communicator);
            setAuthenticated(communicatorWrapper);
        } else {
            communicatorWrapper = (CommunicatorWrapper) communicator;
        }

        establishedConnections.add(communicatorWrapper);
        onConnectionEstablished(communicatorWrapper);

        if (reconnect) {
            runInitializationPhases(communicatorWrapper);
        }
    }

    private void onConnectionEstablished(CommunicatorWrapper communicator) {
        try {
            if (reconnectionRegistration != null) {
                if (!communicator.isAuthenticated()) {
                    configureAuthentication(communicator, reconnectionRegistration.getLocalPort());
                } else {
                    configureInitializationWhenAuthenticated(communicator);
                }
            } else {
                configureAuthenticationAndInitialization(communicator);
            }

        } catch (Exception e) {
            communicator.disconnect(e);
        }
    }

    private void replacePendingCommunicatorWrapper(CommunicatorWrapper newWrapper, CommunicatorWrapper oldWrapper) {
        oldWrapper.removeDisconnectedListener(disconnectedEventListener);
        newWrapper.addDisconnectedListener(disconnectedEventListener);
        newWrapper.setWrappedCommunicator(oldWrapper.getWrappedCommunicator());
        pendingCommunicators.set(pendingCommunicators.indexOf(oldWrapper), newWrapper);
    }

    private void configureAuthenticationAndInitialization(CommunicatorWrapper communicator) {
        configureAuthentication(communicator, this::onAuthenticatedWithoutReconnect);
    }

    private void configureAuthentication(CommunicatorWrapper communicator, int reconnectionPort) {
        configureAuthentication(communicator, x -> onAuthenticatedWithReconnect(x, reconnectionPort));
    }

    private void configureAuthentication(CommunicatorWrapper communicator, ProducerPhase onAuthenticated) {
        var initializer = new InternalCommunicatorInitializer();
        initializer.add(new IdentityProducer(communicatorIdFactory));
        initializer.add(c -> onConnected(c));
        onConfigureAuthentication(initializer);
        initializer.add(onAuthenticated);
        communicator.addInitializationPhases(initializer.getInitializationPhases());
    }

    private void onAuthenticatedWithoutReconnect(Communicator communicator) throws InitializationException {
        setAuthenticated(communicator);
        communicator.getOutgoing().putBool(false);
        configureInitializationWhenAuthenticated((CommunicatorWrapper) communicator);
    }

    private void onAuthenticatedWithReconnect(Communicator communicator, int reconnectionPort)
            throws InitializationException {
        communicator.getOutgoing().putBool(true);
        communicator.getOutgoing().putInt(reconnectionPort);
        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
            throw new InitializationException(e);
        }
        pendingCommunicators.remove(communicator);
        communicator.removeDisconnectedListener(disconnectedEventListener);
        reconnectingCommunicators
                .add(new ReconnectingCommunicator((CommunicatorWrapper) communicator, reconnectionTimeLimit));
    }

    private void setAuthenticated(Communicator communicator) {
        communicator.setAuthenticated();
    }

    @Override
    public void update(float deltaTime) {
        updateReconnecting(deltaTime);
        super.update(deltaTime);
    }

    private void updateReconnecting(float deltaTime) {
        for (int i = reconnectingCommunicators.size() - 1; i >= 0; --i) {
            var rc = reconnectingCommunicators.get(i);
            rc.update(deltaTime);
            if (rc.timeExpired()) {
                reconnectingCommunicators.remove(i);
                onDisconnected(rc.getCommunicator(), false);
            }
        }
    }

    protected abstract void onConnected(Communicator communicator);

    protected abstract void onConnectionLost(Communicator communicator, boolean pending);

    protected abstract void onConfigureAuthentication(CommunicationInitializer initializer);

    private static class ReconnectingCommunicator {

        private final CommunicatorWrapper communicator;

        private float timer;

        ReconnectingCommunicator(CommunicatorWrapper communicator, float reconnectionTimeLimit) {
            this.communicator = communicator;
            timer = reconnectionTimeLimit;
        }

        public CommunicatorWrapper getCommunicator() {
            return communicator;
        }

        public void update(float deltaTime) {
            timer -= deltaTime;
        }

        boolean timeExpired() {
            return timer <= 0;
        }
    }
}