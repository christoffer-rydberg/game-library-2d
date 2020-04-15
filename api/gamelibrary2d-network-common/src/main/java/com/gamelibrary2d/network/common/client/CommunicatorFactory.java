package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;

import java.util.concurrent.Future;

public interface CommunicatorFactory {
    Future<Communicator> create() throws NetworkConnectionException;
}
