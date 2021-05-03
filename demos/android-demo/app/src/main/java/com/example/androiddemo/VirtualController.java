package com.example.androiddemo;

import com.gamelibrary2d.demos.networkgame.client.input.AbstractController;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;

public class VirtualController extends AbstractController {
    private float left;
    private float right;
    private float up;

    @Override
    public float getValue(ControllerInputId controllerInputId) {
        switch (controllerInputId) {
            case LEFT:
                return left;
            case RIGHT:
                return right;
            case UP:
                return up;
            default:
                throw new IllegalStateException("Unexpected value: " + controllerInputId);
        }
    }

    @Override
    public void setValue(ControllerInputId controllerInputId, float value) {
        switch (controllerInputId) {
            case LEFT:
                left = value;
                break;
            case RIGHT:
                right = value;
                break;
            case UP:
                up = value;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + controllerInputId);
        }
    }
}
