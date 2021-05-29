package com.gamelibrary2d.interpolation;

import com.gamelibrary2d.common.FloatUtils;
import com.gamelibrary2d.markers.Updatable;

public class InterpolatableAngle implements Updatable {
    private float angle;
    private float angle0;
    private float angle1;
    private float timer;
    private float endTime = -1f;

    public InterpolatableAngle() {

    }

    public InterpolatableAngle(float angle) {
        this.angle = angle;
    }


    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        stopInterpolation();
    }

    public void beginInterpolation(float goal, float duration) {
        timer = 0;
        endTime = duration;
        angle0 = angle;
        angle1 = angle + FloatUtils.normalizeDegrees(goal - angle);
    }

    public void stopInterpolation() {
        endTime = -1;
    }

    public void finishInterpolation() {
        setAngle(angle1);
    }

    @Override
    public void update(float deltaTime) {
        if (endTime >= 0) {
            timer += deltaTime;
            if (timer >= endTime) {
                finishInterpolation();
            } else {
                float alpha = timer / endTime;
                angle = angle0 * (1f - alpha) + angle1 * alpha;
            }
        }
    }
}