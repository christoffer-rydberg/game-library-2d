package com.gamelibrary2d.updates;

import com.gamelibrary2d.components.denotations.Opacifiable;

public class OpacityUpdate<T extends Opacifiable> extends AbstractAttributeUpdate<T> {

    private final float originalDeltaOpacity;

    private float deltaOpacity;

    public OpacityUpdate(T target, float deltaOpacity) {
        super(target);
        this.originalDeltaOpacity = deltaOpacity;
        this.deltaOpacity = deltaOpacity;
    }

    @Override
    public void makeAbsolute() {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity();
    }

    @Override
    public void makeRelative(T target) {
        deltaOpacity = originalDeltaOpacity - getTarget().getOpacity() + target.getOpacity();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        getTarget().setOpacity(getTarget().getOpacity() + deltaOpacity * deltaTime);
    }
}