package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.concurrent.NotHandledException;
import com.gamelibrary2d.common.concurrent.ResultHandlingFuture;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.functional.Functions;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.*;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractClientSideCommunicator extends AbstractNetworkCommunicator
        implements TcpConnector, UdpReceiver, Connectable, Reconnectable {

    private final boolean ownsCommunicationServer;

    private TcpConnectionSettings tcpSettings;

    private TcpConnectionSettings connectedTcpSettings;

    private volatile boolean connectingTcp;

    protected AbstractClientSideCommunicator(TcpConnectionSettings tcpSettings) {
        super(new CommunicationServer(), 2, false);
        this.tcpSettings = tcpSettings;
        ownsCommunicationServer = true;
    }

    protected AbstractClientSideCommunicator(TcpConnectionSettings tcpSettings, CommunicationServer communicationServer) {
        super(communicationServer, 2, false);
        this.tcpSettings = tcpSettings;
        ownsCommunicationServer = false;
    }

    @Override
    public Future<Void> connect() {
        return canConnect() ? connectTcp() : CompletableFuture.completedFuture(null);
    }

    @Override
    public void connect(Action onSuccess, ParameterizedAction<Throwable> onFail) {
        if (canConnect()) {
            connectTcp(onSuccess, onFail);
        }
    }

    private boolean canConnect() {
        return !isConnected() && !connectingTcp;
    }

    @Override
    public TcpConnectionSettings getTcpConnectionSettings() {
        return tcpSettings;
    }

    @Override
    public void setTcpConnectionSettings(TcpConnectionSettings tcpSettings) {
        this.tcpSettings = tcpSettings;
    }

    @Override
    public String getEndpoint() {
        return connectedTcpSettings != null ? connectedTcpSettings.getHost() : tcpSettings.getHost();
    }

    private Future<Void> connectTcp() {
        connectingTcp = true;
        var communicationServer = getCommunicationServer();
        var communicationServerWasRunning = communicationServer.isRunning();
        var future = new CompletableFuture<Void>();

        SocketChannelConnectedHandler connected = x -> {
            try {
                onConnected(x);
                future.complete(null);
            } catch (SocketException e) {
                future.completeExceptionally(e);
            }
        };

        SocketChannelFailedConnectionHandler failed = (x, e) -> {
            try {
                onDisconnected(e);
            } finally {
                future.completeExceptionally(e);
            }
        };

        try {
            communicationServer.start();
            communicationServer.connect(tcpSettings.getHost(), tcpSettings.getPort(), connected, failed);
            connectedTcpSettings = tcpSettings;
        } catch (IOException e) {
            connectingTcp = false;
            if (!communicationServerWasRunning) {
                try {
                    communicationServer.stop();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            future.completeExceptionally(e);
        }

        return new ResultHandlingFuture<>(future, Functions::Id, e -> {
            disconnect(e);
            throw new NotHandledException();
        });
    }

    private void connectTcp(Action onSuccess, ParameterizedAction<Throwable> onFail) {
        connectingTcp = true;
        var communicationServer = getCommunicationServer();
        var communicationServerWasRunning = communicationServer.isRunning();

        SocketChannelConnectedHandler connected = x -> {
            try {
                onConnected(x);
                new Thread(onSuccess::invoke).start();
            } catch (SocketException e) {
                new Thread(() -> onFail.invoke(e)).start();
            }
        };

        SocketChannelFailedConnectionHandler failed = (x, e) -> {
            try {
                onDisconnected(e);
            } finally {
                new Thread(() -> onFail.invoke(e)).start();
            }
        };

        try {
            communicationServer.start();
            communicationServer.connect(tcpSettings.getHost(), tcpSettings.getPort(), connected, failed);
            connectedTcpSettings = tcpSettings;
        } catch (IOException e) {
            connectingTcp = false;
            if (!communicationServerWasRunning) {
                try {
                    communicationServer.stop();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            onFail.invoke(e);
        }
    }

    /**
     * This method is invoked from the {@link CommunicationServer communication
     * server} thread and must be thread safe.
     */
    private void onConnected(SocketChannel socketChannel) throws SocketException {
        setConnected();
        connectingTcp = false;
        socketChannel.socket().setTcpNoDelay(true);
        setSocketChannel(socketChannel);
        getCommunicationServer().connect(socketChannel, this, this::onSocketChannelDisconnected);
    }

    /**
     * This method is invoked from the {@link CommunicationServer communication
     * server} thread and must be thread safe.
     */
    private void onDisconnected(IOException e) {
        onSocketChannelDisconnected(e);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        connectingTcp = false;
        super.onDisconnected(cause);
        if (ownsCommunicationServer) {
            try {
                getCommunicationServer().stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectUdpReceiver(int localPort) throws IOException {
        super.connectUdp(ConnectionOperations.READ, localPort, 0);
    }

    @Override
    public void disconnectUdpReceiver() {
        disconnectUdp();
    }

    @Override
    protected void disconnectTcp() {
        super.disconnectTcp();
        connectedTcpSettings = null;
    }

    @Override
    public Future<Void> reconnect() {
        if (!connectedTcpSettings.equals(tcpSettings)) {
            disconnectTcp();
            return connectTcp();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected boolean isCommunicationClosed() {
        return super.isCommunicationClosed() && !connectingTcp;
    }
}