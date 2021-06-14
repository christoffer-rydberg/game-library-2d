package com.gamelibrary2d.demos.networkgame.client.input;

import com.gamelibrary2d.components.denotations.Updatable;

public interface Controller extends Updatable {
    float getValue(ControllerInputId id);

    void setValue(ControllerInputId id, float value);

    void addBinding(ControllerInputId id, ControllerInputBinding.InputChangedListener inputChangedListener);
}
