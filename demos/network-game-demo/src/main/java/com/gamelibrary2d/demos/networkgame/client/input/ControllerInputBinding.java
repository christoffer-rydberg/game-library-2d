package com.gamelibrary2d.demos.networkgame.client.input;

import com.gamelibrary2d.input.AbstractInputBinding;
import com.gamelibrary2d.input.InputState;

class ControllerInputBinding extends AbstractInputBinding {
    private final ControllerInputId id;
    private final Controller controller;
    private final InputChangedListener inputChangedListener;

    ControllerInputBinding(Controller controller, ControllerInputId id, InputChangedListener inputChangedListener) {
        super(0f, 0f);
        this.id = id;
        this.controller = controller;
        this.inputChangedListener = inputChangedListener;
    }

    @Override
    protected float getValueFromSource() {
        return controller.getValue(id);
    }

    private void updateInputValue(float prevValue, float newValue) {
        if (prevValue != newValue) {
            inputChangedListener.onInputChanged(newValue);
        }
    }

    @Override
    protected void onStateUnchanged(InputState state, float prevValue, float newValue, float deltaTime) {
        if (state == InputState.ACTIVE) {
            updateInputValue(prevValue, newValue);
        }
    }

    @Override
    protected void onStateChanged(InputState newState, float prevValue, float newValue, float deltaTime) {
        switch (newState) {
            case ACTIVE:
                updateInputValue(prevValue, newValue);
                break;
            case INACTIVE:
                updateInputValue(prevValue, 0f);
                break;
        }
    }

    public interface InputChangedListener {
        void onInputChanged(float value);
    }
}
