package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.Write;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.demos.networkgame.server.DemoGameServer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.Connectable;
import com.gamelibrary2d.network.common.client.DefaultClientSideCommunicator;
import com.gamelibrary2d.network.common.client.DefaultLocalCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.DefaultLocalServer;
import com.gamelibrary2d.network.common.server.DefaultNetworkServer;
import com.gamelibrary2d.network.common.server.Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ServerManager {
    private Thread serverThread;

    public Future<Communicator> hostLocalServer() {
        return startServer(this::createLocalServer);
    }

    public Future<Communicator> hostNetworkServer(int port) {
        return startServer(() -> createNetworkServer(port));
    }

    public Future<Communicator> joinNetworkServer(String ip, int port) {
        var futureCommunicator = new CompletableFuture<Communicator>();
        var communicator = createNetworkCommunicator(ip, port);
        connectCommunicator(communicator);
        futureCommunicator.complete(communicator);
        return futureCommunicator;
    }

    public void stopHostedServer() {
        if (serverThread != null) {
            try {
                serverThread.interrupt();
                serverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectCommunicator(Communicator communicator) {
        if (communicator instanceof Connectable) {
            ((Connectable) communicator).connect();
        }
    }

    private Future<Communicator> startServer(Factory<ServerResult> serverFactory) {
        var futureCommunicator = new CompletableFuture<Communicator>();
        serverThread = new Thread(() -> {
            var serverResult = serverFactory.create();
            connectCommunicator(serverResult.communicator);
            futureCommunicator.complete(serverResult.communicator);
            new UpdateLoop(serverResult.server::update, DemoGameServer.UPDATES_PER_SECOND).run();

            try {
                serverResult.server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        return futureCommunicator;
    }

    private void authenticate(CommunicationSteps steps) {
        steps.add(communicator -> {
            var outgoing = communicator.getOutgoing();
            Write.textWithSizeHeader("serverPassword123", StandardCharsets.UTF_8, outgoing);
        });
    }

    private ServerResult createLocalServer() {
        var localServer = new DefaultLocalServer(DemoGameServer::new);
        localServer.initialize();

        var communicator = new DefaultLocalCommunicator(localServer);
        communicator.onConfigureAuthentication(this::authenticate);

        return new ServerResult(localServer, communicator);
    }

    private Communicator createNetworkCommunicator(String ip, int port) {
        var communicator = new DefaultClientSideCommunicator(
                new TcpConnectionSettings(ip, port));
        communicator.onConfigureAuthentication(this::authenticate);
        return communicator;
    }

    private ServerResult createNetworkServer(int port) {
        try {
            var server = new DefaultNetworkServer(port, DemoGameServer::new);
            server.listenForConnections(true);

            var communicator = createNetworkCommunicator("localhost", port);

            return new ServerResult(server, communicator);
        } catch (IOException e) {
            throw new GameLibrary2DRuntimeException("Failed to create network server", e);
        }
    }

    private static class ServerResult {
        private final Server server;
        private final Communicator communicator;

        ServerResult(Server server, Communicator communicator) {
            this.server = server;
            this.communicator = communicator;
        }
    }
}
