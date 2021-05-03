package com.gamelibrary2d.demos.networkgame.client.input;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.input.AbstractInputBinding;
import com.gamelibrary2d.input.InputState;

class ControllerInputBinding extends AbstractInputBinding {
    private final ControllerInputId id;
    private final Controller controller;
    private final Action onActivation;
    private final Action onDeactivation;

    ControllerInputBinding(Controller controller, ControllerInputId id, Action onActivation, Action onDeactivation) {
        super(0.5f, 0.5f);
        this.id = id;
        this.controller = controller;
        this.onActivation = onActivation;
        this.onDeactivation = onDeactivation;
    }

    @Override
    protected float getValueFromSource() {
        return controller.getValue(id);
    }

    @Override
    protected void onStateUnchanged(InputState state, float deltaTime) {

    }

    @Override
    protected void onStateChanged(InputState newState, float deltaTime) {
        switch (newState) {
            case ACTIVE:
                onActivation.perform();
                break;
            case INACTIVE:
                onDeactivation.perform();
                break;
        }
    }
}
