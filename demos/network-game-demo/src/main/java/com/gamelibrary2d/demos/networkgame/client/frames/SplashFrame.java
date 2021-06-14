package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.components.frames.AbstractFrame;
import com.gamelibrary2d.components.frames.InitializationContext;

public class SplashFrame extends AbstractFrame {

    private final Game game;

    public SplashFrame(Game game) {
        this.game = game;
    }

    @Override
    protected void onInitialize(InitializationContext context) {

    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {
        game.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onEnd() {
        game.setBackgroundColor(Color.BLACK);
    }
}
