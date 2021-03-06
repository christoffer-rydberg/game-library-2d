package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.client.CommunicatorFactory;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedEvent;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.nio.charset.StandardCharsets;

public class DemoClient extends AbstractClient {
    private final UpdateLoop updateLoop;

    private volatile boolean disconnected;

    DemoClient() {
        updateLoop = new UpdateLoop(this::update, 10);
    }

    void run(CommunicatorFactory communicatorFactory) {
        try {
            setCommunicatorFactory(communicatorFactory);
            CommunicationContext context = initialize();
            initialized(context);
        } catch (NetworkConnectionException e) {
            System.err.println("Failed to connect client");
            e.printStackTrace();
            return;
        } catch (NetworkAuthenticationException e) {
            System.err.println("Failed to authenticate client");
            e.printStackTrace();
            return;
        } catch (NetworkInitializationException e) {
            System.err.println("Failed to initialize client");
            e.printStackTrace();
            return;
        }

        updateLoop.run();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (disconnected) {
            readMessages(); // Read and handle any last messages
            updateLoop.stop();
        }
    }

    private void sendMessage(String message) {
        Communicator communicator = getCommunicator();
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        communicator.getOutgoing().putInt(bytes.length);
        communicator.getOutgoing().put(bytes);
    }

    @Override
    protected void onConfigureInitialization(CommunicationSteps steps) {

    }

    @Override
    protected void onMessage(DataBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        String message = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(String.format("Server: %s", message));
        sendMessage("Roberto.");
    }

    @Override
    protected void onInitialized(CommunicationContext context, Communicator communicator) {
        communicator.addDisconnectedListener(this::onDisconnected);
        sendMessage("What do you call a guy with a rubber toe?");
    }

    private void onDisconnected(CommunicatorDisconnectedEvent event) {
        disconnected = true;
    }
}