package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.AbstractNetworkServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DemoServer extends AbstractNetworkServer {
    private int messagesReceived;

    DemoServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected void configureClientAuthentication(CommunicationSteps steps) {

    }

    @Override
    protected void configureClientInitialization(CommunicationSteps steps) {

    }

    @Override
    protected void onClientAuthenticated(CommunicationContext context, Communicator communicator) {
        log(String.format("Client has been authenticated: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onClientInitialized(CommunicationContext context, Communicator communicator) {
        log(String.format("Client has been initialized: %s", communicator.getEndpoint()));
    }

    @Override
    protected boolean acceptConnection(String endpoint) {
        log(String.format("Accepting incoming connection: %s", endpoint));
        return true;
    }

    @Override
    protected void onConnectionFailed(String endpoint, Exception e) {
        log(String.format("Incoming connection failed: %s", endpoint), e);
    }

    @Override
    protected void onConnected(Communicator communicator) {
        log(String.format("Connection established: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        log(String.format("Connection lost: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onUpdate(float deltaTime) {

    }

    private void sendMessage(Communicator communicator, String message) {
        var bytes = message.getBytes(StandardCharsets.UTF_8);
        communicator.getOutgoing().putInt(bytes.length);
        communicator.getOutgoing().put(bytes);
    }

    @Override
    protected void onMessage(Communicator communicator, DataBuffer buffer) {
        var length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        var message = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(String.format("Client: %s", message));

        ++messagesReceived;
        if (messagesReceived == 1) {
            sendMessage(communicator, "What?");
        } else {
            sendMessage(communicator, "... Bye.");
            try {
                communicator.sendOutgoing();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                communicator.disconnect();
            }
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void log(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
    }
}