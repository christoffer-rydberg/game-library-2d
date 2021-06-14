package com.gamelibrary2d.updates;

import com.gamelibrary2d.components.denotations.Transformable;

public class PositionUpdate<T extends Transformable> extends AbstractAttributeUpdate<T> {

    private final float originalDeltaX;
    private final float originalDeltaY;

    private float deltaX;
    private float deltaY;

    public PositionUpdate(T target, float deltaX, float deltaY) {
        super(target);
        this.originalDeltaX = deltaX;
        this.originalDeltaY = deltaY;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public void makeAbsolute() {
        deltaX = originalDeltaX - getTarget().getPosition().getX();
        deltaY = originalDeltaY - getTarget().getPosition().getY();
    }

    @Override
    public void makeRelative(T target) {
        deltaX = originalDeltaX - getTarget().getPosition().getX() + target.getPosition().getX();
        deltaY = originalDeltaY - getTarget().getPosition().getY() + target.getPosition().getY();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        getTarget().getPosition().add(deltaX * deltaTime, deltaY * deltaTime);
    }
}