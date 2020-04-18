package com.gamelibrary2d.network;

import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.Transformable;

public class RotationInterpolator implements Updatable {
    private final Transformable target;

    private float rotation0;
    private float rotation1;
    private float rotationDelta;

    private float timer;
    private float endTime = -1f;

    public RotationInterpolator(Transformable target) {
        this.target = target;
    }

    public void setGoal(float rotation, float time) {
        rotation0 = target.getRotation();
        rotation1 = rotation;
        // Always take the shortest way to reach the correct rotation
        rotationDelta = rotation1 - rotation0;
        while (rotationDelta < -180)
            rotationDelta += 360;
        while (rotationDelta > 180)
            rotationDelta -= 360;
        timer = 0;
        endTime = time;
    }

    public void abort() {
        endTime = -1;
    }

    public void finish() {
        target.setRotation(rotation1);
        endTime = -1;
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finish();
            } else {
                float rotation = rotation0 + rotationDelta * (timer / endTime);
                target.setRotation(rotation);
            }
        }
    }
}