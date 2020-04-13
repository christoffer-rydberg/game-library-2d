package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractLoadingFrame;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.LoadingContext;

public class LoadingFrame extends AbstractLoadingFrame {

    private LoadingAction loadingAction;

    public LoadingFrame(Game game) {
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
        getGame().setBackgroundColor(Color.LIGHT_CORAL);
    }

    @Override
    protected void onEnd() {
        getGame().setBackgroundColor(Color.BLACK);
    }

    public void setLoadingAction(LoadingAction action) {
        loadingAction = action;
    }

    @Override
    protected void loadFrame(Frame frame, LoadingContext context) throws InitializationException {
        if (loadingAction != null) {
            loadingAction.invoke();
        }

        super.loadFrame(frame, context);
    }

    public interface LoadingAction {
        void invoke() throws InitializationException;
    }
}
