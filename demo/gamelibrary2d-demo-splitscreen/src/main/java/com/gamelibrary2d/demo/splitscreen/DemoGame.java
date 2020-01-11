package com.gamelibrary2d.demo.splitscreen;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    protected void onStart() {
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {

    }
}
