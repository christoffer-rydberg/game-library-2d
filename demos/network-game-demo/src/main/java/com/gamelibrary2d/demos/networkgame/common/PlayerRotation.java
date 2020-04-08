package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;
import com.gamelibrary2d.network.common.client.Client;

public class PlayerRotation implements Message {
    private final Client client;
    private final int playerId;

    private boolean rotatingLeft;
    private boolean rotatingRight;

    private RotationDirection rotation;
    private RotationDirection prevRotation;

    public PlayerRotation(Client client, int playerId) {
        this.client = client;
        this.playerId = playerId;
    }

    public void setRotatingLeft(boolean b) {
        this.rotatingLeft = b;
    }

    public void setRotatingRight(boolean b) {
        this.rotatingRight = b;
    }

    private RotationDirection getRotation() {
        if (rotatingLeft && !rotatingRight) {
            return RotationDirection.LEFT;
        } else if (rotatingRight && !rotatingLeft) {
            return RotationDirection.RIGHT;
        } else {
            return RotationDirection.NONE;
        }
    }

    public void updateServer() {
        rotation = getRotation();
        if (rotation != prevRotation) {
            prevRotation = rotation;
            serializeMessage(client.getCommunicator().getOutgoing());
        }
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        buffer.put(ClientMessages.PLAYER_ROTATION);
        buffer.putInt(playerId);
        buffer.putEnum(rotation);
    }
}