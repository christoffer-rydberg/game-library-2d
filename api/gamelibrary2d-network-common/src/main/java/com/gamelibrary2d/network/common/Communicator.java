package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.security.EncryptionReader;
import com.gamelibrary2d.network.common.security.EncryptionWriter;

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
    void setAuthenticated();

    /**
     * Used to encrypt and write data to a {@link DataBuffer}.
     */
    EncryptionWriter getEncryptionWriter();

    /**
     * Sets the communicator's {@link #getEncryptionWriter encryption reader}.
     */
    void setEncryptionWriter(EncryptionWriter encryptionWriter);

    /**
     * Writes data to the {@link #getOutgoing() outgoing buffer} and then encrypts the written data.
     * This method can only be used if the communicator has an {@link #setEncryptionWriter encryption writer}.
     *
     * @param plaintextWriter Writes plain data to the buffer.
     * @throws IOException Occurs if the encryption fails.
     */
    default void writeEncrypted(ParameterizedAction<DataBuffer> plaintextWriter) throws IOException {
        var encryptionWriter = getEncryptionWriter();
        if (encryptionWriter == null) {
            throw new NullPointerException("No encryption writer has been set");
        }

        encryptionWriter.write(getOutgoing(), plaintextWriter);
    }

    /**
     * Used to read and decrypt data from a {@link DataBuffer}.
     */
    EncryptionReader getEncryptionReader();

    /**
     * Sets the communicator's {@link #getEncryptionReader encryption reader}.
     */
    void setEncryptionReader(EncryptionReader encryptionReader);

    /**
     * Reads encrypted data from the input buffer, decrypts it, and writes it to the output buffer.
     * This method can only be used if the communicator has an {@link #setEncryptionReader encryption reader}.
     *
     * @param input  The input buffer.
     * @param output The output buffer.
     * @return True if encrypted data was read, false otherwise.
     * @throws IOException Occurs if the decryption fails.
     */
    default boolean readEncrypted(DataBuffer input, DataBuffer output) throws IOException {
        var encryptionReader = getEncryptionReader();
        if (encryptionReader == null) {
            throw new NullPointerException("No encryption reader has been set");
        }

        return encryptionReader.readNext(input, output);
    }
}