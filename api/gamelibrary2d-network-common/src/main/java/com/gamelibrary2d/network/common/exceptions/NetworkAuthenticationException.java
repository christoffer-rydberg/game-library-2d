package com.gamelibrary2d.network.common.exceptions;

import java.io.IOException;

public class NetworkAuthenticationException extends IOException {

    public NetworkAuthenticationException() {
    }

    public NetworkAuthenticationException(String message) {
        super(message);
    }

    public NetworkAuthenticationException(Throwable cause) {
        super(cause);
    }

    public NetworkAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}