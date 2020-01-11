package com.gamelibrary2d.demo.clientserver;

import com.gamelibrary2d.network.common.client.ClientSideCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;

import java.io.IOException;

public class ClientServerDemo {

    private static void exitWithError(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
        System.exit(-1);
    }

    private static Thread startServerThread() {
        var thread = new Thread(() -> {
            var server = new DemoServer(4444);
            try {
                // Listen for incoming connections
                server.startConnectionServer();

                // Run update loop that will send outgoing messages and listen for incoming messages (blocking).
                server.run();

                // Stop internal server threads
                server.stop();
            } catch (IOException e) {
                exitWithError("Failed to start connection server", e);
            }
        });
        thread.start();
        return thread;
    }

    private static Thread startClientThread() {
        var thread = new Thread(() -> {
            var communicator = new ClientSideCommunicator(new TcpConnectionSettings("localhost", 4444, true));
            var client = new DemoClient(communicator);

            // Begin connecting to server
            client.connect(client::tellJoke, e -> exitWithError("Failed to connect to server", e));

            // Run update loop that will send outgoing messages and listen for incoming messages once connected (blocking).
            client.run();
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        var serverThread = startServerThread();
        var clientThread = startClientThread();
        try {
            clientThread.join();
            serverThread.interrupt();
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}