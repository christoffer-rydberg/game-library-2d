package com.gamelibrary2d.demos.lightning;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.lwjgl.MouseCursorMode;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    private GlfwWindow window;

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) throws InitializationException {
        this.window = (GlfwWindow) window;
        super.start(window);
    }

    @Override
    protected void onStart() throws InitializationException {
        window.setMouseCursorMode(MouseCursorMode.HIDDEN);
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {

    }
}
