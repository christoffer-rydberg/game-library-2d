package com.gamelibrary2d.network.common.events;

import com.gamelibrary2d.network.common.Communicator;

public class CommunicatorDisconnectedEvent {

    private final Communicator communicator;
    private final Throwable cause;

    public CommunicatorDisconnectedEvent(Communicator communicator, Throwable cause) {
        this.communicator = communicator;
        this.cause = cause;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public Throwable getCause() {
        return cause;
    }
}