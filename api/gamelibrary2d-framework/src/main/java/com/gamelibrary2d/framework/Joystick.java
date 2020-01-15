package com.gamelibrary2d.framework;

import java.util.List;

public interface Joystick {

    /**
     * Gets the runtime specific {@link Joystick} instance.
     */
    public static Joystick instance() {
        return Runtime.getFramework().getJoystick();
    }

    /**
     * Gets a read-only list of identifiers for all connected joysticks.
     */
    List<Integer> getConnectedJoysticks();

    /**
     * Gets the name of the joystick with the specified identifier.
     *
     * @param id The joystick identifier.
     */
    String getName(int id);

    /**
     * Checks if the specified button is pressed.
     *
     * @param id     The joystick identifier.
     * @param button The button identifier.
     */
    boolean isButtonPressed(int id, int button);

    /**
     * Gets the value of the specified axis.
     *
     * @param id   The joystick identifier.
     * @param axis The axis identifier.
     * @return The axis value [-1.0, 1.0].
     */
    float getAxisValue(int id, int axis);

    /**
     * Gets the the axis identifier of the first found tilted axis. If more than one
     * axis are tilted, the lowest identifier will be returned.
     *
     * @param id        The joystick identifier.
     * @param threshold The tilt threshold [0, 1.0].
     * @param offset    Specifies an offset in axis identifiers. Any identifiers less than
     *                  or equal to this offset will be skipped.
     * @return The identifier of the tilted axis, or -1 if no axis is tilted.
     */
    int getTiltedAxis(int id, float threshold, int offset);

    /**
     * Gets the button identifier of the first found pressed button. If more than
     * one button are pressed, the lowest identifier will be returned.
     *
     * @param id     The joystick identifier.
     * @param offset Specifies an offset in button identifiers. Any identifiers less
     *               than or equal to this offset will be skipped.
     * @return The identifier of the pressed button, or -1 if no button is pressed.
     */
    int getPressedButton(int id, int offset);
}