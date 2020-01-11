package com.gamelibrary2d.updates;

abstract class AbstractAttributeUpdate implements AttributeUpdate {

    private final UpdateObject target;

    private final boolean scaleOverDuration;

    protected AbstractAttributeUpdate(UpdateObject target, boolean scaleOverDuration) {
        this.target = target;
        this.scaleOverDuration = scaleOverDuration;
    }

    public UpdateObject getTarget() {
        return target;
    }

    @Override
    public void apply(float deltaTime, float scaledDeltaTime) {
        onApply(scaleOverDuration ? scaledDeltaTime : deltaTime);
    }

    protected abstract void onApply(float deltaTime);
}