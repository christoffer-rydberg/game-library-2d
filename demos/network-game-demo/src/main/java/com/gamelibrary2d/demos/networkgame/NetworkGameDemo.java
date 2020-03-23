package com.gamelibrary2d.demos.networkgame;

import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.server.DemoGameServer;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;

import java.io.IOException;

public class NetworkGameDemo {
    private static final String title = "Network Game";

    private static boolean isWindowed(String mode) {
        return mode.equalsIgnoreCase("w") || mode.equalsIgnoreCase("windowed");
    }

    private static boolean isFullscreen(String mode) {
        return mode.equalsIgnoreCase("f") || mode.equalsIgnoreCase("fullscreen");
    }

    private static Window createWindow(String[] args) {
        if (args.length == 0) {
            return GlfwWindow.createWindowed(title);
        } else if (args.length == 1) {
            if (isWindowed(args[0])) {
                return GlfwWindow.createWindowed(title);
            } else if (isFullscreen(args[0])) {
                return GlfwWindow.createWindowed(title);
            } else {
                throw new RuntimeException(String.format("Unsupported window mode: %s", args[0]));
            }
        } else if (args.length == 2) {
            int windowWidth = Integer.parseInt(args[0]);
            int windowHeight = Integer.parseInt(args[1]);
            return GlfwWindow.createWindowed(title, windowWidth, windowHeight);
        } else if (args.length == 3) {
            int windowWidth = Integer.parseInt(args[0]);
            int windowHeight = Integer.parseInt(args[1]);
            if (isWindowed(args[2])) {
                return GlfwWindow.createWindowed(title, windowWidth, windowHeight);
            } else if (isFullscreen(args[2])) {
                return GlfwWindow.createFullScreen(title, windowWidth, windowHeight);
            } else {
                throw new RuntimeException(String.format("Unsupported window mode: %s", args[2]));
            }
        } else {
            throw new RuntimeException("Invalid arguments");
        }
    }

    private static Thread startServerThread() {
        var thread = new Thread(() -> {
            var server = new DemoGameServer(4444, 4445);
            try {
                server.listenForConnections(true);
                server.start(); // Blocking
            } catch (IOException e) {
                System.out.println("Failed to start connection server");
                e.printStackTrace();
                System.exit(-1);
            }
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        var serverThread = startServerThread();
        new DemoGame().start(createWindow(args));
        try {
            serverThread.interrupt();
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}