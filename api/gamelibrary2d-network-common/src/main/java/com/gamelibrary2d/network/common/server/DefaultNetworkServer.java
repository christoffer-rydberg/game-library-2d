package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.NetworkService;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;

public final class DefaultNetworkServer extends AbstractNetworkServer {
    private final ServerContext serverContext;

    public DefaultNetworkServer(int port, Func<Server, ServerContext> serverContextFactory) {
        super(port);
        this.serverContext = serverContextFactory.invoke(this);
    }

    public DefaultNetworkServer(int port, NetworkService networkService, Func<Server, ServerContext> serverContextFactory) {
        super(port, networkService);
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
    protected void onClientAuthenticated(CommunicationContext context, Communicator communicator) {
        serverContext.onClientAuthenticated(context, communicator);
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
    protected void onClientInitialized(CommunicationContext context, Communicator communicator) {
        serverContext.onClientInitialized(context, communicator);
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        serverContext.onDisconnected(communicator, pending);
    }

    @Override
    protected void onStart() throws IOException {
        super.onStart();
        serverContext.start();
    }

    @Override
    protected void onStop() throws IOException, InterruptedException {
        serverContext.stop();
        super.onStop();
    }

    @Override
    protected void onMessage(Communicator communicator, DataBuffer buffer) {
        serverContext.onMessage(communicator, buffer);
    }
}