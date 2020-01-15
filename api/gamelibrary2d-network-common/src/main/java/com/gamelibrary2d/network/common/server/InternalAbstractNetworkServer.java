package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.ServerSocketChannelRegistration;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

abstract class InternalAbstractNetworkServer extends InternalAbstractServer {

    private final CommunicationServer communicationServer;

    private final boolean ownsCommunicationServer;

    InternalAbstractNetworkServer() {
        this.communicationServer = new CommunicationServer();
        this.ownsCommunicationServer = true;
    }

    InternalAbstractNetworkServer(CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
        this.ownsCommunicationServer = false;
    }

    void deregisterConnectionListener(ServerSocketChannelRegistration registration) throws IOException {
        communicationServer.deregisterConnectionListener(registration);
    }

    ServerSocketChannelRegistration registerConnectionListener(String hostName, int port, boolean ssl)
            throws IOException {
        return communicationServer.registerConnectionListener(hostName, port, x -> onConnected(x, ssl),
                (x, y) -> invokeLater(() -> onConnectionFailed(x, y)));
    }

    void startInternal() throws IOException {
        communicationServer.start();
    }

    void stopInternal() {
        if (ownsCommunicationServer) {
            try {
                communicationServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void onConnected(SocketChannel channel, boolean ssl) {
        invokeLater(() -> {
            var endpoint = channel.socket().getInetAddress().getHostAddress();
            if (!acceptConnection(endpoint)) {
                communicationServer.disconnect(channel);
                onConnectionFailed(endpoint, new IOException("Connection refused by server"));
            } else {
                var communicator = new ServerSideCommunicator(communicationServer, channel);
                try {
                    // Disable Nagle's algorithm
                    channel.socket().setTcpNoDelay(true);
                    addCommunicator(new CommunicatorWrapper(communicator));
                } catch (SocketException | InitializationException e) {
                    communicationServer.disconnect(channel);
                    onConnectionFailed(endpoint, e);
                }
            }
        });
    }

    protected abstract boolean acceptConnection(String endpoint);

    protected abstract void onConnectionFailed(String endpoint, Exception e);
}
