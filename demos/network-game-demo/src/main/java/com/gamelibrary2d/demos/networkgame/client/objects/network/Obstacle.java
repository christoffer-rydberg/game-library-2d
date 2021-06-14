package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.client.frames.game.GameFrameClient;

public class Obstacle extends AbstractClientObject {
    private float rotationSpeed;
    private float goalDirection;

    public Obstacle(byte primaryType, GameFrameClient client, DataBuffer buffer) {
        super(primaryType, client, buffer);
        randomizeRotationSpeed();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setRotation(getRotation() + rotationSpeed * deltaTime);
    }

    @Override
    public void setGoalDirection(float direction) {
        if (goalDirection != direction) {
            goalDirection = direction;
            super.setGoalDirection(direction);
            randomizeRotationSpeed();
        }
    }

    private void randomizeRotationSpeed() {
        rotationSpeed = RandomInstance.random11() * 180f;
    }
}
