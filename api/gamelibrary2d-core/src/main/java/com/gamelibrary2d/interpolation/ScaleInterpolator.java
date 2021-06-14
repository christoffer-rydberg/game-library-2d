package com.gamelibrary2d.interpolation;

import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.denotations.Transformable;

public class ScaleInterpolator implements Updatable {
    private final Transformable target;

    private float x0;
    private float y0;
    private float x1;
    private float y1;

    private float timer;
    private float endTime = -1f;

    public ScaleInterpolator(Transformable target) {
        this.target = target;
    }

    public void setGoal(float x, float y, float time) {
        x0 = target.getScale().getX();
        y0 = target.getScale().getY();
        x1 = x;
        y1 = y;
        timer = 0;
        endTime = time;
    }

    public void abort() {
        endTime = -1;
    }

    public void finish() {
        target.setScale(x1, y1);
        endTime = -1;
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finish();
            } else {
                target.getScale().lerp(x0, y0, x1, y1, timer / endTime);
            }
        }
    }
}