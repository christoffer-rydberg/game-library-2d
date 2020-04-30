package com.gamelibrary2d.input;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.framework.Joystick;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Mouse;

/**
 * Binds to an input from a mouse, keyboard or joystick.
 * The input can be a button or an axis (if the source is a joystick).
 * This class contains methods to determine if the button/axis is pressed/tilted
 * and to update and retrieve the {@link InputState state}.
 */
public class InputBinding implements Serializable {

    public static final int KEYBOARD_SOURCE_ID = Integer.MAX_VALUE;
    public static final int MOUSE_SOURCE_ID = Integer.MAX_VALUE - 1;

    private final int inputId;
    private final int sourceId;
    private final InputType inputType;

    private Action onActive;
    private Action onActiveUnchanged;
    private Action onRelease;
    private Action onReleaseUnchanged;
    private InputState state = InputState.RELEASED;

    private InputBinding(int sourceId, int inputId, InputType inputType) {
        this.sourceId = sourceId;
        this.inputId = inputId;
        this.inputType = inputType;
    }

    public InputBinding(DataBuffer buffer) {
        this(buffer.getInt(), buffer.getInt(), InputType.values()[buffer.getInt()]);
    }

    public static InputBinding keyboard(int inputId) {
        return new InputBinding(KEYBOARD_SOURCE_ID, inputId, InputType.BUTTON);
    }

    public static InputBinding mouse(int inputId) {
        return new InputBinding(MOUSE_SOURCE_ID, inputId, InputType.BUTTON);
    }

    public static InputBinding joystick(int sourceId, int inputId, InputType inputType) {
        return new InputBinding(sourceId, inputId, inputType);
    }

    public void onActive(Action onActive) {
        this.onActive = onActive;
    }

    public void onActiveUnchanged(Action onActiveUnchanged) {
        this.onActiveUnchanged = onActiveUnchanged;
    }

    public void onReleased(Action onRelease) {
        this.onRelease = onRelease;
    }

    public void onReleaseUnchanged(Action onReleaseUnchanged) {
        this.onReleaseUnchanged = onReleaseUnchanged;
    }

    @Override
    public void serialize(DataBuffer buffer) {
        buffer.putInt(sourceId);
        buffer.putInt(inputId);
        buffer.putInt(inputType.ordinal());
    }

    /**
     * @return True if the input binding belongs to a joystick axis, false if it
     * belongs to a button.
     */
    public boolean isAxis() {
        return !isButton();
    }

    /**
     * @return True if the input binding belongs to a button, false if it belongs to
     * a joystick axis.
     */
    public boolean isButton() {
        return inputType == InputType.BUTTON;
    }

    /**
     * Gets the input type.
     *
     * @return The input type, used to determine if the input is a button or an
     * axis. If it is an axis, it can either be positive or negative, which
     * indicates which way the axis is turned to trigger this input.
     */
    public InputType getInputType() {
        return inputType;
    }

    /**
     * Gets the value of the bound joystick axis.
     *
     * @return The value of the joystick axis, or 0 if the binding doesn't belong to
     * a joystick axis.
     */
    public float getAxisValue() {
        if (inputId < 0 || !isAxis())
            return 0;
        float value = Joystick.instance().getAxisValue(sourceId, inputId);

        if (inputType == InputType.POSITIVE_AXIS)
            return value > 0 ? value : 0;

        return value < 0 ? -value : 0;
    }

    /**
     * Checks if the bound joystick axis is tilted.
     *
     * @param threshold The threshold (0, 1] to qualify as a tilt.
     * @return True if the axis is tilted. False if the binding does not belong to
     * an axis, or if the tilt is less than the threshold.
     */
    public boolean isAxisTilted(float threshold) {
        if (threshold <= 0) {
            throw new IllegalStateException("Threshold must be a positive value greater than 0.");
        }

        return getAxisValue() >= threshold;
    }

