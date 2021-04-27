package com.gamelibrary2d.demos.networkgame;

import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.ServerManager;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;
import com.gamelibrary2d.sound.lwjgl.DefaultSoundManager;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class NetworkGameDemo {
    private static final String title = "Network Game Demo";

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

    private static KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static void main(String[] args) throws InitializationException, NoSuchAlgorithmException {
        try (DefaultDisposer disposer = new DefaultDisposer()) {
            new DemoGame(
                    new Lwjgl_Framework(),
                    ServerManager.create(createKeyPair(), disposer),
                    DefaultSoundManager.create(disposer)).start(createWindow(args)
            );
        }
    }
}