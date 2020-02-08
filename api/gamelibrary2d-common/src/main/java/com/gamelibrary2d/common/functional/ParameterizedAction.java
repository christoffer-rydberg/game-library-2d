package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method with a single parameter and no return value.
 */
public interface ParameterizedAction<T> {

    void invoke(T t);

}