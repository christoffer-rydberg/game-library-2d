package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;

public interface Client {

    /**
     * @return The client communicator, or null if the client has not been initialized.
     */
    Communicator getCommunicator();

    /**
     * Sets a {@link CommunicatorFactory} used when connecting the client.
     */
    void setCommunicatorFactory(CommunicatorFactory communicatorFactory);

    /**
     * Connects, authenticates and initializes the client.
     * This method can be invoked from an arbitrary thread, and when it has finished, {@link #prepared} should
     * be invoked from the main thread.
     */
    void prepare(CommunicationContext context)
            throws NetworkConnectionException, NetworkAuthenticationException, NetworkInitializationException;

    /**
     * Invoked from the main thread when the client has been {@link #prepare initialized).
     */
    void prepared(CommunicationContext context);

    /**
     * Checks if the client is connected.
     */
    boolean isConnected();

    /**
     * Disconnects the client.
     */
    void disconnect();

    /**
     * Updates the client.
     *
     * @param deltaTime Time since last update in seconds.
     */
    void update(float deltaTime);

    /**
     * Updates the client with the specified update action.
     *
     * @param deltaTime Time since last update in seconds.
     * @param onUpdate  Time update action.
     */
    void update(float deltaTime, UpdateAction onUpdate);

    /**
     * Clears all received data.
     */
    void clearInbox();
}
