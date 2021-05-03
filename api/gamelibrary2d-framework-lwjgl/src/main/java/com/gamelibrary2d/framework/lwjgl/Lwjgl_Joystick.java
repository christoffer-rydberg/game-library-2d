package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.Joystick;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lwjgl_Joystick implements Joystick {

    private static Lwjgl_Joystick instance;

    private final List<Integer> connectedJoysticks = new ArrayList<Integer>(GLFW.GLFW_JOYSTICK_LAST + 1);

    private final List<Integer> readOnlyconnectedJoysticks = Collections.unmodifiableList(connectedJoysticks);

    private Lwjgl_Joystick() {
        instance = this;
    }

    public static Lwjgl_Joystick instance() {
        return instance != null ? instance : new Lwjgl_Joystick();
    }

    void onConnected(int id) {
        if (!connectedJoysticks.contains(id))
            connectedJoysticks.add(id);
    }

    void onDisconnected(int id) {
        connectedJoysticks.remove(id);
    }

    public void initialize() {
        for (int i = 0; i <= GLFW.GLFW_JOYSTICK_LAST; ++i) {
            if (GLFW.glfwJoystickPresent(i))
                onConnected(i);
        }
    }

    @Override
    public List<Integer> getConnectedJoysticks() {
        return readOnlyconnectedJoysticks;
    }

    @Override
    public String getName(int id) {
        return GLFW.glfwGetJoystickName(id);
    }

    @Override
    public boolean isButtonPressed(int id, int button) {
        ByteBuffer buffer = GLFW.glfwGetJoystickButtons(id);
        if (buffer == null || button >= buffer.limit()) {
            return false;
        } else {
            return buffer.get(button) == GLFW.GLFW_PRESS;
        }
    }

    @Override
    public int getTiltedAxis(int id, float threshold, int offset) {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1.");
        }

        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(id);
        if (buffer != null) {
            int remaining = buffer.remaining() - offset;
            for (int i = offset; i < remaining; ++i)
                if (Math.abs(buffer.get(i)) > threshold)
                    return i;
        }

        return -1;
    }

    public float getAxisValue(int id, int axis) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(id);
        if (buffer != null) {
            return axis < buffer.limit() ? buffer.get(axis) : 0;
        }

        return 0;
    }

    @Override
    public int getPressedButton(int id, int offset) {
        ByteBuffer buffer = GLFW.glfwGetJoystickButtons(id);
        if (buffer != null) {
            int remaining = buffer.remaining() - offset;
            for (int i = offset; i < remaining; ++i)
                if (buffer.get(i) == GLFW.GLFW_PRESS)
                    return i;
        }
        return -1;
    }
}