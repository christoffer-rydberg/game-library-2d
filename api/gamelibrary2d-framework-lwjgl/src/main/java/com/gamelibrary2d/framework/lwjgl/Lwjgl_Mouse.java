package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.Mouse;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;

public class Lwjgl_Mouse implements Mouse {

    private static Lwjgl_Mouse instance;

    private Lwjgl_Mouse() {
        instance = this;
    }

    public static Lwjgl_Mouse instance() {
        return instance != null ? instance : new Lwjgl_Mouse();
    }

    @Override
    public int actionPressed() {
        return GLFW.GLFW_PRESS;
    }

    @Override
    public int actionReleased() {
        return GLFW.GLFW_RELEASE;
    }

    @Override
    public int mouseButton1() {
        return GLFW.GLFW_MOUSE_BUTTON_1;
    }

    @Override
    public int mouseButton2() {
        return GLFW.GLFW_MOUSE_BUTTON_2;
    }

    @Override
    public boolean isButtonDown(int button) {
        return GLFW.glfwGetMouseButton(glfwGetCurrentContext(), button) == actionPressed();
    }
}