package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;

public class Obstacle extends AbstractClientObject {
    private float rotationSpeed;

    public Obstacle(byte primaryType, DemoFrameClient client, DataBuffer buffer) {
        super(primaryType, client, false, buffer);
        randomizeRotationSpeed();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setRotation(getRotation() + rotationSpeed * deltaTime);
    }

    @Override
    public void setDirection(float direction) {
        if (getDirection() != direction) {
            super.setDirection(direction);
            randomizeRotationSpeed();
        }
    }

    private void randomizeRotationSpeed() {
        rotationSpeed = RandomInstance.random11() * 180f;
    }
}
