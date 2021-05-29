package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.collision.Obstacle;
import com.gamelibrary2d.collision.handlers.BounceHandler;
import com.gamelibrary2d.collision.handlers.RestrictedAreaHandler;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;
import com.gamelibrary2d.network.common.Communicator;

public class ServerPlayer extends AbstractDemoServerObject implements Obstacle {
    public final static float MAX_SPEED = 100f;
    public final static float MAX_ACCELERATION = 200f;

    private final DemoGameLogic gameLogic;

    private final Communicator communicator;

    private float acceleration;
    private float rotationAcceleration;
    private Point accelerationVector = new Point();

    public ServerPlayer(DemoGameLogic gameLogic, Communicator communicator, Rectangle bounds) {
        super(ObjectTypes.PLAYER);
        this.gameLogic = gameLogic;
        this.communicator = communicator;
        this.setBounds(bounds);
    }

    private void updateAccelerationVector() {
        accelerationVector.set(0f, acceleration);
        accelerationVector.rotate(getRotation());
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        updateAccelerationVector();
    }

    @Override
    public void setSecondaryType(byte secondaryType) {
        super.setSecondaryType(secondaryType);
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        boolean isLocal = communicator.getOutgoing() == buffer;
        buffer.putBool(isLocal);
        super.serializeMessage(buffer);
    }

    @Override
    public void update(float deltaTime) {
        if (isRotating()) {
            float rotationAcceleration = isAccelerating()
                    ? this.rotationAcceleration / 2f
                    : this.rotationAcceleration;

            setRotation(getRotation() + rotationAcceleration * deltaTime * 180);
        }

        if (isAccelerating()) {
            Point velocity = getVelocity();
            velocity.add(
                    accelerationVector.getX() * MAX_ACCELERATION * deltaTime,
                    accelerationVector.getY() * MAX_ACCELERATION * deltaTime);

            float speed = velocity.getLength();
            if (speed > MAX_SPEED) {
                velocity.multiply(MAX_SPEED / speed);
            }

            onVelocityChanged();
        }

        super.update(deltaTime);
    }

    public void setSpeedAndDirection(float speed, float direction) {
        super.setSpeedAndDirection(
                Math.min(MAX_SPEED, speed),
                direction);
    }

    @Override
    public float getMass() {
        return 1f;
    }

    @Override
    public void onPushed(Obstacle pusher, float accelerationX, float accelerationY) {
        if (pusher instanceof ServerObstacle) {
            gameLogic.destroy((ServerObstacle) pusher);
            gameLogic.destroy(this);
        } else {
            accelerate(accelerationX, accelerationY);
        }
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
        updateAccelerationVector();
    }

    public void setRotationAcceleration(float rotationAcceleration) {
        this.rotationAcceleration = rotationAcceleration;
    }

    @Override
    public void addCollisionDetection(CollisionDetection collisionDetection) {
        collisionDetection.add(
                this,
                new RestrictedAreaHandler<>(collisionDetection.getBounds(), ServerPlayer::accelerate),
                new BounceHandler<>(Obstacle.class)
        );
    }

    @Override
    public boolean isAccelerating() {
        return acceleration != 0f;
    }

    public boolean isRotating() {
        return rotationAcceleration != 0f;
    }
}
