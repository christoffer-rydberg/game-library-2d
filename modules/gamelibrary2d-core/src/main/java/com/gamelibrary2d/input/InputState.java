package com.gamelibrary2d.input;

/**
 * Describes the current state of the input binding.
 *
 * @author Christoffer Rydberg
 */
public enum InputState {

    /**
     * The binding was activated.
     */
    ACTIVE,

    /**
     * The binding was released.
     */
    RELEASED,

    /**
     * The binding is unchanged (still active).
     */
    ACTIVE_UNCHANGED,

    /**
     * The binding is unchanged (still released).
     */
    RELEASED_UNCHANGED
}