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
    public void onCharInput(char charInput) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).onCharInput(charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).onKeyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).onKeyRelease(key, scanCode, mods);
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
    public boolean onMouseButtonDown(int button, int mods, float x, float y) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).onMouseButtonDown(button, mods, x, y);
        }
        return false;
    }

    @Override
    public boolean onMouseMove(float x, float y) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).onMouseMove(x, y);
        }
        return false;
    }

    @Override
    public void onMouseButtonRelease(int button, int mods, float x, float y) {
        if (wrapped instanceof MouseAware) {
            ((MouseAware) wrapped).onMouseButtonRelease(button, mods, x, y);
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
