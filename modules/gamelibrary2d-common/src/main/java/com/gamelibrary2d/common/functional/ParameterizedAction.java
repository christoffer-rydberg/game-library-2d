package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method with a single parameter and no return value.
 *
 * @author Christoffer Rydberg
 */
public interface ParameterizedAction<T> {

    void invoke(T t);

}