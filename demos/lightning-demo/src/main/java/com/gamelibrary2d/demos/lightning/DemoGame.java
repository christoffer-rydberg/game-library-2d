package com.gamelibrary2d.demos.lightning;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.MouseCursorMode;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    protected void onStart() throws InitializationException {
        getWindow().setMouseCursorMode(MouseCursorMode.HIDDEN);
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {

    }
}
