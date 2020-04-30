package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;

public class Boulder extends AbstractClientObject {

    private float rotationSpeed;

    public Boulder(byte objectIdentifier, DemoFrameClient client, DataBuffer buffer) {
        super(objectIdentifier, client, false, buffer);
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
