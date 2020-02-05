package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

public class ScaleUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaX;
    private final float originalDeltaY;

    private float deltaX;
    private float deltaY;

    public ScaleUpdate(GameObject target, float deltaScale) {
        this(target, deltaScale, deltaScale);
    }

    public ScaleUpdate(GameObject target, float deltaScale, boolean scaleOverDuration) {
        this(target, deltaScale, deltaScale, scaleOverDuration);
    }

    public ScaleUpdate(GameObject target, float deltaX, float deltaY) {
        this(target, deltaX, deltaY, true);
    }

    public ScaleUpdate(GameObject target, float deltaX, float deltaY, boolean scaleOverDuration) {
        super(target, scaleOverDuration);
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
    public void makeRelative(GameObject target) {
        deltaX = originalDeltaX - getTarget().getScale().getX() + target.getScale().getX();
        deltaY = originalDeltaY - getTarget().getScale().getY() + target.getScale().getY();
    }

    @Override
    protected void onApply(float deltaTime) {
        getTarget().getScale().add(deltaX * deltaTime, deltaY * deltaTime);
    }

}