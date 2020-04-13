package com.gamelibrary2d.network.common.exceptions;

import com.gamelibrary2d.common.exceptions.GameLibrary2DException;

public class NetworkAuthenticationException extends GameLibrary2DException {

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