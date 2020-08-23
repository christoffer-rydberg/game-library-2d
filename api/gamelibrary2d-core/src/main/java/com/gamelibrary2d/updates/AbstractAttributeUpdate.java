package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

abstract class AbstractAttributeUpdate implements AttributeUpdate {

    private final GameObject target;

    protected AbstractAttributeUpdate(GameObject target) {
        this.target = target;
    }

    public GameObject getTarget() {
        return target;
    }

    @Override
    public void update(float deltaTime) {
        onUpdate(deltaTime);
    }

    protected abstract void onUpdate(float deltaTime);
}