package com.gamelibrary2d.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.updates.UpdateObject;

public abstract class AbstractGameObject implements GameObject, UpdateObject {

    private final Point position = new Point();
    private final Point scale = new Point(1, 1);
    private final Point scaleAndRotationCenter = new Point();

    private float rotation;
    private float opacity = 1.0f;

    private Rectangle bounds = Rectangle.EMPTY;
    private boolean enabled = true;

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

    public boolean isPixelVisible(float projectedX, float projectedY) {
        return getBounds().isInside(projectedX, projectedY);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    protected void setBounds(Rectangle bounds) {
        this.bounds = bounds;
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

    protected abstract void onRenderProjected(float alpha);
}