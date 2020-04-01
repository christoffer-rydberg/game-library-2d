package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.AbstractCommunicator;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.LocalServer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractLocalCommunicator extends AbstractCommunicator implements LocalCommunicator, Connectable {

    private final LocalServer localServer;
    private final LocalServerSideCommunicator serverSideCommunicator;

    protected AbstractLocalCommunicator(LocalServer localServer) {
        super(1, false);
        this.localServer = localServer;
        serverSideCommunicator = new LocalServerSideCommunicator(this, localServer);
    }

    private static void addIncoming(DataBuffer buffer, DataBuffer b) {
        int size = buffer.remaining();
        b.putBool(false);
        b.putInt(size);
        b.put(buffer);
    }

    @Override
    public Future<Void> connect() {
        if (setConnected()) {
            try {
                if (!localServer.isRunning()) {
                    localServer.start();
                }
                localServer.connectCommunicator(serverSideCommunicator);
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getEndpoint() {
        return "localhost";
    }

    @Override
    protected void send(DataBuffer buffer) throws IOException {
        serverSideCommunicator.addIncoming(b -> addIncoming(buffer, b), 0);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        if (localServer != null) {
            serverSideCommunicator.disconnect();
            localServer.update(0);
        }
    }

    @Override
    public LocalServer getLocalServer() {
        return localServer;
    }

    private static class LocalServerSideCommunicator extends AbstractCommunicator {
        private final Communicator serverSideCommunicator;
        private final ParameterizedAction<CommunicationSteps> configureAuthentication;

        LocalServerSideCommunicator(Communicator host, LocalServer server) {
            super(1, true);
            this.serverSideCommunicator = host;
            this.configureAuthentication = server::configureClientAuthentication;
        }

        @Override
        public String getEndpoint() {
            return "localhost";
        }

        @Override
        public void configureAuthentication(CommunicationSteps steps) {
            configureAuthentication.invoke(steps);
        }

        @Override
        protected void send(DataBuffer buffer) throws IOException {
            serverSideCommunicator.addIncoming(b -> AbstractLocalCommunicator.addIncoming(buffer, b), 0);
        }

        @Override
        protected void onDisconnected(Throwable cause) {
            serverSideCommunicator.disconnect();
        }
    }
}