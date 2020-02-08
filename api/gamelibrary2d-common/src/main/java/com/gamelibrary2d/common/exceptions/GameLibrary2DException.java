package com.gamelibrary2d.common.exceptions;

/**
 * Base class for GameLibrary2D exceptions.
 */
public class GameLibrary2DException extends Exception {

    /**
     */
    private static final long serialVersionUID = 1L;

    private final Exception innerException;

    public GameLibrary2DException() {
        innerException = null;
    }

    public GameLibrary2DException(Exception innerException) {
        this.innerException = innerException;
    }

    public GameLibrary2DException(String message) {
        super(message);
        innerException = null;
    }

    public GameLibrary2DException(String message, Exception innerException) {
        super(message);
        this.innerException = innerException;
    }

    public Exception getInnerException() {
        return innerException;
    }
}