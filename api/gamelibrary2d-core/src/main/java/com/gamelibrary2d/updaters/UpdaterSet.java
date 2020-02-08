package com.gamelibrary2d.updaters;

/**
 * Represents an {@link Updater} that groups several {@link Updater updaters}
 * into one. The invocation order is determined by the implementation of this
 * interface.
 */
public interface UpdaterSet extends Updater {

    /**
     * Adds the specified updater.
     */
    void add(Updater updater);

    /**
     * Removes all added updaters.
     */
    void clear();

}
