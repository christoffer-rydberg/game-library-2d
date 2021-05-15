package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.InputAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.markers.Updatable;

public class AbstractGameObjectWrapper<T extends GameObject>
        implements GameObject, PointerAware, KeyAware, InputAware, Updatable {

    private T wrapped;

    protected AbstractGameObjectWrapper() {

    }

    protected AbstractGameObjectWrapper(T wrapped) {
        this.wrapped = wrapped;
    }

    protected T getWrapped() {
        return wrapped;
    }

    protected void setWrapped(T wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void render(float alpha) {
        wrapped.render(alpha);
    }

    @Override
    public Rectangle getBounds() {
        return wrapped.getBounds();
    }

    @Override
    public void charInput(char charInput) {
        if (wrapped instanceof InputAware) {
            ((InputAware) wrapped).charInput(charInput);
        }
    }

    @Override
    public void keyDown(int key, boolean repeat) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyDown(key, repeat);
        }
    }

    @Override
    public void keyUp(int key) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyUp(key);
        }
    }

    @Override
    public Point getPosition() {
        return wrapped.getPosition();
    }

    @Override
    public Point getScale() {
        return wrapped.getScale();
    }

    @Override
    public Point getScaleAndRotationCenter() {
        return wrapped.getScaleAndRotationCenter();
    }

    @Override
    public float getRotation() {
        return wrapped.getRotation();
    }

    @Override
    public void setRotation(float rotation) {
        wrapped.setRotation(rotation);
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof PointerAware) {
            return ((PointerAware) wrapped).pointerDown(id, button, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof PointerAware) {
            return ((PointerAware) wrapped).pointerMove(id, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof PointerAware) {
            ((PointerAware) wrapped).pointerUp(id, button, x, y, projectedX, projectedY);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (wrapped instanceof Updatable) {
            ((Updatable) wrapped).update(deltaTime);
        }
    }

    @Override
    public boolean isEnabled() {
        return wrapped.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        wrapped.setEnabled(enabled);
    }

    @Override
    public float getOpacity() {
        return wrapped.getOpacity();
    }

    @Override
    public void setOpacity(float opacity) {
        wrapped.setOpacity(opacity);
    }
}
