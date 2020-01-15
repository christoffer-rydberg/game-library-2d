package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Point;

class InternalTargetSettings {
    private final Point position = new Point();
    private final Point scale = new Point(1f, 1f);
    private final Point scaleAndRotationCenter = new Point();
    private float rotation;

    public Point getPosition() {
        return position;
    }

    public Point getScale() {
        return scale;
    }

    public Point getScaleAndRotationCenter() {
        return scaleAndRotationCenter;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
