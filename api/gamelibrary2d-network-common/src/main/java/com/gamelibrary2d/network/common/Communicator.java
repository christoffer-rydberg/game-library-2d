package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;

public interface Communicator {

    /**
     * @return The communication end point.
     */
    String getEndpoint();

    /**
     * Gets the unique ID of the communicator.
     *
     * @return Unique ID of the communicator.
     */
    int getId();

    /**
     * Sets the unique ID of the communicator.
     */
    void setId(int id);

    /**
     * Checks if the communicator is connected.
     *
     * @return True if connected, false otherwise.
     */
    boolean isConnected();

    /**
     * Disconnects the communicator.
     */
    void disconnect();

    /**
     * Disconnects the communicator.
     */
    void disconnect(Throwable cause);

    /**
     * Adds bytes from the specified {@link DataReader} to the communicator's
     * incoming buffer.
     */
    void addIncoming(int channel, DataReader dataReader) throws IOException;

    /**
     * Reads bytes from the communicator's incoming buffer to the specified output
     * buffer.
     *
     * @return True if any bytes were read, false otherwise.
     */
    boolean readIncoming(DataBuffer outputBuffer) throws IOException;

    /**
     * Gets the outgoing buffer.
     */
    DataBuffer getOutgoing();

    /**
     * Sends and clears the outgoing buffer.
     */
    void sendOutgoing() throws IOException;

    /**
     * Sends the update buffer. Only the latest version of this buffer is important,
     * which gives the communicator freedom to send this buffer using a faster
     * protocol than that for other messages (which must guarantee delivery).
     */
    void sendUpdate(DataBuffer updateBuffer) throws IOException;

    /**
     * Adds a listener for events from this communicator
     */
    void addDisconnectedListener(CommunicatorDisconnectedListener listener);

    /**
     * Removes a listener for events from this communicator
     */
    void removeDisconnectedListener(CommunicatorDisconnectedListener listener);

    /**
     * @return True if the communicator has been authenticated, false otherwise.
     */
    boolean isAuthenticated();

    void configureAuthentication(CommunicationSteps steps);

    /**
     * Invoked when the client/server connection has been authenticated.
     */
    void onAuthenticated();

    /**
     * Reallocates the outgoing buffer.
     */
    void reallocateOutgoing();
}