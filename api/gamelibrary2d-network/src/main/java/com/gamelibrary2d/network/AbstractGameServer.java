package com.gamelibrary2d.network;

import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.server.AbstractSecureNetworkServer;

public abstract class AbstractGameServer<T extends GameLogic> extends AbstractSecureNetworkServer {
    private T gameLogic;
    private int streamRate;
    private int updateCounter;
    private ConnectedAction connected;
    private ConnectionFailedAction connectionFailed;
    private ConnectionLostAction connectionLost;
    private InitializedAction initialized;
    private AcceptConnectionFunc acceptConnection;

    public AbstractGameServer(int sslPort, int reconnectionPort) {
        super(sslPort, reconnectionPort);
    }

    public AbstractGameServer(int sslPort) {
        super(sslPort);
    }

    @Override
    public final boolean acceptConnection(String endpoint) {
        return acceptConnection == null || acceptConnection.invoke(endpoint);
    }

    @Override
    public final void onConnected(Communicator communicator) {
        if (connected != null) {
            connected.invoke(communicator);
        }
    }

    @Override
    public final void onConnectionFailed(String endpoint, Exception e) {
        if (connectionFailed != null) {
            connectionFailed.invoke(endpoint, e);
        }
    }

    @Override
    public final void onConnectionLost(Communicator communicator, boolean pending) {
        if (connectionLost != null) {
            connectionLost.invoke(communicator, pending);
        }
    }

    @Override
    public final void onInitialized(Communicator communicator) {
        if (initialized != null) {
            initialized.invoke(communicator);
        }
    }

    protected abstract void initialize(T gameLogic, ServerContextInitializer initializer);

    /**
     * Sends data to all clients with any changes since last time this method was
     * called. This method can be called after each update or every x update in case
     * you want to update the game logic more often than you stream data to the
     * clients (to reduce network load).
     */
    protected abstract void updateClients();

    public void start(T gameLogic, int ups, int streamRate) {
        this.gameLogic = gameLogic;

        var initializer = new ServerContextInitializer();
        initialize(gameLogic, initializer);
        connected = initializer.connectedAction;
        connectionFailed = initializer.connectionFailedAction;
        connectionLost = initializer.connectionLostAction;
        initialized = initializer.initializedAction;
        acceptConnection = initializer.acceptConnection;

        this.streamRate = streamRate;
        new UpdateLoop(this, ups).run();
    }

    @Override
    public void onUpdate(float deltaTime) {
        if (gameLogic.update(deltaTime)) {
            if (updateCounter == streamRate) {
                updateClients();
                updateCounter = 1;
            } else {
                ++updateCounter;
            }
        }
    }

    protected interface AcceptConnectionFunc {
        boolean invoke(String endpoint);
    }

    protected interface ConnectedAction {
        void invoke(Communicator communicator);
    }

    protected interface ConnectionFailedAction {
        void invoke(String endpoint, Exception e);
    }

    protected interface ConnectionLostAction {
        void invoke(Communicator communicator, boolean pending);
    }

    protected interface InitializedAction {
        void invoke(Communicator communicator);
    }

    protected static class ServerContextInitializer {
        private ConnectedAction connectedAction;
        private ConnectionFailedAction connectionFailedAction;
        private ConnectionLostAction connectionLostAction;
        private InitializedAction initializedAction;
        private AcceptConnectionFunc acceptConnection;

        public final void acceptConnection(AcceptConnectionFunc func) {
            acceptConnection = func;
        }

        public final void onConnected(ConnectedAction action) {
            this.connectedAction = action;
        }

        public final void onConnectionFailed(ConnectionFailedAction action) {
            this.connectionFailedAction = action;
        }

        public final void onConnectionLost(ConnectionLostAction action) {
            this.connectionLostAction = action;
        }

        public final void onInitialized(InitializedAction action) {
            this.initializedAction = action;
        }
    }
}