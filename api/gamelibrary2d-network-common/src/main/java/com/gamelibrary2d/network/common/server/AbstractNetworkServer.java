package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.ServerSocketChannelRegistration;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public abstract class AbstractNetworkServer extends InternalAbstractServer {
    private final CommunicationServer communicationServer;
    private final boolean ownsCommunicationServer;

    private int port;
    private ServerSocketChannelRegistration registration;

    private AbstractNetworkServer(int port, CommunicationServer communicationServer, boolean ownsCommunicationServer) {
        this.port = port;
        this.communicationServer = communicationServer;
        this.ownsCommunicationServer = ownsCommunicationServer;
    }

    protected AbstractNetworkServer(int port) {
        this(port, new CommunicationServer(), true);
    }

    protected AbstractNetworkServer(int port, CommunicationServer communicationServer) {
        this(port, communicationServer, false);
    }

    private void onConnected(SocketChannel channel) {
        invokeLater(() -> {
            var endpoint = channel.socket().getInetAddress().getHostAddress();
            if (!acceptConnection(endpoint)) {
                communicationServer.disconnect(channel);
                onConnectionFailed(endpoint, new IOException("Connection refused by server"));
            } else {
                try {
                    // Disable Nagle's algorithm
                    channel.socket().setTcpNoDelay(true);
                    addConnectedCommunicator(new ServerSideCommunicator(communicationServer, channel, this::configureClientAuthentication));
                } catch (IOException e) {
                    communicationServer.disconnect(channel);
                    onConnectionFailed(endpoint, e);
                }
            }
        });
    }

    public boolean isListeningForConnections() {
        return registration != null;
    }

    public void listenForConnections(boolean listen) throws IOException {
        if (!isRunning()) {
            throw new GameLibrary2DRuntimeException("Server is not running");
        }

        if (listen) {
            enableConnections();
        } else {
            disableConnections();
        }
    }

    private void enableConnections() throws IOException {
        registration = communicationServer.registerConnectionListener(
                "localhost",
                port,
                this::onConnected,
                (endpoint, exc) -> invokeLater(() -> onConnectionFailed(endpoint, exc)));
    }

    private void disableConnections() throws IOException {
        if (registration != null) {
            communicationServer.deregisterConnectionListener(registration);
            registration = null;
        }
    }

    @Override
    protected void onStart() throws IOException {
        communicationServer.start();
    }

    @Override
    protected void onStop() throws IOException, InterruptedException {
        disableConnections();
        if (ownsCommunicationServer) {
            communicationServer.stop();
        }
    }

    protected abstract void configureClientAuthentication(CommunicationSteps steps);

    protected abstract boolean acceptConnection(String endpoint);

    protected abstract void onConnectionFailed(String endpoint, Exception e);
}