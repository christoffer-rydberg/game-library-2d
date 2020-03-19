package com.gamelibrary2d.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

/**
 * Occurs when a frame fails to load.
 */
public class LoadFailedException extends GameLibrary2DException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LoadFailedException() {
    }

    public LoadFailedException(String message) {
        super(message);
    }

    public LoadFailedException(String message, Exception innerException) {
        super(message, innerException);
    }
}