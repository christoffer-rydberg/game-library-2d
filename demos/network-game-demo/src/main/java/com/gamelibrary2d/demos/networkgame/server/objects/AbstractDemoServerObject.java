package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.network.AbstractServerObject;

public abstract class AbstractDemoServerObject extends AbstractServerObject implements DemoServerObject, Collidable {
    private final byte objectIdentifier;
    private final Point velocity = new Point();

    private boolean destroyed;
    private float speed;
    private float direction;

    protected AbstractDemoServerObject(byte objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    @Override
    public byte getObjectIdentifier() {
        return objectIdentifier;
    }

    @Override
    public void update(float deltaTime) {
        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
    }

    @Override
    public boolean canCollide() {
        return !isDestroyed();
    }

    @Override
    public void onDestroyed() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public float getDirection() {
        return direction;
    }

    public void reposition(float x, float y) {
        getPosition().set(x, y);
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    protected void setSpeedAndDirection(float speed, float direction) {
        this.speed = speed;
        this.direction = normalizeDirection(direction);
        velocity.set(0, speed);
        velocity.rotate(direction);
    }

    protected void setSpeed(float x, float y) {
        velocity.set(x, y);
        speed = velocity.getLength();
        direction = normalizeDirection(velocity.getAngleDegrees());
    }

    protected void accelerate(float x, float y) {
        setSpeed(velocity.getX() + x, velocity.getY() + y);
    }

    private float normalizeDirection(float direction) {
        return (((direction % 360f) + 360f) % 360f);
    }

    @Override
    public float getPosX() {
        return getPosition().getX();
    }

    @Override
    public float getPosY() {
        return getPosition().getY();
    }
}
