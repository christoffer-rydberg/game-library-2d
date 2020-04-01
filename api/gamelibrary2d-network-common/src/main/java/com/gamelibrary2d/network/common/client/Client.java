package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.util.concurrent.Future;

public interface Client {

    Future<Void> connect();

    void disconnect();

    boolean isConnected();

    void authenticate() throws InitializationException;

    void initialize() throws InitializationException;

    void update(float deltaTime);

    void update(float deltaTime, UpdateAction onUpdate);

    Communicator getCommunicator();

    void setCommunicator(Communicator communicator);

    void clearInbox();
}
