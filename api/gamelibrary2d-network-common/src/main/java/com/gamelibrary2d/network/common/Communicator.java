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
     * @return The unique ID of the communicator.
     */
    int getId();

    /**
     * Sets the communicator {@link #getId id}.
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
    boolean readIncoming(DataBuffer outputBuffer);

    /**
     * Gets the outgoing buffer.
     */
    DataBuffer getOutgoing();

    /**
     * Sends and clears the {@link #getOutgoing() outgoing buffer}.
     */
    void sendOutgoing() throws IOException;

    /**
     * Reallocates the outgoing buffer.
     */
    void reallocateOutgoing();

    /**
     * Sends the content of the specified buffer.
     *
     * @param buffer The buffer to send.
     */
    void sendUpdate(DataBuffer buffer) throws IOException;

    /**
     * Adds a listener for events from this communicator
     */
    void addDisconnectedListener(CommunicatorDisconnectedListener listener);

    /**
     * Removes a listener for events from this communicator
     */
    void removeDisconnectedListener(CommunicatorDisconnectedListener listener);

    /**
     * Configures authentication steps.
     */
    void configureAuthentication(CommunicationSteps steps);

    /**
     * @return True if the communicator has been authenticated, false otherwise.
     */
    boolean isAuthenticated();

    /**
     * Invoked when the client/server connection has been authenticated.
     */
    void onAuthenticated();

}