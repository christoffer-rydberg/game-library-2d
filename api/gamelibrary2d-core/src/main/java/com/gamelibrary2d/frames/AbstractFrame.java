package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.exceptions.LoadInterruptedException;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.AbstractLayer;
import com.gamelibrary2d.updaters.Updater;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractFrame extends AbstractLayer<Renderable> implements Frame {
    private Game game;

    private DisposerStack disposer;
    private Deque<Updater> updaters;

    private boolean paused;
    private boolean disposed;
    private boolean initialized;
    private boolean loaded;

    private LoadAction onLoad;
    private Action onLoaded;
    private Action onBegin;
    private Action onEnd;

    protected AbstractFrame(Game game) {
        this.game = game;
    }

    @Override
    public void registerDisposal(Disposable disposable) {
        disposer.push(disposable);
    }

    @Override
    public void initialize() {
        if (isDisposed()) {
            throw new GameLibrary2DRuntimeException("This object has been disposed.");
        }

        if (isInitialized())
            return;

        game.registerDisposal(this);
        disposer = new DisposerStack();
        updaters = new ArrayDeque<>();

        onInitialize();

        disposer.pushBreak();
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void load() throws LoadInterruptedException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            throw new LoadInterruptedException("Frame has not been initialized");
        }

        if (onLoad != null) {
            try {
                onLoad.invoke();
            } catch (Exception e) {
                e.printStackTrace();
                reset();
                throw e instanceof LoadInterruptedException ? (LoadInterruptedException) e
                        : new LoadInterruptedException(e.getMessage());
            }
        }

        loaded = true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void loadCompleted() {
        if (!isLoaded()) {
            throw new GameLibrary2DRuntimeException("Frame has not been loaded");
        }

        if (onLoaded != null) {
            onLoaded.invoke();
        }
    }

    @Override
    public void reset() {
        // Dispose all resources created after the initialization phase.
        disposer.disposeUntilBreak();
        commonCleanUp();
    }

    @Override
    public void dispose() {
        if (isDisposed())
            return;

        disposer.dispose();
        commonCleanUp();
        initialized = false;
        disposed = true;
        game = null;
        disposer = null;
        updaters = null;
    }

    public boolean isDisposed() {
        return disposed;
    }

    private void commonCleanUp() {
        clear();
        updaters.clear();
        loaded = false;
    }

    protected void runUpdater(Updater updater) {
        runUpdater(updater, true);
    }

    @Override
    public void runUpdater(Updater updater, boolean reset) {
        if (!updaters.contains(updater))
            updaters.addLast(updater);
        if (reset) {
            updater.reset();
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (!isPaused()) {
            super.onUpdate(deltaTime);
            for (int i = 0; i < updaters.size(); ++i) {
                Updater updater = updaters.pollFirst();
                updater.update(deltaTime);
                if (!updater.isFinished()) {
                    updaters.addLast(updater);
                }
            }
        }
    }

    @Override
    public Game game() {
        return game;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    /**
     * Called prior to {@link #load} in order to perform non-thread-safe
     * initialization. When {@link #reset resetting} this frame, initialization done inside
     * this method is be kept intact so that the frame can be efficiently reused.
     */
    protected abstract void onInitialize();

    /**
     * The specified action is invoked after {@link #initialize} but prior to {@link #loaded}. All
     * initialization code (needed to reset the frame) should be placed here or in
     * the {@link #onLoaded} method. If a {@link LoadingFrame} is used, this method
     * will not be invoked from the main thread. This allows the loading frame to be
     * updated and rendered while this frame is loaded in the background.
     * <p>
     * <b>Note:</b> The thread invoking this method from the loading frame has no
     * OpenGL-context. Any OpenGL-related functionality, such as loading textures,
     * must be done in {@link #initialize} or {@link #loaded}.
     * </p>
     */
    protected final void onLoad(LoadAction onLoad) {
        this.onLoad = onLoad;
    }

    /**
     * The specified action is invoked after {@link #load} in order to perform initialization that isn't
     * thread safe. Only code that needs to run after the frame has loaded should be
     * placed here. In other case, consider placing it in {@link #onInitialize}.
     */
    protected final void onLoaded(Action onLoaded) {
        this.onLoaded = onLoaded;
    }

    public void begin() {
        if (onBegin != null) {
            onBegin.invoke();
        }
    }

    public void end() {
        if (onEnd != null) {
            onEnd.invoke();
        }
    }

    /**
     * The specified action is invoked when the frame begins, after any calls to {@link #initialize},
     * {@link #load} or {@link #loaded}.
     */
    public final void onBegin(Action onBegin) {
        this.onBegin = onBegin;
    }

    /**
     * The specified action is invoked when the frame ends before any call to {@link #reset}.
     */
    public final void onEnd(Action onEnd) {
        this.onEnd = onEnd;
    }

    public interface LoadAction {
        void invoke() throws LoadInterruptedException;
    }

    private static class DisposerStack {
        private final static Disposable breakMark = () -> {
        };

        private final Deque<Disposable> stack = new ArrayDeque<>();

        public void push(Disposable disposable) {
            stack.addLast(disposable);
        }

        public void pushBreak() {
            stack.addLast(breakMark);
        }

        public void remove(Disposable disposable) {
            stack.removeLastOccurrence(disposable);
        }

        public void disposeUntilBreak() {
            while (!stack.isEmpty()) {
                Disposable e = stack.pollLast();
                if (e == breakMark) {
                    stack.addLast(e);
                    return;
                }
                e.dispose();
            }
        }

        public void dispose() {
            while (!stack.isEmpty()) {
                stack.pollLast().dispose();
            }
        }
    }
}