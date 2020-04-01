package com.gamelibrary2d.demos.network;

import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.client.DefaultClientSideCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.io.IOException;

public class NetworkDemo {

    private static void exitWithError(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
        System.exit(-1);
    }

    private static Thread startServerThread() {
        var thread = new Thread(() -> {
            var server = new DemoServer(4444);
            try {
                server.start();
                server.listenForConnections(true);
                new UpdateLoop(server::update, 10).run();
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
            var communicator = new DefaultClientSideCommunicator(new TcpConnectionSettings("localhost", 4444));
            var client = new DemoClient(communicator);

            try {
                client.connect().get();
            } catch (Exception e) {
                exitWithError("Failed to connect", e);
            }

            // Run update loop that will send outgoing messages and listen for incoming messages once connected (blocking).
            try {
                client.run();
            } catch (InitializationException e) {
                e.printStackTrace();
            }
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