package com.gamelibrary2d.updates;

/**
 * The purpose of this update is to do nothing at all. This can be useful when
 * setting up sequential updates. Placing this update in a {@link com.gamelibrary2d.updaters.DurationUpdater},
 * with a specified duration will act as a delay before the next update starts.
 *
 * @author Christoffer Rydberg
 */
public class EmptyUpdate implements Update {

    @Override
    public void apply(float deltaTime, float scaledDeltaTime) {

    }
}