    /**
     * Checks if the bound button is pushed.
     *
     * @return True if the button is pushed, false if the binding does not belong to
     * a button, or if the button is not pushed.
     */
    public boolean isPushed() {
        if (inputId < 0 || isAxis())
            return false;

        switch (sourceId) {
            case KEYBOARD_SOURCE_ID:
                return Keyboard.instance().isKeyDown(inputId);

            case MOUSE_SOURCE_ID:
                return Mouse.instance().isButtonDown(inputId);

            default:
                return Joystick.instance().isButtonPressed(sourceId, inputId);
        }
    }

    /**
     * Updates the current {@link InputState state} and triggers the corresponding action:
     * {@link #onActive(Action) onActive}, {@link #onActiveUnchanged(Action) onActiveUnchanged},
     * {@link #onReleased(Action) onRelease} or {@link #onReleaseUnchanged(Action) onReleaseUnchanged}.
     *
     * @param tiltThreshold    Threshold for a tilt if the input is an axis. Value range: (0, 1].
     * @param releaseThreshold Threshold for releasing a tilt if the input is an axis. Value
     *                         range: (0, 1].
     * @return The updated state.
     */
    public void updateState(float tiltThreshold, float releaseThreshold) {
        if (isActiveState()) {
            if (isActive(tiltThreshold, releaseThreshold)) {
                state = InputState.ACTIVE_UNCHANGED;
                tryInvoke(onActiveUnchanged);
            } else {
                state = InputState.RELEASED;
                tryInvoke(onRelease);
            }
        } else {
            if (isActive(tiltThreshold, releaseThreshold)) {
                state = InputState.ACTIVE;
                tryInvoke(onActive);
            } else {
                state = InputState.RELEASED_UNCHANGED;
                tryInvoke(onReleaseUnchanged);
            }
        }
    }

    public InputState getState() {
        return state;
    }

    /**
     * Sets the current {@link #getState state}. This will affect the result of {@link #updateState} next time it is invoked.
     */
    public void setState(InputState state) {
        this.state = state;
    }

    private boolean isActive(float tiltThreshold, float releaseThreshold) {
        if (isAxis()) {
            return isActiveState() ? isAxisTilted(releaseThreshold) : isAxisTilted(tiltThreshold);
        } else {
            return isPushed();
        }
    }

    private boolean isActiveState() {
        return state == InputState.ACTIVE || state == InputState.ACTIVE_UNCHANGED;
    }

    private void tryInvoke(Action action) {
        if (action != null) {
            action.invoke();
        }
    }

    /**
     * Checks if the source is a keyboard.
     *
     * @return True if the source is a keyboard, false otherwise.
     */
    public boolean isKeyboard() {
        return sourceId == KEYBOARD_SOURCE_ID;
    }

    /**
     * Checks if the source is a mouse.
     *
     * @return True if the source is a mouse, false otherwise.
     */
    public boolean isMouse() {
        return sourceId == MOUSE_SOURCE_ID;
    }

    /**
     * Checks if the source is a joystick.
     *
     * @return True if the source is a joystick, false otherwise.
     */
    public boolean isJoystick() {
        return !isKeyboard() && !isMouse();
    }

    /**
     * Gets the source identifier.
     *
     * @return The source identifier. This can be a keyboard, mouse or joystick. The
     * id used for the keyboard is determined by the static field
     * {@link #KEYBOARD_SOURCE_ID} and the id for the mouse by
     * {@link #MOUSE_SOURCE_ID}. Any other value means it is a joystick.
     */
    public int getSourceId() {
        return sourceId;
    }

    /**
     * Gets the input identifier, used to identify the button or axis of the source.
     * The source can be a mouse, keyboard or joystick. The input can only be an
     * axis if the input source is a joystick. To get the source id, use the
     * {@link #getSourceId() getSourceId} method. To determine if it is an axis or
     * button, use the {@link #isAxis() isAxis} or {@link #isButton() isButton}
     * methods.
     *
     * @return The input identifier.
     */
    public int getInputId() {
        return inputId;
    }
}
