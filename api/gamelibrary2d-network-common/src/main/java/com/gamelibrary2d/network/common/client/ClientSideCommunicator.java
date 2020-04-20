package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.*;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ClientSideCommunicator extends AbstractNetworkCommunicator implements UdpReceiver {

    private final ParameterizedAction<CommunicationSteps> configureAuthentication;
    private final TcpConnectionSettings tcpSettings;

    private ClientSideCommunicator(
            TcpConnectionSettings tcpSettings,
            NetworkService networkService,
            ParameterizedAction<CommunicationSteps> configureAuthentication,
            boolean ownsNetworkService) {
        super(networkService, 2, ownsNetworkService);
        this.tcpSettings = tcpSettings;
        this.configureAuthentication = configureAuthentication;
    }

    public static Future<Communicator> connect(TcpConnectionSettings tcpSettings) {
        return connect(tcpSettings, new NetworkService(), true, null);
    }

    public static Future<Communicator> connect(
            TcpConnectionSettings tcpSettings,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {
        return connect(tcpSettings, new NetworkService(), true, configureAuthentication);
    }

    public static Future<Communicator> connect(
            TcpConnectionSettings tcpSettings,
            NetworkService networkService) {
        return connect(tcpSettings, networkService, false, null);
    }

    public static Future<Communicator> connect(
            TcpConnectionSettings tcpSettings,
            NetworkService networkService,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {
        return connect(tcpSettings, networkService, false, configureAuthentication);
    }

    private static Future<Communicator> connect(
            TcpConnectionSettings tcpSettings,
            NetworkService networkService,
            boolean ownsNetworkService,
            ParameterizedAction<CommunicationSteps> configureAuthentication) {

        var future = new CompletableFuture<Communicator>();

        SocketChannelConnectedHandler onConnected = socketChannel -> {
            var communicator = new ClientSideCommunicator(
                    tcpSettings, networkService, configureAuthentication, ownsNetworkService);
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
            networkService.connect(tcpSettings.getHost(), tcpSettings.getPort(), onConnected, onConnectionFailed);
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
            configureAuthentication.invoke(steps);
        }
    }

    @Override
    public String getEndpoint() {
        return tcpSettings.getHost();
    }

    @Override
    public void connectUdpReceiver(int localPort) throws IOException {
        super.connectUdp(ConnectionOperations.READ, localPort, 0);
    }

    @Override
    public void disconnectUdpReceiver() {
        disconnectUdp();
    }
}