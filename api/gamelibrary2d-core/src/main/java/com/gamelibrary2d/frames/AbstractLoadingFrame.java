package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.LoadInterruptedException;

public abstract class AbstractLoadingFrame extends AbstractFrame implements LoadingFrame {

    private final Game game;

    private Thread workerThread;
    private Frame fallbackFrame;
    private Frame previousFrame;
    private Frame nextFrame;

    private volatile Frame frameAfterLoading;

    private boolean loadFinished;

    public AbstractLoadingFrame(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (loadFinished) return;
        if (workerThread != null && !workerThread.isAlive()) {
            loadFinished = true;
            if (frameAfterLoading == nextFrame) {
                onLoadFinished();
            } else {
                showLoadedFrame();
            }
        }
    }

    protected void onLoadFinished() {
        showLoadedFrame();
    }

    /**
     * Shows the next frame. This should only be called once loading has finished.
     */
    protected void showLoadedFrame() {
        if (!loadFinished) {
            throw new GameLibrary2DRuntimeException("Loading has not finished");
        }
        frameAfterLoading.loadCompleted();
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
        loadFinished = false;
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