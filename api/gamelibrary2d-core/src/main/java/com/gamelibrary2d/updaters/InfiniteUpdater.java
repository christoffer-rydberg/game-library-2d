package com.gamelibrary2d.updaters;

import com.gamelibrary2d.components.denotations.Updatable;

public class InfiniteUpdater implements Updater {

    private final Updatable onUpdate;

    private boolean aborted;

    public InfiniteUpdater(Updatable onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    public void reset() {
        aborted = false;
    }

    public void abort() {
        aborted = true;
    }

    @Override
    public boolean isFinished() {
        return aborted;
    }

    @Override
    public float update(float deltaTime) {
        if (aborted) {
            return 0f;
        }

        onUpdate.update(deltaTime);

        return deltaTime;
    }
}
