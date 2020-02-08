package com.gamelibrary2d.markers;

/**
 * Any game object containing a clear method should implement this interface in
 * order to enable automatic clearing in recursive methods. For instance, when a
 * container is cleared it can access the clear-method of any child object that
 * implements this interface. To further control if the child object is
 * automatically cleared, this interface contains the {@link #isAutoClearing} method.
 */
public interface Clearable {

    /**
     * Clears the object.
     */
    void clear();

    /**
     * Indicates if the object should automatically be cleared when the parent
     * object is cleared. It is up to the implementation of the parent to respect
     * this flag.
     */
    boolean isAutoClearing();

}
