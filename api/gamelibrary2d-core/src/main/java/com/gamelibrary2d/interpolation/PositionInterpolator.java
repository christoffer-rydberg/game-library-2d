package com.gamelibrary2d.interpolation;

import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.Transformable;

public class PositionInterpolator implements Updatable {
    private final Transformable target;

    private float x0;
    private float y0;
    private float x1;
    private float y1;

    private float timer;
    private float endTime = -1f;

    public PositionInterpolator(Transformable target) {
        this.target = target;
    }

    public void setGoal(float x, float y, float time) {
        x0 = target.getPosition().getX();
        y0 = target.getPosition().getY();
        x1 = x;
        y1 = y;
        timer = 0;
        endTime = time;
    }

    public void abort() {
        endTime = -1;
    }

    public void finish() {
        target.setPosition(x1, y1);
        endTime = -1;
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finish();
            } else {
                target.getPosition().lerp(x0, y0, x1, y1, timer / endTime);
            }
        }
    }
}