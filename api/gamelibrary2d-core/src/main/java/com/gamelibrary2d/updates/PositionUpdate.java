package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

public class PositionUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaX;
    private final float originalDeltaY;

    private float deltaX;
    private float deltaY;

    public PositionUpdate(GameObject target, float deltaX, float deltaY) {
        this(target, deltaX, deltaY, true);
    }

    public PositionUpdate(GameObject target, float deltaX, float deltaY, boolean scaleOverDuration) {
        super(target, scaleOverDuration);
        this.originalDeltaX = deltaX;
        this.originalDeltaY = deltaY;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public void makeAbsolute() {
        deltaX = originalDeltaX - getTarget().position().getX();
        deltaY = originalDeltaY - getTarget().position().getY();
    }

    public void makeRelative(GameObject target) {
        deltaX = originalDeltaX - getTarget().position().getX() + target.position().getX();
        deltaY = originalDeltaY - getTarget().position().getY() + target.position().getY();
    }

    @Override
    protected void onApply(float deltaTime) {
        getTarget().position().add(deltaX * deltaTime, deltaY * deltaTime);
    }
}