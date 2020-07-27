package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.collision.Obstacle;
import com.gamelibrary2d.collision.handlers.BounceHandler;
import com.gamelibrary2d.collision.handlers.RestrictedAreaHandler;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.common.RotationDirection;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;
import com.gamelibrary2d.network.common.Communicator;

public class ServerPlayer extends AbstractDemoServerObject implements Obstacle {
    private final DemoGameLogic gameLogic;

    private final Communicator communicator;

    private RotationDirection rotationDirection = RotationDirection.NONE;

    public ServerPlayer(DemoGameLogic gameLogic, Communicator communicator, Rectangle bounds) {
        super(ObjectIdentifiers.PLAYER);
        this.gameLogic = gameLogic;
        this.communicator = communicator;
        this.setBounds(bounds);
        setSpeedAndDirection(100f, RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        var isLocal = communicator.getOutgoing() == buffer;
        buffer.putBool(isLocal);
        super.serializeMessage(buffer);
    }

    @Override
    public void update(float deltaTime) {
        updateRotation(deltaTime);
        super.update(deltaTime);
    }

    private void updateRotation(float deltaTime) {
        switch (rotationDirection) {
            case NONE:
                break;
            case LEFT:
                setDirection(getDirection() - deltaTime * 180);
                break;
            case RIGHT:
                setDirection(getDirection() + deltaTime * 180);
                break;
        }
    }

    public void setDirection(float direction) {
        super.setSpeedAndDirection(getSpeed(), direction);
    }

    @Override
    public float getMass() {
        return 1f;
    }

    @Override
    public void onPushed(Obstacle pusher, float accelerationX, float accelerationY) {
        if (pusher instanceof ServerBoulder) {
            gameLogic.destroy((ServerBoulder) pusher);
            gameLogic.destroy(this);
        } else {
            accelerate(accelerationX, accelerationY);
        }
    }

    public void setRotationDirection(RotationDirection rotationDirection) {
        this.rotationDirection = rotationDirection;
    }

    @Override
    public void addCollisionDetection(CollisionDetection collisionDetection) {
        collisionDetection.add(
                this,
                new RestrictedAreaHandler<>(collisionDetection.getBounds(), ServerPlayer::accelerate),
                new BounceHandler<>(Obstacle.class)
        );
    }
}
