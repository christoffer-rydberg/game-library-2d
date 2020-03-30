package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;

public final class DefaultNetworkServer extends AbstractNetworkServer {
    private final ServerContext serverContext;

    public DefaultNetworkServer(int port, Func<Server, ServerContext> serverContextFactory) {
        super(port);
        this.serverContext = serverContextFactory.invoke(this);
    }

    public DefaultNetworkServer(int port, CommunicationServer communicationServer, Func<Server, ServerContext> serverContextFactory) {
        super(port, communicationServer);
        this.serverContext = serverContextFactory.invoke(this);
    }

    @Override
    protected void configureClientAuthentication(CommunicationSteps steps) {
        serverContext.configureClientAuthentication(steps);
    }

    @Override
    protected boolean acceptConnection(String endpoint) {
        return serverContext.acceptConnection(endpoint);
    }

    @Override
    protected void onConnectionFailed(String endpoint, Exception e) {
        serverContext.onConnectionFailed(endpoint, e);
    }

    @Override
    protected void onClientAuthenticated(Communicator communicator) {
        serverContext.onClientAuthenticated(communicator);
    }

    @Override
    protected void onUpdate(float deltaTime) {
        serverContext.update(deltaTime);
    }

    @Override
    protected void onConnected(Communicator communicator) {
        serverContext.onConnected(communicator);
    }

    @Override
    protected void configureClientInitialization(CommunicationSteps steps) {
        serverContext.configureClientInitialization(steps);
    }

    @Override
    protected void onClientInitialized(Communicator communicator) {
        serverContext.onClientInitialized(communicator);
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        serverContext.onDisconnected(communicator, pending);
    }

    @Override
    public void stop() throws IOException {
        serverContext.stop();
        super.stop();
    }

    @Override
    protected void onMessage(Communicator communicator, DataBuffer buffer) {
        serverContext.onMessage(communicator, buffer);
    }
}