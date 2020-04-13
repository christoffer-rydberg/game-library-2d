package com.gamelibrary2d.common.exceptions;

/**
 * Base class for GameLibrary2D runtime exceptions.
 */
public class GameLibrary2DRuntimeException extends RuntimeException {

    public GameLibrary2DRuntimeException() {

    }

    public GameLibrary2DRuntimeException(Throwable cause) {
        super(cause);
    }

    public GameLibrary2DRuntimeException(String message) {
        super(message);
    }

    public GameLibrary2DRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}