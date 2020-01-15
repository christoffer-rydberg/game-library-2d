package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.glUtil.ModelMatrix;

/**
 * Abstract implementation of a {@link com.gamelibrary2d.objects.GameObject
 * GameObject}. It contains methods to position, scale and rotate the rendered
 * object, as well as changing opacity or completely disabling it. The purpose
 * of this class is to provide the base implementation of a game object that
 * does not require mouse or keyboard input. If the object should handle input,
 * or at least be able to, consider extending the {@link AbstractInputObject}
 * class instead. It is also possible to implement {@link MouseAware} and
 * {@link KeyAware}, but this requires more own code.
 *
 * @author Christoffer Rydberg
 */
public abstract class AbstractGameObject implements GameObject {

    private final Point position = new Point();
    private final Point scale = new Point(1, 1);
    private final Point scaleAndRotationCenter = new Point();

    private float rotation;
    private float opacity = 1.0f;
    private boolean enabled = true;

    private Rectangle bounds = Rectangle.EMPTY;

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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
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
    public void render(float alpha) {
        if (isEnabled()) {
            ModelMatrix.instance().pushMatrix();
            projectTo();
            onRender(alpha * opacity);
            ModelMatrix.instance().popMatrix();
        }
    }

    protected void projectTo() {
        float centerX = getScaleAndRotationCenter().getX();
        float centerY = getScaleAndRotationCenter().getY();

        ModelMatrix.instance().translatef(position.getX() + centerX, position.getY() + centerY, 0);

        ModelMatrix.instance().rotatef(-getRotation(), 0, 0, 1);

        ModelMatrix.instance().scalef(scale.getX(), scale.getY(), 1.0f);

        if (centerX != 0 && centerY != 0) {
            ModelMatrix.instance().translatef(-centerX, -centerY, 0);
        }
    }

    protected abstract void onRender(float alpha);
}