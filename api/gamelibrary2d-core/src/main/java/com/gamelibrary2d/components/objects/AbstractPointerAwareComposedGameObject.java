package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.components.denotations.PointerAware;

public class AbstractPointerAwareComposedGameObject<T extends GameObject>
        extends AbstractComposedGameObject<T> implements PointerAware {

    protected AbstractPointerAwareComposedGameObject() {

    }

    protected AbstractPointerAwareComposedGameObject(T content) {
        super(content);
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            return ((PointerAware) content).pointerDown(id, button, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            return ((PointerAware) content).pointerMove(id, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            ((PointerAware) content).pointerUp(id, button, x, y, projectedX, projectedY);
        }
    }
}
