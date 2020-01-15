package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.ServerSocketChannelRegistration;

import java.io.IOException;

public abstract class AbstractNetworkServer extends InternalAbstractNetworkServer {

    private final int port;
    private final boolean ssl;
    private ServerSocketChannelRegistration registration;

    protected AbstractNetworkServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
    }

    protected AbstractNetworkServer(int port, boolean ssl, CommunicationServer communicationServer) {
        super(communicationServer);
        this.port = port;
        this.ssl = ssl;
    }

    /**
     * Starts the server. This is implicitly called when invoking
     * {@link AbstractNetworkServer#startConnectionServer startConnectionServer}.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        super.startInternal();
    }

    /**
     * Stops internal threads of the server.
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        stopConnectionServer();
        super.stopInternal();
    }

    public void startConnectionServer() throws IOException {
        if (registration == null) {
            start();
            registration = registerConnectionListener("localhost", port, ssl);
        }
    }

    public void stopConnectionServer() throws IOException {
        if (registration != null) {
            deregisterConnectionListener(registration);
            registration = null;
        }
    }
}