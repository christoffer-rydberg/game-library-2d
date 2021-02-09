package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.client.ClientSideCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;

import java.io.IOException;

public class NetworkDemo {

    private static void exitWithError(String message, Throwable cause) {
        System.out.println(message);
        cause.printStackTrace();
        System.exit(-1);
    }

    private static Thread startServerThread() {
        Thread thread = new Thread(() -> {
            DemoServer server = new DemoServer("localhost", 4444);
            try {
                server.start();
                server.listenForConnections(true);
                new UpdateLoop(server::update, 10).run();
                server.stop();
            } catch (IOException | InterruptedException e) {
                exitWithError("Failed to start connection server", e);
            }
        });
        thread.start();
        return thread;
    }

    private static Thread startClientThread() {
        Thread thread = new Thread(() -> {
            DemoClient client = new DemoClient();

            // Run update loop that will send outgoing messages and listen for incoming messages once connected (blocking).
            client.run(() -> ClientSideCommunicator.connect(
                    new TcpConnectionSettings("localhost", 4444)
            ));
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        Thread serverThread = startServerThread();
        Thread clientThread = startClientThread();
        try {
            clientThread.join();
            serverThread.interrupt();
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}