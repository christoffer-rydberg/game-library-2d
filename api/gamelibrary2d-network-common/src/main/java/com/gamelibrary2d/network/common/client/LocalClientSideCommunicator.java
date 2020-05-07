package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.AbstractCommunicator;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.LocalServer;

import java.io.IOException;

public class LocalClientSideCommunicator extends AbstractCommunicator implements LocalCommunicator {

    private final LocalServer localServer;
    private final LocalServerSideCommunicator serverSideCommunicator;
    private ParameterizedAction<CommunicationSteps> configureAuthentication;

    private LocalClientSideCommunicator(LocalServer localServer) {
        super(1);
        this.localServer = localServer;
        serverSideCommunicator = new LocalServerSideCommunicator(this, localServer);
    }

    public static Communicator connect(LocalServer localServer) {
        return connect(localServer, null);
    }

    public static Communicator connect(LocalServer localServer, ParameterizedAction<CommunicationSteps> configureAuthentication) {
        var communicator = new LocalClientSideCommunicator(localServer);
        communicator.configureAuthentication = configureAuthentication;
        localServer.connectCommunicator(communicator.serverSideCommunicator);
        return communicator;
    }

    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        if (configureAuthentication != null) {
            configureAuthentication.invoke(steps);
        }
    }

    @Override
    public String getEndpoint() {
        return "localhost";
    }

    @Override
    protected void send(DataBuffer buffer) throws IOException {
        serverSideCommunicator.addIncoming(0, b -> Util.addIncoming(buffer, b));
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        serverSideCommunicator.disconnect();
    }

    @Override
    public LocalServer getLocalServer() {
        return localServer;
    }

    private static class LocalServerSideCommunicator extends AbstractCommunicator implements LocalCommunicator {
        private final Communicator clientSideCommunicator;
        private final LocalServer server;
        private final ParameterizedAction<CommunicationSteps> configureAuthentication;

        LocalServerSideCommunicator(Communicator clientSideCommunicator, LocalServer server) {
            super(1);
            this.clientSideCommunicator = clientSideCommunicator;
            this.server = server;
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
            clientSideCommunicator.addIncoming(0, b -> Util.addIncoming(buffer, b));
        }

        @Override
        protected void onDisconnected(Throwable cause) {
            clientSideCommunicator.disconnect();
        }

        @Override
        public LocalServer getLocalServer() {
            return server;
        }
    }

    private static class Util {
        private static void addIncoming(DataBuffer buffer, DataBuffer b) {
            int size = buffer.remaining();
            b.putBool(false);
            b.putInt(size);
            b.put(buffer);
        }
    }
}