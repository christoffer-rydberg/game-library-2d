package com.gamelibrary2d.demos.transparentwindow;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

import static org.lwjgl.glfw.GLFW.*;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) throws InitializationException {
        GlfwWindow glfwWindow = (GlfwWindow) window;
        glfwWindow.additionalWindowHint(GLFW_DECORATED, GLFW_FALSE);
        glfwWindow.additionalWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
        super.start(glfwWindow);
    }

    @Override
    protected void onStart() throws InitializationException {
        setBackgroundColor(Color.TRANSPARENT);
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
    }
}