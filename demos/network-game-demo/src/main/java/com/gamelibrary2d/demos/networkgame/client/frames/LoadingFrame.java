package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.frames.AbstractLoadingFrame;
import com.gamelibrary2d.frames.Frame;

public class LoadingFrame extends AbstractLoadingFrame {

    private LoadingAction loadingAction;

    public LoadingFrame(Game game) {
        super(game);
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        initializer.onBegin(() -> getGame().setBackgroundColor(Color.LIGHT_CORAL));
        initializer.onEnd(() -> getGame().setBackgroundColor(Color.BLACK));
    }

    public void setLoadingAction(LoadingAction action) {
        loadingAction = action;
    }

    @Override
    protected void loadFrame(Frame frame) throws LoadFailedException {
        if (loadingAction != null) {
            loadingAction.invoke();
        }
        
        super.loadFrame(frame);
    }

    public interface LoadingAction {
        void invoke() throws LoadFailedException;
    }
}
