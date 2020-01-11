package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method without parameters and no return value.
 *
 * @author Christoffer Rydberg
 */
public interface Action {

    public static final Action EMPTY = () -> {
    };

    void invoke();

}