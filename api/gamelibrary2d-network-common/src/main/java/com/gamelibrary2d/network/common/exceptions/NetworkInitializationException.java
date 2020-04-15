package com.gamelibrary2d.network.common.exceptions;

import java.io.IOException;

public class NetworkInitializationException extends IOException {

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