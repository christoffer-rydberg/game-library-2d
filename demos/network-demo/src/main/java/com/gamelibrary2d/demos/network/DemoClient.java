package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

import java.nio.charset.StandardCharsets;

public class DemoClient extends AbstractClient {

    private final UpdateLoop updateLoop;

    DemoClient(Communicator communicator) {
        super(communicator);
        updateLoop = new UpdateLoop(this, 10);
    }

    void run() {
        updateLoop.run();
    }

    private void sendMessage(String message) {
        var communicator = getCommunicator();
        var bytes = message.getBytes(StandardCharsets.UTF_8);
        communicator.getOutgoing().putInt(bytes.length);
        communicator.getOutgoing().put(bytes);
    }

    void tellJoke() {
        sendMessage("What do you call a guy with a rubber toe?");
    }

    @Override
    protected void configureInitialization(CommunicationInitializer initializer) {

    }

    @Override
    protected void onMessage(DataBuffer buffer) {
        var length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        var message = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(String.format("Server: %s", message));
        sendMessage("Roberto.");
    }

    @Override
    protected void onDisconnected(Throwable cause) {
        updateLoop.stop();
    }
}