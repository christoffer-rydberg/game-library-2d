package com.gamelibrary2d.updates;

import com.gamelibrary2d.updaters.Updater;

/**
 * <p>
 * Representation of an update used by an {@link Updater}. The update can either
 * be instant or applied over a period of time. The {@link Updater} decides the
 * duration for the Update, i.e. how many times {@link #apply} is invoked.
 *
 * <p>
 * If the {@link Update} is time-dependent and has a goal, it should be
 * configured to reach the goal after 1 second. This is so that the
 * {@link Updater} can scale the duration of the {@link Update} when invoking
 * {@link #apply}.
 * </p>
 */
public interface Update {

    /**
     * Applies the update.
     *
     * @param deltaTime       Time since the last update cycle.
     * @param scaledDeltaTime Time since the last update cycle divided by the
     *                        duration of the update. The {@link Updater} decides
     *                        the duration and will adjust this parameter
     *                        accordingly, within the interval [0, 1].
     */
    void apply(float deltaTime, float scaledDeltaTime);

}
