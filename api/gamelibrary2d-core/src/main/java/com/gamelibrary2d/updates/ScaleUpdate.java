package com.gamelibrary2d.updates;

import com.gamelibrary2d.components.denotations.Transformable;

public class ScaleUpdate<T extends Transformable> extends AbstractAttributeUpdate<T> {
    private final float originalDeltaX;
    private final float originalDeltaY;

    private float deltaX;
    private float deltaY;

    public ScaleUpdate(T target, float deltaScale) {
        this(target, deltaScale, deltaScale);
    }

    public ScaleUpdate(T target, float deltaX, float deltaY) {
        super(target);
        this.originalDeltaX = deltaX;
        this.originalDeltaY = deltaY;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public void makeAbsolute() {
        deltaX = originalDeltaX - getTarget().getScale().getX();
        deltaY = originalDeltaY - getTarget().getScale().getY();
    }

    @Override
    public void makeRelative(T target) {
        deltaX = originalDeltaX - getTarget().getScale().getX() + target.getScale().getX();
        deltaY = originalDeltaY - getTarget().getScale().getY() + target.getScale().getY();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        getTarget().getScale().add(deltaX * deltaTime, deltaY * deltaTime);
    }

}