package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;
import com.gamelibrary2d.network.common.client.Client;

public class PlayerAcceleration implements Message {
    private final Client client;
    private final int playerId;

    private float acceleration;
    private float leftAcceleration;
    private float rightAcceleration;

    private boolean changed = true;

    public PlayerAcceleration(Client client, int playerId) {
        this.client = client;
        this.playerId = playerId;
    }

    public void setAcceleration(float acceleration) {
        if (this.acceleration != acceleration) {
            this.acceleration = acceleration;
            changed = true;
        }
    }

    public void setLeftAcceleration(float leftAcceleration) {
        if (this.leftAcceleration != leftAcceleration) {
            this.leftAcceleration = leftAcceleration;
            changed = true;
        }
    }

    public void setRightAcceleration(float rightAcceleration) {
        if (this.rightAcceleration != rightAcceleration) {
            this.rightAcceleration = rightAcceleration;
            changed = true;
        }
    }

    public void updateServer() {
        if (changed) {
            changed = false;
            serializeMessage(client.getCommunicator().getOutgoing());
        }
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        buffer.put(ClientMessages.PLAYER_ACCELERATION);
        buffer.putInt(playerId);
        buffer.putFloat(acceleration);
        buffer.putFloat(rightAcceleration - leftAcceleration);
    }
}