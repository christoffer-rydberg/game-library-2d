package com.gamelibrary2d.updates;

public class OpacityUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaOpacity;

    private float deltaOpacity;

    public OpacityUpdate(UpdateObject target, float deltaOpacity) {
        this(target, deltaOpacity, true);
    }

    public OpacityUpdate(UpdateObject target, float deltaOpacity, boolean scaleOverDuration) {
        super(target, scaleOverDuration);
        this.originalDeltaOpacity = deltaOpacity;
        this.deltaOpacity = deltaOpacity;
    }

    @Override
    public void makeAbsolute() {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity();
    }

    @Override
    public void makeRelative(UpdateObject target) {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity() + target.getOpacity();
    }

    @Override
    protected void onApply(float deltaTime) {
        getTarget().setOpacity(getTarget().getOpacity() + deltaOpacity * deltaTime);
    }
}