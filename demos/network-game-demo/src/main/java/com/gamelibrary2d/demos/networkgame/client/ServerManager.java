package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.Write;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.demos.networkgame.server.DemoGameServer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.ConnectionType;
import com.gamelibrary2d.network.common.NetworkCommunicator;
import com.gamelibrary2d.network.common.client.ClientSideCommunicator;
import com.gamelibrary2d.network.common.client.LocalClientSideCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.DefaultLocalServer;
import com.gamelibrary2d.network.common.server.DefaultNetworkServer;
import com.gamelibrary2d.network.common.server.LocalServer;
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

    public Future<Communicator> hostNetworkServer(int port, int localUdpPort) {
        return startServer(() -> createNetworkServer(port, localUdpPort));
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

    public Future<Communicator> connectToServer(String ip, int port, int localUpdPort) {
        return ClientSideCommunicator.connect(
                new TcpConnectionSettings(ip, port),
                steps -> authenticate(steps, localUpdPort));
    }

    private Future<Communicator> connectToLocalServer(LocalServer server) {
        return CompletableFuture.completedFuture(
                LocalClientSideCommunicator.connect(server, this::authenticate)
        );
    }

    private ServerResult createLocalServer() {
        var localServer = new DefaultLocalServer(DemoGameServer::new);
        try {
            localServer.start();
            return new ServerResult(localServer, () -> connectToLocalServer(localServer));
        } catch (IOException e) {
            return new ServerResult(localServer, () -> CompletableFuture.failedFuture(e));
        }
    }

    private ServerResult createNetworkServer(int port, int localUpdPort) {
        var server = new DefaultNetworkServer(port, DemoGameServer::new);
        try {
            server.start();
            server.listenForConnections(true);
            return new ServerResult(server, () -> connectToServer("localhost", port, localUpdPort));
        } catch (IOException e) {
            return new ServerResult(server, () -> CompletableFuture.failedFuture(e));
        }
    }

    private void authenticate(CommunicationSteps steps) {
        steps.add((context, communicator) ->
                Write.textWithSizeHeader("serverPassword123", StandardCharsets.UTF_8, communicator.getOutgoing()));
    }

    private void authenticate(CommunicationSteps steps, int localUdpPort) {
        authenticate(steps);
        steps.add((__, com) -> initializeUdp(com, localUdpPort));
    }

    private void initializeUdp(Communicator communicator, int port) throws IOException {
        ((NetworkCommunicator) communicator).enableUdp(ConnectionType.READ, port, 0);
        communicator.getOutgoing().putInt(port);
    }

    private Future<Communicator> startServer(Factory<ServerResult> serverFactory) {
        var futureCommunicator = new CompletableFuture<Communicator>();
        serverThread = new Thread(() -> {
            var serverResult = serverFactory.create();

            try {
                futureCommunicator.complete(serverResult.communicatorFactory.create().get());
            } catch (Exception e) {
                futureCommunicator.completeExceptionally(e);
                stopServer(serverResult.server);
                return;
            }

            new UpdateLoop(serverResult.server::update, DemoGameServer.UPDATES_PER_SECOND).run();

            stopServer(serverResult.server);
        });

        serverThread.start();

        return futureCommunicator;
    }

    private void stopServer(Server server) {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ServerResult {
        private final Server server;
        private final Factory<Future<Communicator>> communicatorFactory;

        ServerResult(Server server, Factory<Future<Communicator>> communicatorFactory) {
            this.server = server;
            this.communicatorFactory = communicatorFactory;
        }
    }
}
