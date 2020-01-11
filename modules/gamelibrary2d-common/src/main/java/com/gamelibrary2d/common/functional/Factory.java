package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method with a return value and no parameters.
 *
 * @author Christoffer Rydberg
 */
public interface Factory<T> {

    T create();

}