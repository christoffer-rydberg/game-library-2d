package com.gamelibrary2d.updaters;

import java.util.Deque;

/**
 * The SequentialUpdater is an {@link UpdaterSet} running all updates
 * sequentially, in the order they were added. The updater is finished once all
 * updates have finished.
 *
 * @author Christoffer Rydberg
 */
public class SequentialUpdater extends AbstractUpdaterSet {

    public SequentialUpdater() {
        super();
    }

    public SequentialUpdater(int size) {
        super(size);
    }

    @Override
    protected float onUpdate(Deque<Updater> active, Deque<Updater> finished, float deltaTime) {
        float remainingTime = deltaTime;
        while (!active.isEmpty() && remainingTime > 0) {
            Updater current = active.peekFirst();
            remainingTime -= current.update(remainingTime);
            if (current.isFinished()) {
                finished.addLast(active.pollFirst());
            }
        }
        return deltaTime - remainingTime;
    }
}