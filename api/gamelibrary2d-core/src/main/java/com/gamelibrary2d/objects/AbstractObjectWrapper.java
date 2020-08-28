package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.markers.Updatable;

public class AbstractObjectWrapper<T extends GameObject>
        implements GameObject, MouseAware, KeyAware, Updatable {

    private T wrapped;

    protected AbstractObjectWrapper() {

    }

    protected AbstractObjectWrapper(T wrapped) {
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
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).charInput(charInput);
        }
    }

    @Override
    public void keyDown(int key, int scanCode, boolean repeat, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyReleased(key, scanCode, mods);
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
    public boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).mouseButtonDown(button, mods, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public boolean mouseMove(float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).mouseMove(x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (wrapped instanceof MouseAware) {
            ((MouseAware) wrapped).mouseButtonReleased(button, mods, x, y, projectedX, projectedY);
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
