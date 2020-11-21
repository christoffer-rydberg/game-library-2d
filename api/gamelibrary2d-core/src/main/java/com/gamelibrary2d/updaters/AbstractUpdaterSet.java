package com.gamelibrary2d.updaters;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Abstract implementation of an {@link UpdaterSet} holding a set of active and
 * finished updates. Derived classes decides when the updates are applied and
 * when they are finished.
 */
abstract class AbstractUpdaterSet implements UpdaterSet {

    private Deque<Updater> active;

    private Deque<Updater> finished;

    protected AbstractUpdaterSet() {
        active = new ArrayDeque<>();
        finished = new ArrayDeque<>();
    }

    protected AbstractUpdaterSet(int size) {
        active = new ArrayDeque<>(size);
        finished = new ArrayDeque<>(size);
    }

    @Override
    public boolean isFinished() {
        return active.isEmpty();
    }

    @Override
    public void reset() {
        while (!active.isEmpty()) {
            finished.addLast(active.pollFirst());
        }

        finished.forEach(Updater::reset);

        Deque<Updater> tmp = finished;
        finished = active;
        active = tmp;
    }

    @Override
    public float update(float deltaTime) {
        return onUpdate(active, finished, deltaTime);
    }

    @Override
    public void add(Updater updater) {
        active.addLast(updater);
    }

    @Override
    public void clear() {
        active.clear();
        finished.clear();
    }

    protected abstract float onUpdate(Deque<Updater> active, Deque<Updater> finished, float deltaTime);
}
