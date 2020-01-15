package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.Timer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Lwjgl_Timer extends com.gamelibrary2d.common.updating.Timer implements Timer {

    @Override
    public double getTime() {
        return glfwGetTime();
    }

}