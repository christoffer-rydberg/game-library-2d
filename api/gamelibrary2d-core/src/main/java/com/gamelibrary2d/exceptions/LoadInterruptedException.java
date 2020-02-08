package com.gamelibrary2d.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

/**
 * Occurs when a frame fails to load.
 */
public class LoadInterruptedException extends GameLibrary2DException {

    /**
     */
    private static final long serialVersionUID = 1L;

    public LoadInterruptedException() {
    }

    public LoadInterruptedException(String message) {
        super(message);
    }
}