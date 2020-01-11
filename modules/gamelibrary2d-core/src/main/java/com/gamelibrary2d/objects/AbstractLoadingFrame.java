package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.LoadInterruptedException;

public abstract class AbstractLoadingFrame extends AbstractFrame implements LoadingFrame {

    private final Game game;

    private Thread workerThread;
    private Frame fallbackFrame;
    private Frame previousFrame;
    private Frame nextFrame;

    private volatile Frame frameAfterLoading;

    private boolean loadingComplete;

    public AbstractLoadingFrame(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (loadingComplete) return;
        if (workerThread != null && !workerThread.isAlive()) {
            loadingComplete = true;
            if (frameAfterLoading == nextFrame) {
                onLoadingComplete();
            } else {
                showLoadedFrame();
            }
        }
    }

    protected void onLoadingComplete() {
        showLoadedFrame();
    }

    /**
     * Shows the next frame. This should only be called once loading has finished.
     */
    protected void showLoadedFrame() {
        game.setFrame(frameAfterLoading, FrameDisposal.NONE);
    }

    @Override
    public Frame getFallBackFrame() {
        return fallbackFrame;
    }

    @Override
    public void setFallBackFrame(Frame fallBackFrame) {
        fallbackFrame = fallBackFrame;
    }

    @Override
    public Frame getPreviousFrame() {
        return previousFrame;
    }

    @Override
    public void setPreviousFrame(Frame previousFrame) {
        this.previousFrame = previousFrame;
    }

    @Override
    public Frame getNextFrame() {
        return nextFrame;
    }

    @Override
    public void setNextFrame(Frame nextFrame) {
        this.nextFrame = nextFrame;
    }

    @Override
    public void loadNextFrame() {
        loadingComplete = false;
        frameAfterLoading = null;

        Runnable worker = () -> {
            try {
                nextFrame.load();
                frameAfterLoading = nextFrame;
            } catch (LoadInterruptedException e) {
                e.printStackTrace();
                if (fallbackFrame != null) {
                    frameAfterLoading = fallbackFrame;
                } else {
                    frameAfterLoading = previousFrame;
                }
            }
        };

        workerThread = new Thread(worker);
        workerThread.start();
    }
}