package com.gamelibrary2d.network.common.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

public class InitializationException extends GameLibrary2DException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InitializationException() {
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Exception innerException) {
        super(innerException);
    }

    public InitializationException(String message, Exception innerException) {
        super(message, innerException);
    }
}