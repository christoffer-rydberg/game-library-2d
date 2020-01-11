package com.gamelibrary2d.demo.lightning;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.framework.MouseCursorMode;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    protected void onStart() {
        getWindow().setMouseCursorMode(MouseCursorMode.HIDDEN);
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {

    }
}
