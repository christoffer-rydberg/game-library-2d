package com.gamelibrary2d.updaters;

/**
 * Float value getter used by the {@link ValueUpdater}.
 *
 * @author Christoffer Rydberg
 */
public interface ValueGetter {

    /**
     * Getter for the float value that the {@link ValueUpdater} is updating.
     */
    float get();

}
