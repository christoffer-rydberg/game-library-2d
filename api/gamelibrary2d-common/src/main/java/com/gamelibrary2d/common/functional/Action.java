package com.gamelibrary2d.common.functional;

/**
 * Encapsulates a method without parameters and no return value.
 */
public interface Action {

    Action EMPTY = () -> {
    };

    void invoke();

}