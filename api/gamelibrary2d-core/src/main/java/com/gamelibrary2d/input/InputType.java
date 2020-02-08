package com.gamelibrary2d.input;

/**
 * Describes the input type of an input binding.
 */
public enum InputType {

    /**
     * The input is from a regular button.
     */
    BUTTON,

    /**
     * The input is from an axis with a negative tilt.
     * This input type is only supported by joysticks.
     */
    NEGATIVE_AXIS,

    /**
     * The input is from an axis with a positive tilt.
     * This input type is only supported by joysticks.
     */
    POSITIVE_AXIS

}