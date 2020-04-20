package com.gamelibrary2d.util;

import com.gamelibrary2d.markers.Updatable;

public class DirectionInterpolation implements Updatable {
    private final DirectionAware target;

    private float direction0;
    private float direction1;
    private float directionDelta;

    private float timer;
    private float endTime = -1f;

    public DirectionInterpolation(DirectionAware target) {
        this.target = target;
    }

    public void setGoal(float direction, float time) {
        direction0 = target.getDirection();
        direction1 = direction;
        // Always take the shortest way to reach the correct rotation
        directionDelta = direction1 - direction0;
        while (directionDelta < -180)
            directionDelta += 360;
        while (directionDelta > 180)
            directionDelta -= 360;
        timer = 0;
        endTime = time;
    }

    public void abort() {
        endTime = -1;
    }

    public void finish() {
        target.setDirection(direction1);
        endTime = -1;
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finish();
            } else {
                float direction = direction0 + directionDelta * (timer / endTime);
                target.setDirection(direction);
            }
        }
    }
}