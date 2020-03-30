package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.frames.AbstractFrame;

public class SplashFrame extends AbstractFrame {

    public SplashFrame(Game game) {
        super(game);
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        initializer.onBegin(() -> getGame().setBackgroundColor(Color.WHITE));
        initializer.onEnd(() -> getGame().setBackgroundColor(Color.BLACK));
    }
}
