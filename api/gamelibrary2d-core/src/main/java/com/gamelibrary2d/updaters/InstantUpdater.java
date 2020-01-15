package com.gamelibrary2d.updaters;

import com.gamelibrary2d.updates.Update;

public class InstantUpdater implements Updater {

    private final Update update;

    private boolean finished = false;

    public InstantUpdater(Update update) {
        this.update = update;
    }

    @Override
    public void reset() {
        finished = false;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public float update(float deltaTime) {

        if (isFinished())
            return deltaTime;

        update.apply(deltaTime, 1f);

        finished = true;

        return 0;
    }
}