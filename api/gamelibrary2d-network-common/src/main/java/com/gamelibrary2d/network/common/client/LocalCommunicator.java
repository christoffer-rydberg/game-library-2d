package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.server.LocalServer;

public interface LocalCommunicator extends Communicator {
    LocalServer getLocalServer();
}
