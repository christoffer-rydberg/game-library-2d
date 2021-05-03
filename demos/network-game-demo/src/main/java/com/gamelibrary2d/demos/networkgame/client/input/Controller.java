package com.gamelibrary2d.demos.networkgame.client.input;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.markers.Updatable;

public interface Controller extends Updatable {
    float getValue(ControllerInputId id);

    void setValue(ControllerInputId id, float value);

    void addBinding(ControllerInputId id, Action onActivation, Action onDeactivation);
}
