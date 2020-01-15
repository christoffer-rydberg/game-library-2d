package com.gamelibrary2d.input;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;

import java.util.Arrays;

public class InputBindings implements Serializable {

    private final Input[] inputs;

    public InputBindings(int maximumNumberOfBindings) {
        inputs = new Input[maximumNumberOfBindings];
    }

    public InputBindings(DataBuffer buffer) {
        inputs = new Input[buffer.getInt()];
        for (int i = 0; i < inputs.length; ++i) {
            boolean hasBinding = buffer.get() == (byte) 1;
            if (hasBinding)
                inputs[i] = new Input(buffer);
        }
    }

    @Override
    public void serialize(DataBuffer buffer) {
        buffer.putInt(inputs.length);
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] != null) {
                buffer.put((byte) 1);
                inputs[i].serialize(buffer);
            } else {
                buffer.put((byte) 0);
            }
        }
    }

    /**
     * Gets the maximum number of bindings. This is also the size of the array used
     * for the bindings, which means no binding index can be equal to or higher than
     * this value.
     *
     * @return The maximum number of bindings.
     */
    public int getMaximumNumberOfBindings() {
        return inputs.length;
    }

    /**
     * Removes all bindings.
     */
    public void clearBindings() {
        Arrays.fill(inputs, null);
    }

    /**
     * Creates an input binding.
     *
     * @param index     The index, which is a game specific value used to identify the
     *                  binding.
     * @param inputId   The input id, which identifies the input button or axis.
     * @param sourceId  The source id, which identifies the source (keyboard, mouse or
     *                  joystick).
     * @param isActive  The initial state of the input. True indicates that the input is
     *                  pushed or tilted (if axis).
     * @param inputType The input type.
     */
    public void createBinding(int index, int inputId, int sourceId, InputType inputType, boolean isActive) {
        Input input = inputs[index];
        if (input == null) {
            inputs[index] = new Input(inputId, sourceId, inputType, isActive);
        } else {
            input.set(inputId, sourceId, inputType, isActive);
        }
    }

    /**
     * Removes the binding at the specified index.
     *
     * @param index The index of the binding to remove.
     */
    public void removeBinding(int index) {
        inputs[index] = null;
    }

    /**
     * Gets the input bound to the specified index.
     *
     * @param index The index of the input.
     * @return The input.
     */
    public Input getInput(int index) {
        return inputs[index];
    }

    /**
     * Resets the state of all {@link Input inputs} by invoking
     * {@link Input#resetState(boolean)}.
     */
    public void resetInputStates(boolean active) {
        for (int i = 0; i < inputs.length; ++i) {
            Input input = inputs[i];
            if (input != null)
                input.resetState(active);
        }
    }
}