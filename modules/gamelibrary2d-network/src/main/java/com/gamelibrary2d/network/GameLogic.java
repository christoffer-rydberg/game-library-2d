package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.server.Server;

import java.util.Collection;
import java.util.List;

public interface GameLogic {

    /**
     * @return The server used to communicate with the game client.
     */
    Server getServer();

    /**
     * This method is called on each update cycle. The clients are not updated until
     * {@link #updateClients() updateClients} is called.
     *
     * @param deltaTime Time since the last update.
     * @return True if the game was updated, false otherwise (for example if the
     * game is paused).
     */
    boolean update(float deltaTime);

    /**
     * Sends data to all clients with any changes since last time this method was
     * called. This method can be called after each update or every x update in case
     * you want to update the game logic more often than you stream data to the
     * clients (to reduce network load).
     */
    void updateClients();

    /**
     * @return All registered players.
     */
    List<ServerPlayer> getRegisteredPlayers();

    /**
     * Finds all players that are registered to the specified communicator
     * identifier, and puts them in the specified output list.
     *
     * @param communicatorId The communicator identifier.
     * @param output         The output list.
     */
    void getRegisteredPlayers(int communicatorId, List<ServerPlayer> output);

    /**
     * Gets the {@link ServerObject} registered to the specified identifier.
     *
     * @param id The identifier.
     */
    ServerObject getRegisteredObject(int id);

    /**
     * @return All registered objects.
     */
    Collection<ServerObject> getRegisteredObjects();

    /**
     * Clears the object register.
     */
    void clearRegisteredObjects();

    void configureAuthentication(CommunicationInitializer initializer);

    void configureInitialization(CommunicationInitializer initializer);

    void initialized(Communicator communicator);

    void disconnected(Communicator communicator, boolean pending);

    void onMessage(Communicator communicator, DataBuffer buffer);
}