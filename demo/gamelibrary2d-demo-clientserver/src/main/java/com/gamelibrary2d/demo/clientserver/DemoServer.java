package com.gamelibrary2d.demo.clientserver;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.server.AbstractNetworkServer;

import java.nio.charset.StandardCharsets;

public class DemoServer extends AbstractNetworkServer {

    private final UpdateLoop updateLoop;

    private int messagesReceived;

    DemoServer(int port) {
        super(port, true);
        updateLoop = new UpdateLoop(this, 10);
    }

    void run() {
        updateLoop.run();
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {

    }

    @Override
    protected boolean acceptConnection(String endpoint) {
        return true;
    }

    @Override
    protected void onInitialized(Communicator communicator) {
        System.out.println(String.format("%s has connected.", communicator.getEndpoint()));
    }

    @Override
    protected void onConnectionFailed(String endpoint, Exception e) {
        System.out.println(String.format("%s failed to connect", endpoint));
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        System.out.println(String.format("%s has disconnected.", communicator.getEndpoint()));
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                communicator.disconnect();
            }
        }
    }
}