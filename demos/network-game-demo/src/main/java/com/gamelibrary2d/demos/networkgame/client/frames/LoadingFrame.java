package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.frames.AbstractLoadingFrame;
import com.gamelibrary2d.frames.InitializationContext;

public class LoadingFrame extends AbstractLoadingFrame {

    public LoadingFrame(Game game, ResourceManager resourceManager) {
        super(game);
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
        getGame().setBackgroundColor(Color.LIGHT_CORAL);
    }

    @Override
    protected void onEnd() {
        getGame().setBackgroundColor(Color.BLACK);
    }

}
