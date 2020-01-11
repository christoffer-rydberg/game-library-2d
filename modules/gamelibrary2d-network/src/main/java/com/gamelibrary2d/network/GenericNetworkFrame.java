package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.objects.Frame;

import java.util.List;

public interface GenericNetworkFrame<TClientObject extends ClientObject, TClientPlayer extends ClientPlayer> extends Frame {

    /**
     * Registers an object to the frame. Call this method whenever an object is read
     * from the server, in order to populate the lists of objects and players in the
     * frame. This will, among other things, ensure that the object is updated when
     * an update is read from the server. The object must also be added to the
     * frame, or one of its containers, for it to be rendered and interpolated
     * toward its orientation.
     *
     * @param obj The client object to register.
     */
    void registerObject(TClientObject obj);

    TClientObject getClientObjectById(int id);

    /**
     * @return List of all local players in the frame.
     */
    List<TClientPlayer> getLocalPlayers();

    /**
     * Unregisters an object to the frame. Call this method when an object should be
     * removed from the client. The object must also be removed from the frame or
     * container that holds it to prevent it from updated and rendered.
     *
     * @param obj The client object to unregister.
     */
    void unregisterObject(TClientObject obj);

    /**
     * @return The communicator used to communicate with the server.
     */
    Communicator getCommunicator();

    /**
     * Sets the communicator used to communicate with the server.
     */
    void setCommunicator(Communicator communicator);

    /**
     * @return The update streaming rate of the server (updates per second)
     */
    float getServerUpdateRate();
}