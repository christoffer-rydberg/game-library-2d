package com.gamelibrary2d.updates;

abstract class AbstractAttributeUpdate<T> implements AttributeUpdate<T> {

    private final T target;

    protected AbstractAttributeUpdate(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    @Override
    public void update(float deltaTime) {
        onUpdate(deltaTime);
    }

    protected abstract void onUpdate(float deltaTime);
}