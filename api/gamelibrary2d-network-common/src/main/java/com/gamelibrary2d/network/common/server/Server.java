package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.Message;

import java.io.IOException;

public interface Server {
    void start() throws IOException;

    void stop() throws IOException, InterruptedException;

    boolean isRunning();

    void update(float deltaTime);

    /**
     * Marks the specified communicator as pending and invokes {@link #configureClientInitialization}
     * for it to be reinitialized. This is useful if the client has changed frame and wants
     * to initialize that frame.
     */
    void deinitialize(Communicator communicator);

    void send(Communicator communicator, int message);

    void send(Communicator communicator, float message);

    void send(Communicator communicator, double message);

    void send(Communicator communicator, byte message);

    void send(Communicator communicator, byte[] message, int off, int len);

    void send(Communicator communicator, Message message);

    void sendToAll(int message, boolean stream);

    void sendToAll(float message, boolean stream);

    void sendToAll(double message, boolean stream);

    void sendToAll(byte message, boolean stream);

    void sendToAll(byte[] message, int off, int len, boolean stream);

    void sendToAll(Message message, boolean stream);

}