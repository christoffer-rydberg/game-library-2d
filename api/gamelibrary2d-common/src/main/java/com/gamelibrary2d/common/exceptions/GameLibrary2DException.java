package com.gamelibrary2d.common.exceptions;

/**
 * Base class for GameLibrary2D exceptions.
 */
public class GameLibrary2DException extends Exception {

    public GameLibrary2DException() {

    }

    public GameLibrary2DException(Throwable cause) {
        super(cause);
    }

    public GameLibrary2DException(String message) {
        super(message);
    }

    public GameLibrary2DException(String message, Throwable cause) {
        super(message, cause);
    }
}