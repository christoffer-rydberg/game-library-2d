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
    private final ParameterizedAction<CommunicationSteps> configureAuthentication;

    private LocalClientSideCommunicator(
            LocalServer localServer,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {
        super(1);
        this.localServer = localServer;
        this.configureAuthentication = configureAuthentication;
        serverSideCommunicator = new LocalServerSideCommunicator(
                this,
                localServer,
                configureAuthentication != null ? localServer::configureClientAuthentication : null
        );
    }

    public static Communicator connect(LocalServer localServer) {
        return connect(localServer, null);
    }

    public static Communicator connect(
            LocalServer localServer,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {

        var communicator = new LocalClientSideCommunicator(localServer, configureAuthentication);
        localServer.connectCommunicator(communicator.serverSideCommunicator);
        return communicator;
    }

    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        if (configureAuthentication != null) {
            configureAuthentication.perform(steps);
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

        LocalServerSideCommunicator(
                Communicator clientSideCommunicator,
                LocalServer server,
                ParameterizedAction<CommunicationSteps> configureAuthentication) {
            super(1);
            this.clientSideCommunicator = clientSideCommunicator;
            this.server = server;
            this.configureAuthentication = configureAuthentication;
        }

        @Override
        public String getEndpoint() {
            return "localhost";
        }

        @Override
        public void configureAuthentication(CommunicationSteps steps) {
            if (configureAuthentication != null) {
                configureAuthentication.perform(steps);
            }
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