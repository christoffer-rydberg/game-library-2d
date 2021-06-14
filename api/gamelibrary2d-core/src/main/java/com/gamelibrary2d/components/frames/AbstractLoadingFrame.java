package com.gamelibrary2d.components.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.InitializationException;

public abstract class AbstractLoadingFrame extends AbstractFrame implements LoadingFrame {
    private final Game game;
    private Thread workerThread;
    private volatile LoadResult loadResult;

    public AbstractLoadingFrame(Game game) {
        this.game = game;
    }

    protected Game getGame() {
        return game;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (workerThread != null && !workerThread.isAlive()) {
            LoadResult loadResult = this.loadResult;
            workerThread = null;
            this.loadResult = null;
            if (loadResult.error == null) {
                try {
                    onLoadingSuccessful(loadResult);
                } catch (InitializationException e) {
                    onLoadingFailed(new LoadResult(
                            loadResult.frame,
                            null,
                            loadResult.previousFrame,
                            loadResult.previousFrameDisposal,
                            e
                    ));
                }
            } else {
                onLoadingFailed(loadResult);
            }
        }
    }

    private void changeFrame(Frame frame, Frame previousFrame, FrameDisposal previousFrameDisposal) throws InitializationException {
        game.setFrame(frame, FrameDisposal.NONE);
        previousFrame.dispose(previousFrameDisposal);
    }

    private void verifyNotLoading() {
        if (workerThread != null) {
            throw new IllegalStateException("Loading is in progress");
        }
    }

    protected void onLoadingSuccessful(LoadResult result) throws InitializationException {
        verifyNotLoading();
        result.frame.loaded(result.context);
        changeFrame(result.frame, result.previousFrame, result.previousFrameDisposal);
    }

    protected void onLoadingFailed(LoadResult result) {
        verifyNotLoading();
        try {
            game.setFrame(result.previousFrame, FrameDisposal.NONE);
        } catch (InitializationException e) {
            IllegalStateException exception = new IllegalStateException(
                    "Failed to restore previous frame after failed load",
                    e);

            exception.addSuppressed(result.error);

            throw exception;
        }
    }

    protected InitializationContext loadFrame(Frame frame) throws InitializationException {
        return frame.load();
    }

    @Override
    public void load(Frame frame, Frame previousFrame, FrameDisposal previousFrameDisposal) throws InitializationException {
        verifyNotLoading();
        if (frame.isLoaded()) {
            changeFrame(frame, previousFrame, previousFrameDisposal);
        } else {
            workerThread = new Thread(() -> {
                try {
                    InitializationContext context = loadFrame(frame);
                    loadResult = new LoadResult(frame, context, previousFrame, previousFrameDisposal, null);
                } catch (InitializationException e) {
                    frame.dispose(FrameDisposal.UNLOAD);
                    loadResult = new LoadResult(frame, null, previousFrame, previousFrameDisposal, e);
                }
            });
            workerThread.start();
        }
    }

    protected static class LoadResult {
        public final Frame frame;
        public final Frame previousFrame;
        public final FrameDisposal previousFrameDisposal;
        public final InitializationException error;
        public final InitializationContext context;

        private LoadResult(Frame frame, InitializationContext context, Frame previousFrame,
                           FrameDisposal previousFrameDisposal, InitializationException error) {
            this.frame = frame;
            this.previousFrame = previousFrame;
            this.context = context;
            this.previousFrameDisposal = previousFrameDisposal;
            this.error = error;
        }
    }
}