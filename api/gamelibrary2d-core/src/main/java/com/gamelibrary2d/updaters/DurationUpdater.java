package com.gamelibrary2d.updaters;

import com.gamelibrary2d.markers.Updatable;

public class DurationUpdater implements Updater {
    private final Updatable update;
    private final float duration;
    private final boolean scaleOverDuration;

    private float timeLeft;

    /**
     * @param duration The duration of the updater.
     * @param update   The update to invoke.
     */
    public DurationUpdater(float duration, Updatable update) {
        this(duration, false, update);
    }

    /**
     * @param duration          The duration of the updater.
     * @param scaleOverDuration Scales the update over the duration of the updater
     *                          by dividing the delta time of each update by the duration.
     * @param update            The update to invoke.
     */
    public DurationUpdater(float duration, boolean scaleOverDuration, Updatable update) {
        this.update = update;
        this.duration = duration;
        this.scaleOverDuration = scaleOverDuration;
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
            return 0f;
        }

        float usedTime = Math.min(timeLeft, deltaTime);
        update.update(scaleOverDuration ? usedTime / duration : usedTime);
        timeLeft -= usedTime;
        return usedTime;
    }
}