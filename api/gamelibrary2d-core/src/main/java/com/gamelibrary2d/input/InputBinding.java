package com.gamelibrary2d.input;

import com.gamelibrary2d.markers.Updatable;

/**
 * Defines a binding to an input, such as a key on the keyboard or a button on the joystick.
 * The input source can be virtual or physical.
 */
public interface InputBinding extends Updatable {

    /**
     * @return The input value from the previous update
     */
    float getValue();

    /**
     * @return The {@link InputState} from the previous update
     */
    InputState getState();

}
