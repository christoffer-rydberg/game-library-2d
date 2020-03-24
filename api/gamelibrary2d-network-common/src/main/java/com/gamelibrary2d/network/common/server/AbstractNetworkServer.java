package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.ServerSocketChannelRegistration;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.net.SocketException;
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
                    addCommunicator(new ServerSideCommunicator(communicationServer, channel, this::configureClientAuthentication));
                } catch (SocketException | InitializationException e) {
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
        if (listen) {
            communicationServer.start();
            registration = communicationServer.registerConnectionListener(
                    "localhost",
                    port,
                    this::onConnected,
                    (endpoint, exc) -> invokeLater(() -> onConnectionFailed(endpoint, exc)));
        } else if (registration != null) {
            communicationServer.deregisterConnectionListener(registration);
            registration = null;
        }
    }

    public void stop() throws IOException {
        listenForConnections(false);
        if (ownsCommunicationServer) {
            try {
                communicationServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void configureClientAuthentication(CommunicationSteps steps);

    protected abstract boolean acceptConnection(String endpoint);

    protected abstract void onConnectionFailed(String endpoint, Exception e);
}