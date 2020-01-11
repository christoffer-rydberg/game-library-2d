package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;

public interface ServerPlayer extends ServerObject {
    Communicator getCommunicator();
}