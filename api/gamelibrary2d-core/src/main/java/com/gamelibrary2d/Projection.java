package com.gamelibrary2d;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.components.denotations.Transformable;

public class Projection {

    /**
     * Projects the input coordinates to the "self" coordinate system of the {@link Transformable} target.
     */
    public static void projectTo(Transformable target, float inputX, float inputY, Point output) {
        output.set(inputX, inputY);

        float rotationAndScaleCenterX = target.getPosition().getX() + target.getScaleAndRotationCenter().getX();
        float rotationAndScaleCenterY = target.getPosition().getY() + target.getScaleAndRotationCenter().getY();

        if (target.getRotation() != 0) {
            output.rotate(-target.getRotation(), rotationAndScaleCenterX, rotationAndScaleCenterY);
        }

        if (target.getScale().getX() != 1 || target.getScale().getY() != 1) {
            output.setX(rotationAndScaleCenterX + ((output.getX() - rotationAndScaleCenterX) / target.getScale().getX()));
            output.setY(rotationAndScaleCenterY + ((output.getY() - rotationAndScaleCenterY) / target.getScale().getY()));
        }

        output.setX(output.getX() - target.getPosition().getX());
        output.setY(output.getY() - target.getPosition().getY());
    }

    /**
     * Projects the input coordinates to the "self" coordinate system of the {@link Transformable} target.
     */
    public static void projectTo(Transformable target, Point input, Point output) {
        projectTo(target, input.getX(), input.getY(), output);
    }
}
