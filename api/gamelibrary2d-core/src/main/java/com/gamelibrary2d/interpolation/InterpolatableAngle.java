package com.gamelibrary2d.interpolation;

import com.gamelibrary2d.markers.Updatable;

public class InterpolatableAngle implements Updatable {
    private float angle0;
    private float angle1;

    private float timer;
    private float endTime = -1f;

    private float angle;

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

    public void interpolate(float goal, float time) {
        angle0 = this.angle;
        angle1 = goal;

        // Always take the shortest way to reach the correct rotation
        while (angle1 - angle0 < -180) {
            angle1 += 360;
        }

        while (angle1 - angle0 > 180) {
            angle1 -= 360;
        }

        timer = 0;
        endTime = time;
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