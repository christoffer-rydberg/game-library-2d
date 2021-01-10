package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.AbstractNetworkCommunicator;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.SocketChannelConnectedHandler;
import com.gamelibrary2d.network.common.SocketChannelFailedConnectionHandler;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ClientSideCommunicator extends AbstractNetworkCommunicator {

    private final ParameterizedAction<CommunicationSteps> configureAuthentication;
    private final TcpConnectionSettings connectionSettings;

    private ClientSideCommunicator(
            TcpConnectionSettings connectionSettings,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {
        super(connectionSettings.getNetworkService(), 2, connectionSettings.isOwningNetworkService());
        this.connectionSettings = connectionSettings;
        this.configureAuthentication = configureAuthentication;
    }

    public static Future<Communicator> connect(TcpConnectionSettings connectionSettings) {
        return connect(connectionSettings, null);
    }

    public static Future<Communicator> connect(
            TcpConnectionSettings connectionSettings,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {

        var future = new CompletableFuture<Communicator>();

        var networkService = connectionSettings.getNetworkService();

        SocketChannelConnectedHandler onConnected = socketChannel -> {
            var communicator = new ClientSideCommunicator(connectionSettings, configureAuthentication);
            socketChannel.socket().setTcpNoDelay(true);
            communicator.setSocketChannel(socketChannel);
            networkService.connect(socketChannel, communicator, communicator::onSocketChannelDisconnected);
            future.complete(communicator);
        };

        SocketChannelFailedConnectionHandler onConnectionFailed =
                (endpoint, error) -> future.completeExceptionally(error);

        var networkServiceWasRunning = networkService.isRunning();
        try {
            networkService.start();
            networkService.connect(
                    connectionSettings.getHost(),
                    connectionSettings.getPort(),
                    onConnected,
                    onConnectionFailed);
        } catch (IOException e) {
            if (!networkServiceWasRunning) {
                try {
                    networkService.stop();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        if (configureAuthentication != null) {
            configureAuthentication.perform(steps);
        }
    }

    @Override
    public String getEndpoint() {
        return connectionSettings.getHost();
    }
}