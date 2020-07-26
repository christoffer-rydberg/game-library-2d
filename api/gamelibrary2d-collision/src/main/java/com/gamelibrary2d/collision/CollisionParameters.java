package com.gamelibrary2d.collision;

public class CollisionParameters {
    int attempt;

    private float deltaTime;
    private float prevX;
    private float prevY;
    private float deltaX;
    private float deltaY;
    private float speed = Float.MAX_VALUE;
    private float direction = Float.MAX_VALUE;

    public int getAttempt() {
        return attempt;
    }

    public float getPrevX() {
        return prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public float getSpeed() {
        if (speed == Float.MAX_VALUE) {
            speed = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY) / deltaTime);
        }

        return speed;
    }

    public float getDirection() {
        if (direction == Float.MAX_VALUE) {
            this.direction = (float) Math.atan2(deltaX, deltaY);
        }

        return direction;
    }

    void reset(float deltaTime, float prevX, float prevY, float x, float y) {
        this.deltaTime = deltaTime;
        this.prevX = prevX;
        this.prevY = prevY;
        this.deltaX = x - prevX;
        this.deltaY = y - prevY;
        speed = Float.MAX_VALUE;
        direction = Float.MAX_VALUE;
    }
}
