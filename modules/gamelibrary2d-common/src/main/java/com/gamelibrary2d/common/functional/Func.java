package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method that with a a return value and a single parameter.
 *
 * @author Christoffer Rydberg
 */
public interface Func<T, TD> {
    TD invoke(T t);
}