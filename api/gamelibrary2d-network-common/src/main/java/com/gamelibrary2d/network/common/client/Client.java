package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;

import java.util.concurrent.Future;

public interface Client {

    Future<Void> connect();

    void disconnect();

    boolean isConnected();

    void authenticate(CommunicationContext context) throws NetworkAuthenticationException;

    void initialize(CommunicationContext context) throws NetworkInitializationException;

    void initialized(CommunicationContext context);

    void update(float deltaTime);

    void update(float deltaTime, UpdateAction onUpdate);

    Communicator getCommunicator();

    void setCommunicator(Communicator communicator);

    void clearInbox();
}
