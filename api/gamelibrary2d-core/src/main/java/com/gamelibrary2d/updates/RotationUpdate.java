package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

public class RotationUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaRotation;

    private float deltaRotation;

    public RotationUpdate(GameObject target, float deltaRotation) {
        super(target);
        this.originalDeltaRotation = deltaRotation;
        this.deltaRotation = deltaRotation;
    }

    @Override
    public void makeAbsolute() {
        deltaRotation = originalDeltaRotation - getTarget().getRotation();
    }

    @Override
    public void makeRelative(GameObject target) {
        deltaRotation = originalDeltaRotation - getTarget().getRotation() + target.getRotation();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        getTarget().setRotation(getTarget().getRotation() + deltaRotation * deltaTime);
    }
}