package com.gamelibrary2d.updaters;

/**
 * Float value setter used by the {@link ValueUpdater}.
 *
 * @author Christoffer Rydberg
 */
public interface ValueSetter {

    /**
     * Setter for the float value that the {@link ValueUpdater} is updating.
     */
    void set(float value);

}
