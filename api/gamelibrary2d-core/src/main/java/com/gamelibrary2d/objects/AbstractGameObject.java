package com.gamelibrary2d.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.markers.KeyAware;

public abstract class AbstractGameObject<T extends Renderable> implements KeyAware, GameObject {
    private final Point position = new Point();
    private final Point scale = new Point(1, 1);
    private final Point scaleAndRotationCenter = new Point();

    private T content;
    private float rotation;
    private float opacity = 1.0f;
    private boolean enabled = true;
    private Rectangle bounds;

    public AbstractGameObject() {

    }

    public AbstractGameObject(T content) {
        this.content = content;
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : getContentBounds();
    }

    protected void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    private Rectangle getContentBounds() {
        if (content instanceof Bounded)
            return ((Bounded) content).getBounds();
        else
            return Rectangle.EMPTY;
    }

    @Override
    public void onCharInput(char charInput) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).onCharInput(charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).onKeyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).onKeyRelease(key, scanCode, mods);
        }
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Point getScale() {
        return scale;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public Point getScaleAndRotationCenter() {
        return scaleAndRotationCenter;
    }

    protected T getContent() {
        return content;
    }

    protected void setContent(T content) {
        this.content = content;
    }

    @Override
    public final void render(float alpha) {
        if (enabled) {
            onRender(alpha);
        }
    }

    protected void onRender(float alpha) {
        ModelMatrix.instance().pushMatrix();
        projectTo();
        onRenderProjected(alpha * opacity);
        ModelMatrix.instance().popMatrix();
    }

    protected void onRenderProjected(float alpha) {
        if (content != null) {
            content.render(alpha);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (!enabled) {
                FocusManager.unfocus(this, true);
            }
        }
    }

    private void projectTo() {
        float centerX = getScaleAndRotationCenter().getX();
        float centerY = getScaleAndRotationCenter().getY();

        ModelMatrix.instance().translatef(position.getX() + centerX, position.getY() + centerY, 0);

        ModelMatrix.instance().rotatef(-getRotation(), 0, 0, 1);

        ModelMatrix.instance().scalef(scale.getX(), scale.getY(), 1.0f);

        if (centerX != 0 && centerY != 0) {
            ModelMatrix.instance().translatef(-centerX, -centerY, 0);
        }
    }
}