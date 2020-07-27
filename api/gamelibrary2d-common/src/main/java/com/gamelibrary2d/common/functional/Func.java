package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method that with a a return value and a single parameter.
 */
public interface Func<T1, T2> {
    T2 invoke(T1 t);
}