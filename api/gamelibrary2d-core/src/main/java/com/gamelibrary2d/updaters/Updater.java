package com.gamelibrary2d.updaters;

import com.gamelibrary2d.layers.Frame;
import com.gamelibrary2d.updates.Update;

/**
 * The {@link Updater} is used to apply one or more {@link Update updates}. You
 * can run an updater by invoking {@link Frame#run}.
 *
 * @author Christoffer Rydberg
 */
public interface Updater {

    /**
     * Resets the updater so that it can be reused.
     */
    void reset();

    /**
     * Checks if the updater is finished.
     */
    boolean isFinished();

    /**
     * Invoked on each update cycle to perform one or more updates.
     *
     * @param deltaTime The time since the last update cycle, in seconds.
     * @return The time consumed by the updater, in seconds.
     */
    float update(float deltaTime);
}