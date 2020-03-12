package com.gamelibrary2d.network;

import java.util.Collection;
import java.util.List;

public interface GameLogic {

    /**
     * @param deltaTime Time since the last update.
     * @return True if the game was updated, false otherwise (for example if the
     * game is paused).
     */
    boolean update(float deltaTime);

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

}