package com.gamelibrary2d.updaters;

import com.gamelibrary2d.updates.Update;

public class DurationUpdater implements Updater {

    private final Update update;

    private final float duration;

    private float timeLeft;

    public DurationUpdater(Update update, float duration) {
        this.update = update;
        this.duration = duration;
        timeLeft = duration;
    }

    @Override
    public void reset() {
        timeLeft = duration;
    }

    @Override
    public boolean isFinished() {
        return timeLeft <= 0;
    }

    @Override
    public float update(float deltaTime) {

        if (isFinished()) {
            return deltaTime;
        }

        float usedTime = Math.min(timeLeft, deltaTime);

        update.apply(usedTime, Math.min(usedTime / duration, 1f));

        timeLeft -= usedTime;

        return usedTime;
    }
}