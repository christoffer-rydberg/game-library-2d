package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

public class OpacityUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaOpacity;

    private float deltaOpacity;

    public OpacityUpdate(GameObject target, float deltaOpacity) {
        this(target, deltaOpacity, true);
    }

    public OpacityUpdate(GameObject target, float deltaOpacity, boolean scaleOverDuration) {
        super(target, scaleOverDuration);
        this.originalDeltaOpacity = deltaOpacity;
        this.deltaOpacity = deltaOpacity;
    }

    @Override
    public void makeAbsolute() {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity();
    }

    @Override
    public void makeRelative(GameObject target) {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity() + target.getOpacity();
    }

    @Override
    protected void onApply(float deltaTime) {
        getTarget().setOpacity(getTarget().getOpacity() + deltaOpacity * deltaTime);
    }
}