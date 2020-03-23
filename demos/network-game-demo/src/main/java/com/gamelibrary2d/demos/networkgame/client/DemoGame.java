package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;

public class DemoGame extends AbstractGame {

    private DemoFrame networkFrame;

    public DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) {
        super.start(window);
    }

    @Override
    protected void onStart() {
        networkFrame = new DemoFrame(this);
        networkFrame.getClient().setCommunicator(new DemoCommunicator(
                new TcpConnectionSettings("localhost", 4444, true)));
        setFrame(networkFrame);
    }

    @Override
    protected void onExit() {
        networkFrame.getClient().disconnect();
    }
}