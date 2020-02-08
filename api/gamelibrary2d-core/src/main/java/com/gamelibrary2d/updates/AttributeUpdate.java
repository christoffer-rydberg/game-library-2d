package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

/**
 * The {@link AttributeUpdate} is an {@link Update} applied to the attributes of
 * an {@link GameObject}.
 */
public interface AttributeUpdate extends Update {

    /**
     * <p>
     * Makes the update relative to the specified object. This is done by
     * subtracting the attributes of the update's target object, and adding the
     * attributes of the specified object. Note that changes to the target or the
     * object after this method has been invoked will not be taken into
     * consideration.
     * </p>
     * <p>
     * This method always operates on the original value of the update. Invoking
     * this method more than once will always yield the same result, assuming the
     * update's target object and the specified object has not changed.
     * </p>
     *
     * @param obj The object, which attributes the update will be set relative to.
     */
    void makeRelative(GameObject obj);

    /**
     * <p>
     * Makes the update absolute. This is done by subtracting the attributes of the
     * update's target object. Note that changes to the target after this method has
     * been invoked will not be taken into consideration.
     * </p>
     * <p>
     * This method always operates on the original value of the update. Invoking
     * this method more than once will always yield the same result, assuming the
     * update's target object has not changed.
     */
    void makeAbsolute();
}