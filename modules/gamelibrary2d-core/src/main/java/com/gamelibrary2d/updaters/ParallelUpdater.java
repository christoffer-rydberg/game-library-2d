package com.gamelibrary2d.updaters;

import java.util.Deque;

/**
 * The ParallelUpdater is an {@link UpdaterSet} running all updates in parallel.
 * The updater is finished once all updates have finished.
 *
 * @author Christoffer Rydberg
 */
public class ParallelUpdater extends AbstractUpdaterSet {

    public ParallelUpdater() {
        super();
    }

    public ParallelUpdater(int size) {
        super(size);
    }

    @Override
    protected float onUpdate(Deque<Updater> active, Deque<Updater> finished, float deltaTime) {
        float usedTime = 0;
        int count = active.size();
        for (int i = 0; i < count; ++i) {
            Updater updater = active.pollFirst();
            usedTime = Math.max(updater.update(deltaTime), usedTime);
            if (updater.isFinished())
                finished.addLast(updater);
            else
                active.addLast(updater);
        }
        return usedTime;
    }
}