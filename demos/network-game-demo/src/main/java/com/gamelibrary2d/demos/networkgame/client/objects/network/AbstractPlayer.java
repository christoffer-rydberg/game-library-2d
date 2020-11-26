package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.GameFrameClient;
import com.gamelibrary2d.interpolation.RotationInterpolator;

public abstract class AbstractPlayer extends AbstractClientObject {
    private final GameFrameClient client;
    private final RotationInterpolator rotationInterpolator = new RotationInterpolator(this);

    protected AbstractPlayer(byte primaryType, GameFrameClient client, DataBuffer buffer) {
        super(primaryType, client, buffer);
        this.client = client;
        getParticleHotspot().set(0, -30);
    }

    @Override
    public void setGoalRotation(float rotation) {
        rotationInterpolator.setGoal(rotation, 1f / client.getServerUpdatesPerSecond());
    }

    @Override
    public void update(float deltaTime) {
        rotationInterpolator.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    protected boolean useUpdateEffect() {
        return super.useUpdateEffect() && isAccelerating();
    }
}
