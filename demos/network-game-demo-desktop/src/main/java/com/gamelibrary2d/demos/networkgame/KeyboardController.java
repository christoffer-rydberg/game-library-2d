package com.gamelibrary2d.demos.networkgame;

import com.gamelibrary2d.demos.networkgame.client.input.AbstractController;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;
import com.gamelibrary2d.framework.Keyboard;

public class KeyboardController extends AbstractController {

    private static int convertToKeyboardInputId(ControllerInputId id) {
        switch (id) {
            case LEFT:
                return Keyboard.instance().keyLeft();
            case RIGHT:
                return Keyboard.instance().keyRight();
            case UP:
                return Keyboard.instance().keyUp();
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
    }

    @Override
    public float getValue(ControllerInputId id) {
        int keyboardInputId = convertToKeyboardInputId(id);
        return Keyboard.instance().isKeyDown(keyboardInputId) ? 1f : 0f;
    }

    @Override
    public void setValue(ControllerInputId id, float value) {
        throw new RuntimeException("Controller is readonly");
    }
}
