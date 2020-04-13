package com.gamelibrary2d.demos.particlesystem;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) throws InitializationException {
        super.start(window);
    }

    @Override
    protected void onStart() throws InitializationException {
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
    }
}