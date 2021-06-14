package com.gamelibrary2d.updates;

import com.gamelibrary2d.components.denotations.Transformable;

public class RotationUpdate<T extends Transformable> extends AbstractAttributeUpdate<T> {

    private final float originalDeltaRotation;

    private float deltaRotation;

    public RotationUpdate(T target, float deltaRotation) {
        super(target);
        this.originalDeltaRotation = deltaRotation;
        this.deltaRotation = deltaRotation;
    }

    @Override
    public void makeAbsolute() {
        deltaRotation = originalDeltaRotation - getTarget().getRotation();
    }

    @Override
    public void makeRelative(T target) {
        deltaRotation = originalDeltaRotation - getTarget().getRotation() + target.getRotation();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        getTarget().setRotation(getTarget().getRotation() + deltaRotation * deltaTime);
    }
}