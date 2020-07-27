package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Rectangle;

public class CollidableInfo<T extends Collidable> {
    private final T collidable;

    private float deltaTime;
    private float prevX;
    private float prevY;

    private Rectangle prevBounds;

    public CollidableInfo(T collidable) {
        this.collidable = collidable;
    }

    public T getCollidable() {
        return collidable;
    }

    public float getX() {
        return collidable.getPosX();
    }

    public float getY() {
        return collidable.getPosY();
    }

    public float getPrevX() {
        return prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public float getSpeedX() {
        return deltaTime > 0f
                ? (collidable.getPosX() - prevX) / deltaTime
                : 0f;
    }

    public float getSpeedY() {
        return deltaTime > 0f
                ? (collidable.getPosY() - prevY) / deltaTime
                : 0f;
    }

    public Rectangle getPrevBounds() {
        return prevBounds;
    }

    void reset(float deltaTime, float prevX, float prevY, Rectangle prevBounds) {
        this.deltaTime = deltaTime;
        this.prevX = prevX;
        this.prevY = prevY;
        this.prevBounds = prevBounds;
    }
}
