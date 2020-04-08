package com.gamelibrary2d.input;

import java.util.ArrayList;
import java.util.List;

public class InputController {
    private final ArrayList<InputBinding> bindings;
    private float tiltTreshold;
    private float releaseTreshold;

    public InputController() {
        bindings = new ArrayList<>();
        tiltTreshold = 0.5f;
        releaseTreshold = 0.5f;
    }

    public InputController(float tiltTreshold, float releaseTreshold) {
        bindings = new ArrayList<>();
        this.tiltTreshold = tiltTreshold;
        this.releaseTreshold = releaseTreshold;
    }

    public List<InputBinding> getBindings() {
        return bindings;
    }

    public void update() {
        for (int i = 0; i < bindings.size(); ++i) {
            var input = bindings.get(i);
            if (input != null) {
                input.updateState(tiltTreshold, releaseTreshold);
            }
        }
    }
}
