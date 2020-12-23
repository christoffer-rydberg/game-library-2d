package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.common.concurrent.NotHandledException;
import com.gamelibrary2d.common.concurrent.ResultHandlingFuture;
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
import com.gamelibrary2d.network.common.security.ClientHandshake;
import com.gamelibrary2d.network.common.server.DefaultLocalServer;
import com.gamelibrary2d.network.common.server.DefaultNetworkServer;
import com.gamelibrary2d.network.common.server.LocalServer;
import com.gamelibrary2d.network.common.server.Server;

import java.io.IOException;
import java.security.KeyPair;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ServerManager {
    private final KeyPair keyPair;

    private Thread serverThread;

    public ServerManager(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

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
                LocalClientSideCommunicator.connect(server));
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
        var server = new DefaultNetworkServer("localhost", port, s -> new DemoGameServer(s, keyPair));

        try {
            server.start();
            server.listenForConnections(true);
            return new ServerResult(server, () -> {
                var future = connectToServer("localhost", port, localUpdPort);
                return new ResultHandlingFuture<>(
                        future,
                        com -> {
                            com.addDisconnectedListener(e -> stopServer(server));
                            return com;
                        },
                        e -> {
                            stopServer(server);
                            throw new NotHandledException();
                        }
                );
            });
        } catch (IOException e) {
            return new ServerResult(server, () -> CompletableFuture.failedFuture(e));
        }
    }

    private void authenticate(CommunicationSteps steps, int localUpdPort) {
        var clientHandshake = new ClientHandshake();
        clientHandshake.configure(steps);
        steps.add((__, com) -> com.writeEncrypted(b -> Write.textWithSizeHeader("serverPassword123", b)));
        steps.add((__, com) -> {
            ((NetworkCommunicator) com).enableUdp(ConnectionType.READ, localUpdPort, 0);
            com.writeEncrypted(b -> b.putInt(localUpdPort));
        });
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
