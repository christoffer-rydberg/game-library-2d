package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.Message;

public interface Server {

    void update(float deltaTime);

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