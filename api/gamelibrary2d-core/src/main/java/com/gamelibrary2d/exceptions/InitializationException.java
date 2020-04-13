package com.gamelibrary2d.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

public class InitializationException extends GameLibrary2DException {

    public InitializationException() {
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}