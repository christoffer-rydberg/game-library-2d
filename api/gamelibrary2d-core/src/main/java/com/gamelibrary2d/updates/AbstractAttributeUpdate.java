package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

abstract class AbstractAttributeUpdate implements AttributeUpdate {

    private final GameObject target;

    private final boolean scaleOverDuration;

    protected AbstractAttributeUpdate(GameObject target, boolean scaleOverDuration) {
        this.target = target;
        this.scaleOverDuration = scaleOverDuration;
    }

    public GameObject getTarget() {
        return target;
    }

    @Override
    public void apply(float deltaTime, float scaledDeltaTime) {
        onApply(scaleOverDuration ? scaledDeltaTime : deltaTime);
    }

    protected abstract void onApply(float deltaTime);
}