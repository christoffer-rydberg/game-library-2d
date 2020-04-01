package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.nio.charset.StandardCharsets;

public class DemoClient extends AbstractClient {
    private final Communicator communicator;
    private final UpdateLoop updateLoop;

    DemoClient(Communicator communicator) {
        this.communicator = communicator;
        updateLoop = new UpdateLoop(this::update, 10);
    }

    void run() throws InitializationException {
        initialize();
        authenticate();
        updateLoop.run();
    }

    private void sendMessage(String message) {
        var communicator = getCommunicator();
        var bytes = message.getBytes(StandardCharsets.UTF_8);
        communicator.getOutgoing().putInt(bytes.length);
        communicator.getOutgoing().put(bytes);
    }

    @Override
    protected void onConfigureInitialization(CommunicationSteps steps) {

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
    protected void onInitialized() {
        sendMessage("What do you call a guy with a rubber toe?");
    }

    @Override
    public Communicator getCommunicator() {
        return communicator;
    }

    @Override
    protected void onDisconnected(Communicator communicator, Throwable cause) {
        updateLoop.stop();
    }
}