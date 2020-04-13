package com.gamelibrary2d.demos.particlesystem;

import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;

public class ParticleSystemDemo {
    private static final String title = "Particle System";

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

    public static void main(String[] args) throws InitializationException {
        new DemoGame().start(createWindow(args));
    }
}