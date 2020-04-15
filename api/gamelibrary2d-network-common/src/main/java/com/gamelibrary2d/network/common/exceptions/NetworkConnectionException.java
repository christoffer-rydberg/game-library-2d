package com.gamelibrary2d.network.common.exceptions;

import java.io.IOException;

public class NetworkConnectionException extends IOException {

    public NetworkConnectionException() {
    }

    public NetworkConnectionException(String message) {
        super(message);
    }

    public NetworkConnectionException(Throwable cause) {
        super(cause);
    }

    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}