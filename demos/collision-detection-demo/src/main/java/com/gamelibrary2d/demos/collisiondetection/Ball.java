package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.collision.Obstacle;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.AbstractGameObject;

public class Ball extends AbstractGameObject<Renderable> implements Obstacle {
    private final static float RADIUS = 16f;
    private final static float FRICTION = 5f;
    private final static float AIR_RESISTANCE_FACTOR = 0.047f;
    private final Point velocity = new Point();
    private float mass = 1f;

    public Ball(Renderable renderer, float x, float y) {
        setContent(renderer);
        setPosition(x, y);
    }

    public void setSpeedAndDirection(float speed, float direction) {
        velocity.set(0, speed);
        velocity.rotate(direction);
    }

    @Override
    public void reposition(float x, float y) {
        setPosition(x, y);
    }

    public void accelerate(float x, float y) {
        velocity.add(x, y);
    }

    @Override
    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    @Override
    public void onPushed(Obstacle pusher, float accelerationX, float accelerationY) {
        accelerate(accelerationX, accelerationY);
    }

    @Override
    public void update(float deltaTime) {
        updateVelocity(deltaTime);
        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
    }

    private void updateVelocity(float deltaTime) {
        float speed2 = velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY();
        if (speed2 > 0f) {
            float airResistanceDeceleration = AIR_RESISTANCE_FACTOR * speed2 / RADIUS;
            float totalDeceleration = airResistanceDeceleration + FRICTION;
            double speed = Math.sqrt(speed2);
            double newSpeed = speed - totalDeceleration * deltaTime;
            if (newSpeed <= 0) {
                velocity.set(0, 0);
            } else {
                velocity.multiply((float) (newSpeed / speed), (float) (newSpeed / speed));
            }
        }
    }

    @Override
    public boolean canCollide() {
        return true;
    }
}