package com.gamelibrary2d.interpolation;

import com.gamelibrary2d.markers.Updatable;

public class InterpolatableFloat implements Updatable {
    private float value0;
    private float value1;

    private float timer;
    private float endTime = -1f;

    private float value;

    public InterpolatableFloat() {

    }

    public InterpolatableFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        stopInterpolation();
    }

    public void interpolate(float goal, float time) {
        value0 = this.value;
        value1 = goal;
        timer = 0;
        endTime = time;
    }

    public void stopInterpolation() {
        endTime = -1;
    }

    public void finishInterpolation() {
        setValue(value1);
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finishInterpolation();
            } else {
                float alpha = timer / endTime;
                value = value0 * (1f - alpha) + value1 * alpha;
            }
        }
    }
}