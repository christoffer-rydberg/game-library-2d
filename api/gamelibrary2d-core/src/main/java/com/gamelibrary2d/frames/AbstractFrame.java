package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
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

    private LoadAction loadAction;
    private Action loadedAction;
    private Action beginAction;
    private Action endAction;

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

        var frameInitializer = new FrameInitializer();
        initializeFrame(frameInitializer);
        loadAction = frameInitializer.load;
        loadedAction = frameInitializer.loaded;
        beginAction = frameInitializer.begin;
        endAction = frameInitializer.end;

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

        if (loadAction != null) {
            try {
                loadAction.invoke();
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

        if (loadedAction != null) {
            loadedAction.invoke();
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

    protected abstract void initializeFrame(FrameInitializer initializer);

    public void begin() {
        if (beginAction != null) {
            beginAction.invoke();
        }
    }

    public void end() {
        if (endAction != null) {
            endAction.invoke();
        }
    }

    public interface LoadAction {
        void invoke() throws LoadInterruptedException;
    }

    protected static class FrameInitializer {
        private LoadAction load;
        private Action loaded;
        private Action begin;
        private Action end;

        /**
         * The specified action is invoked when the frame is loaded. A frame should typically be reloadable after a
         * {@link Frame#reset reset}. It is good practice to place initialization logic, such as initial objects
         * and positions, inside this action.
         * <br>
         * <br>
         * <strong>Important:</strong> The load-action is invoked by a {@link LoadingFrame} on a separate thread.
         * Although no other code in the frame should run in parallel, thread-safety must be considered -
         * especially in regards to thread caching. Another important note is that no OpenGL context is available.
         * By convention, static "create"-methods accepting a {@link Disposer} requires an OpenGL context. Consider
         * placing non-thread safe code or OpenGL-related calls in the {@link #onLoaded} action.
         */
        public final void onLoad(LoadAction action) {
            this.load = action;
        }

        /**
         * The specified action is invoked from the main thread when the frame has loaded.
         */
        public final void onLoaded(Action action) {
            this.loaded = action;
        }

        /**
         * The specified action is invoked when the frame begins.
         */
        public final void onBegin(Action action) {
            this.begin = action;
        }

        /**
         * The specified action is invoked when the frame ends.
         */
        public final void onEnd(Action action) {
            this.end = action;
        }
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