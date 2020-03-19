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
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractNetworkServer extends InternalAbstractServer {
    private final Factory<Integer> communicatorIdFactory = () -> RandomInstance.get().nextInt();
    private final int reconnectionPort;
    private final List<ReconnectingCommunicator> reconnectingCommunicators;
    private final Set<Communicator> establishedConnections;
    private final CommunicationServer communicationServer;
    private final boolean ownsCommunicationServer;
    private int port;
    private float reconnectionTimeLimit = 60;
    private ServerSocketChannelRegistration registration;
    private ServerSocketChannelRegistration reconnectionRegistration;

    protected AbstractNetworkServer(int port) {
        this.communicationServer = new CommunicationServer();
        this.ownsCommunicationServer = true;
        this.port = port;
        reconnectionPort = -1;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    protected AbstractNetworkServer(int port, CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
        this.ownsCommunicationServer = false;
        this.port = port;
        reconnectionPort = -1;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * Creates a secure server that authenticates clients over an SSL connection,
     * and then lets them reconnect to a plain port.
     */
    protected AbstractNetworkServer(int sslPort, int reconnectionPort) {
        this.communicationServer = new CommunicationServer();
        this.ownsCommunicationServer = true;
        this.port = sslPort;
        this.reconnectionPort = reconnectionPort;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    /**
     * Creates a secure server that authenticates clients over an SSL connection,
     * and then lets them reconnect to a plain port.
     */
    protected AbstractNetworkServer(int sslPort, int reconnectionPort, CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
        this.ownsCommunicationServer = false;
        this.port = sslPort;
        this.reconnectionPort = reconnectionPort;
        reconnectingCommunicators = new ArrayList<>();
        establishedConnections = new HashSet<>();
    }

    private void deregisterConnectionListener(ServerSocketChannelRegistration registration) throws IOException {
        communicationServer.deregisterConnectionListener(registration);
    }

    private ServerSocketChannelRegistration registerConnectionListener(int port)
            throws IOException {
        return communicationServer.registerConnectionListener("localhost", port, this::onConnected,
                (x, y) -> invokeLater(() -> onConnectionFailed(x, y)));
    }

    private void onConnected(SocketChannel channel) {
        invokeLater(() -> {
            var endpoint = channel.socket().getInetAddress().getHostAddress();
            if (!acceptConnection(endpoint)) {
                communicationServer.disconnect(channel);
                onConnectionFailed(endpoint, new IOException("Connection refused by server"));
            } else {
                var communicator = new ServerSideCommunicator(communicationServer, channel, this::onConfigureAuthentication);
                try {
                    // Disable Nagle's algorithm
                    channel.socket().setTcpNoDelay(true);
                    addCommunicator(new CommunicatorWrapper(communicator));
                } catch (SocketException | InitializationException e) {
                    communicationServer.disconnect(channel);
                    onConnectionFailed(endpoint, e);
                }
            }
        });
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

    public boolean isListeningForConnections() {
        return registration != null;
    }

    public void startConnectionListener() throws IOException {
        communicationServer.start();
        registration = registerConnectionListener(port);
        if (reconnectionPort >= 0) {
            reconnectionRegistration = registerConnectionListener(reconnectionPort);
        }
    }

    public void stopConnectionListener() throws IOException {
        if (registration != null) {
            deregisterConnectionListener(registration);
            registration = null;
        }
        if (reconnectionRegistration != null) {
            deregisterConnectionListener(reconnectionRegistration);
            reconnectionRegistration = null;
        }
    }

    public void stop() throws IOException {
        stopConnectionListener();
        if (ownsCommunicationServer) {
            try {
                communicationServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private void configureInitializationWhenAuthenticated(CommunicatorWrapper communicator) {
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
            communicatorWrapper.setAuthenticated();
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
        configureAuthentication(communicator,
                c -> onAuthenticatedWithReconnect((CommunicatorWrapper) c, reconnectionPort));
    }

    private void configureAuthentication(CommunicatorWrapper communicator, ProducerPhase onAuthenticated) {
        var initializer = new InternalCommunicatorInitializer();
        initializer.add(new IdentityProducer(communicatorIdFactory));
        initializer.add((ProducerPhase) this::onConnected);
        communicator.configureAuthentication(initializer);
        initializer.add(onAuthenticated);
        communicator.addInitializationPhases(initializer.getInitializationPhases());
    }

    private void onAuthenticatedWithoutReconnect(Communicator communicator) {
        communicator.setAuthenticated();
        communicator.getOutgoing().putBool(false);
        configureInitializationWhenAuthenticated((CommunicatorWrapper) communicator);
    }

    private void onAuthenticatedWithReconnect(CommunicatorWrapper communicator, int reconnectionPort)
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
        reconnectingCommunicators.add(new ReconnectingCommunicator(communicator, reconnectionTimeLimit));
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

    protected abstract boolean acceptConnection(String endpoint);

    protected abstract void onConnectionFailed(String endpoint, Exception e);

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