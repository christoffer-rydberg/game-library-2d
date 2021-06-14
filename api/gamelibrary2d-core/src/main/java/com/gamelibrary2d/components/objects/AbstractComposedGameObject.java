package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;

public class AbstractComposedGameObject<T extends GameObject> implements GameObject {

    private T content;

    protected AbstractComposedGameObject() {

    }

    protected AbstractComposedGameObject(T content) {
        this.content = content;
    }

    protected T getContent() {
        return content;
    }

    protected void setContent(T content) {
        this.content = content;
    }

    @Override
    public void render(float alpha) {
        content.render(alpha);
    }

    @Override
    public Rectangle getBounds() {
        return content.getBounds();
    }

    @Override
    public Point getPosition() {
        return content.getPosition();
    }

    @Override
    public Point getScale() {
        return content.getScale();
    }

    @Override
    public Point getScaleAndRotationCenter() {
        return content.getScaleAndRotationCenter();
    }

    @Override
    public float getRotation() {
        return content.getRotation();
    }

    @Override
    public void setRotation(float rotation) {
        content.setRotation(rotation);
    }

    @Override
    public boolean isEnabled() {
        return content.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        content.setEnabled(enabled);
    }

    @Override
    public float getOpacity() {
        return content.getOpacity();
    }

    @Override
    public void setOpacity(float opacity) {
        content.setOpacity(opacity);
    }
}
