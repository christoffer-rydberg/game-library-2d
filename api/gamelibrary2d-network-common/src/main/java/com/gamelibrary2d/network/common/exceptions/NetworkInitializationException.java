package com.gamelibrary2d.network.common.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

public class NetworkInitializationException extends GameLibrary2DException {

    public NetworkInitializationException() {
    }

    public NetworkInitializationException(String message) {
        super(message);
    }

    public NetworkInitializationException(Throwable cause) {
        super(cause);
    }

    public NetworkInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}