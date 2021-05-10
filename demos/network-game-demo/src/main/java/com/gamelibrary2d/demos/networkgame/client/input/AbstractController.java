package com.gamelibrary2d.demos.networkgame.client.input;

import com.gamelibrary2d.input.InputBinding;

import java.util.ArrayList;

public abstract class AbstractController implements Controller {
    private final ArrayList<InputBinding> bindings;

    protected AbstractController() {
        bindings = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < bindings.size(); ++i) {
            InputBinding input = bindings.get(i);
            if (input != null) {
                input.update(deltaTime);
            }
        }
    }

    @Override
    public void addBinding(ControllerInputId id, ControllerInputBinding.InputChangedListener inputChangedListener) {
        bindings.add(new ControllerInputBinding(this, id, inputChangedListener));
    }
}
