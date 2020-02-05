package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

public class RotationUpdate extends AbstractAttributeUpdate {

    private final float originalDeltaRotation;

    private float deltaRotation;

    public RotationUpdate(GameObject target, float deltaRotation) {
        this(target, deltaRotation, true);
    }

    public RotationUpdate(GameObject target, float deltaRotation, boolean scaleOverDuration) {
        super(target, scaleOverDuration);
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
    protected void onApply(float deltaTime) {
        getTarget().setRotation(getTarget().getRotation() + deltaRotation * deltaTime);
    }
}