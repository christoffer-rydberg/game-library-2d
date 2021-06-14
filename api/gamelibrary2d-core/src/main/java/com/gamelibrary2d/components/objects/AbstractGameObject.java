package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.glUtil.ModelMatrix;

public abstract class AbstractGameObject implements GameObject {
    private final Point position = new Point();
    private final Point scale = new Point(1, 1);
    private final Point scaleAndRotationCenter = new Point();

    private float rotation;
    private float opacity = 1.0f;
    private boolean enabled = true;

    public AbstractGameObject() {

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

    public float getPosX() {
        return position.getX();
    }

    public float getPosY() {
        return position.getY();
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

    @Override
    public final void render(float alpha) {
        if (isEnabled()) {
            onRenderUnprojected(alpha);
        }
    }

    protected void onRenderUnprojected(float alpha) {
        ModelMatrix.instance().pushMatrix();
        applyProjection(ModelMatrix.instance());
        onRender(alpha * opacity);
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

    private void applyProjection(ModelMatrix matrix) {
        float centerX = getScaleAndRotationCenter().getX();
        float centerY = getScaleAndRotationCenter().getY();

        matrix.translatef(position.getX() + centerX, position.getY() + centerY, 0);

        matrix.rotatef(-getRotation(), 0, 0, 1);

        matrix.scalef(scale.getX(), scale.getY(), 1.0f);

        if (centerX != 0 && centerY != 0) {
            matrix.translatef(-centerX, -centerY, 0);
        }
    }

    protected abstract void onRender(float alpha);
}