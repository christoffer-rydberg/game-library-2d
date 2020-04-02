package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;

public class SplashFrame extends AbstractFrame {

    public SplashFrame(Game game) {
        super(game);
    }

    @Override
    protected void onInitialize() {

    }

    @Override
    protected void onLoad(LoadingContext context) {

    }

    @Override
    protected void onLoaded(LoadingContext context) {

    }

    @Override
    protected void onBegin() {
        getGame().setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onEnd() {
        getGame().setBackgroundColor(Color.BLACK);
    }
}
