package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;

public class Projection {
    // TODO: Don't reuse the same instance and update below documentation.
    private final static Point point = new Point();

    /**
     * Projects the specified coordinates to the "self" coordinate system of the {@link Transformable}.
     * NOTE: This method always returns the same {@link Point} instance. This is not thread safe and it is not safe to
     * store the return value (as it will change whenever this method is called). This is a temporary optimization
     * while awaiting Java value types (which hopefully will happen any year now).
     */
    public static Point projectTo(Transformable ref, float x, float y) {
        point.set(x, y);

        float rotationAndScaleCenterX = ref.getPosition().getX() + ref.getScaleAndRotationCenter().getX();
        float rotationAndScaleCenterY = ref.getPosition().getY() + ref.getScaleAndRotationCenter().getY();

        if (ref.getRotation() != 0) {
            point.rotate(-ref.getRotation(), rotationAndScaleCenterX, rotationAndScaleCenterY);
        }

        if (ref.getScale().getX() != 1 || ref.getScale().getY() != 1) {
            point.setX(rotationAndScaleCenterX + ((point.getX() - rotationAndScaleCenterX) / ref.getScale().getX()));
            point.setY(rotationAndScaleCenterY + ((point.getY() - rotationAndScaleCenterY) / ref.getScale().getY()));
        }

        point.setX(point.getX() - ref.getPosition().getX());
        point.setY(point.getY() - ref.getPosition().getY());

        return point;
    }
}