package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.updating.Updatable;

public class AbstractWrapperObject<T extends GameObject>
        implements GameObject, MouseAware, KeyAware, Updatable, Clearable {

    private T wrapped;

    protected AbstractWrapperObject() {

    }

    protected AbstractWrapperObject(T wrapped) {
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
    public boolean isFocused() {
        if (wrapped instanceof KeyAware) {
            return ((KeyAware) wrapped).isFocused();
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).setFocused(focused);
        }
    }

    @Override
    public Rectangle getBounds() {
        return wrapped.getBounds();
    }

    @Override
    public void charInputEvent(char charInput) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).charInputEvent(charInput);
        }
    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyDownEvent(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void keyReleaseEvent(int key, int scanCode, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).keyReleaseEvent(key, scanCode, mods);
        }
    }

    @Override
    public void mouseEventFinished(int button, int action, int mods) {
        if (wrapped instanceof KeyAware) {
            ((KeyAware) wrapped).mouseEventFinished(button, action, mods);
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
    public boolean isPixelVisible(float projectedX, float projectedY) {
        return wrapped.isPixelVisible(projectedX, projectedY);
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

    @Override
    public boolean mouseButtonDownEvent(int button, int mods, float projectedX, float projectedY) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).mouseButtonDownEvent(button, mods, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public boolean mouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).mouseMoveEvent(projectedX, projectedY, drag);
        }
        return false;
    }

    @Override
    public boolean mouseButtonReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        if (wrapped instanceof MouseAware) {
            return ((MouseAware) wrapped).mouseButtonReleaseEvent(button, mods, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        if (wrapped instanceof Updatable) {
            ((Updatable) wrapped).update(deltaTime);
        }
    }

    @Override
    public void clear() {
        if (wrapped instanceof Clearable) {
            ((Clearable) wrapped).clear();
        }
    }

    @Override
    public boolean isAutoClearing() {
        if (wrapped instanceof Clearable) {
            return ((Clearable) wrapped).isAutoClearing();
        }
        return false;
    }
}
