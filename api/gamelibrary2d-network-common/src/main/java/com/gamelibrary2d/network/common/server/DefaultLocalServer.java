package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public class DefaultLocalServer extends AbstractLocalServer {
    private final Func<Server, ServerContext> serverContextFactory;
    private ServerContext serverContext;

    public DefaultLocalServer(Func<Server, ServerContext> serverContextFactory) {
        this.serverContextFactory = serverContextFactory;
    }

    @Override
    protected void onStart() {
        serverContext = serverContextFactory.invoke(this);
        serverContext.start();
    }

    @Override
    protected void onStop() {
        serverContext.stop();
        super.onStop();
    }

    @Override
    public void configureClientAuthentication(CommunicationSteps steps) {
        serverContext.configureClientAuthentication(steps);
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
    protected void onMessage(Communicator communicator, DataBuffer buffer) {
        serverContext.onMessage(communicator, buffer);
    }
}
