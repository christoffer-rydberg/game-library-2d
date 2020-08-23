package com.gamelibrary2d.updates;

import com.gamelibrary2d.markers.Updatable;

/**
 * The purpose of this update is to do nothing at all. This can be useful when
 * setting up sequential updates. Placing this update in a {@link com.gamelibrary2d.updaters.DurationUpdater},
 * with a specified duration will act as a delay before the next update starts.
 */
public class EmptyUpdate implements Updatable {

    @Override
    public void update(float deltaTime) {

    }
}