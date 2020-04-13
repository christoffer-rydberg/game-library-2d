package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.InitializationException;

public abstract class AbstractLoadingFrame extends AbstractFrame implements LoadingFrame {
    private final Game game;
    private Thread workerThread;
    private volatile LoadResult loadResult;

    public AbstractLoadingFrame(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (workerThread != null && !workerThread.isAlive()) {
            var loadResult = this.loadResult;
            workerThread = null;
            this.loadResult = null;
            if (loadResult.error == null) {
                try {
                    onLoadingSuccessful(loadResult);
                } catch (InitializationException e) {
                    onLoadingFailed(new LoadResult(
                            loadResult.frame,
                            loadResult.context,
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
            throw new GameLibrary2DRuntimeException("Loading is in progress");
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
            var exception = new GameLibrary2DRuntimeException(
                    "Failed to restore previous frame after failed load",
                    e);

            exception.addSuppressed(result.error);

            throw exception;
        }
    }

    protected void loadFrame(Frame frame, LoadingContext context) throws InitializationException {
        frame.load(context);
    }

    @Override
    public void load(Frame frame, Frame previousFrame, FrameDisposal previousFrameDisposal) throws InitializationException {
        verifyNotLoading();
        if (frame.isLoaded()) {
            changeFrame(frame, previousFrame, previousFrameDisposal);
        } else {
            workerThread = new Thread(() -> {
                var context = new DefaultLoadingContext();
                try {
                    loadFrame(frame, context);
                    loadResult = new LoadResult(frame, context, previousFrame, previousFrameDisposal, null);
                } catch (InitializationException e) {
                    frame.dispose(FrameDisposal.UNLOAD);
                    loadResult = new LoadResult(frame, context, previousFrame, previousFrameDisposal, e);
                }
            });
            workerThread.start();
        }
    }

    protected static class LoadResult {
        public final Frame frame;
        public final Frame previousFrame;
        public final LoadingContext context;
        public final FrameDisposal previousFrameDisposal;
        public final InitializationException error;

        private LoadResult(Frame frame, LoadingContext context, Frame previousFrame,
                           FrameDisposal previousFrameDisposal, InitializationException error) {
            this.frame = frame;
            this.previousFrame = previousFrame;
            this.context = context;
            this.previousFrameDisposal = previousFrameDisposal;
            this.error = error;
        }
    }
}