package com.gamelibrary2d.framework;

public class Runtime {

    private static Framework framework;

    public static void initialize(Framework framework) {
        if (Runtime.framework != null) {
            throw new IllegalStateException("GameLibrary2D runtime has already been initialized!");
        }

        Runtime.framework = framework;
    }

    public static Framework getFramework() {
        if (framework == null) {
            throw new IllegalStateException("GameLibrary2D runtime has not been initialized");
        }

        return framework;
    }

    public static void dispose() {
        framework = null;
    }
}