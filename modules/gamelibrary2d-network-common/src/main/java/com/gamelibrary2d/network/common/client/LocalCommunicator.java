package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.AbstractCommunicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.server.LocalServer;
import com.gamelibrary2d.network.common.server.Server;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LocalCommunicator extends AbstractCommunicator implements Connectable {

    private final LocalServer localServer;

    private final LocalCommunicator serverSideCommunicator;

    public LocalCommunicator(LocalServer localServer) {
        super(1, false);
        this.localServer = localServer;
        serverSideCommunicator = new LocalCommunicator(this);
    }

    private LocalCommunicator(LocalCommunicator host) {
        super(1, true);
        localServer = null;
        this.serverSideCommunicator = host;
    }

    @Override
    public Future<Void> connect() {
        if (setConnected()) {
            try {
                localServer.addCommunicator(serverSideCommunicator);
            } catch (InitializationException e) {
                return CompletableFuture.failedFuture(e);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void connect(Action onSuccess, ParameterizedAction<Throwable> onFail) {
        if (setConnected()) {
            try {
                localServer.addCommunicator(serverSideCommunicator);
                onSuccess.invoke();
            } catch (InitializationException e) {
                onFail.invoke(e);
            }
        }
    }

    @Override
    public String getEndpoint() {
        return "localhost";
    }

    @Override
    protected void send(DataBuffer buffer) throws IOException {
        serverSideCommunicator.addIncoming(buffer1 -> {
            int size = buffer.remaining();
            buffer1.putBool(false);
            buffer1.putInt(size);
            buffer1.put(buffer);
        }, 0);
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        if (localServer != null) {
            serverSideCommunicator.disconnect();
            localServer.update(0);
        }
    }

    public Server getLocalServer() {
        return localServer;
    }
}