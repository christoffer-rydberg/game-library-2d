package com.gamelibrary2d.common.exceptions;

/**
 * Base class for GameLibrary2D runtime exceptions.
 *
 * @author Christoffer Rydberg
 */
public class GameLibrary2DRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final Exception innerException;

    public GameLibrary2DRuntimeException() {
        innerException = null;
    }

    public GameLibrary2DRuntimeException(Exception innerException) {
        this.innerException = innerException;
    }

    public GameLibrary2DRuntimeException(String message) {
        super(message);
        innerException = null;
    }

    public GameLibrary2DRuntimeException(String message, Exception innerException) {
        super(message);
        this.innerException = innerException;
    }

    public Exception getInnerException() {
        return innerException;
    }
}