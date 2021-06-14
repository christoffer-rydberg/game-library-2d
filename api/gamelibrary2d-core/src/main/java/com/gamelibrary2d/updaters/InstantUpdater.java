package com.gamelibrary2d.updaters;

import com.gamelibrary2d.components.denotations.Updatable;

public class InstantUpdater implements Updater {

    private final Updatable update;

    private boolean finished = false;

    public InstantUpdater(Updatable update) {
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
        if (isFinished()) {
            return 0f;
        }

        update.update(1f);

        finished = true;

        return 0;
    }
}