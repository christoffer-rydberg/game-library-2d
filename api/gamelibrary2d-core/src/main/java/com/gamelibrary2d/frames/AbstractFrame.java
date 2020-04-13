package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.AbstractLayer;
import com.gamelibrary2d.updaters.Updater;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractFrame extends AbstractLayer<Renderable> implements Frame {
    private final Deque<Runnable> invokeLater = new ArrayDeque<>();

    private Game game;
    private DisposerStack disposer;
    private Deque<Updater> updaters;
    private boolean paused;
    private boolean disposed;
    private boolean initialized;
    private volatile boolean loaded;

    protected AbstractFrame(Game game) {
        this.game = game;
    }

    public void invokeLater(Runnable runnable) {
        invokeLater.addLast(runnable);
    }

    @Override
    public void registerDisposal(Disposable disposable) {
        disposer.push(disposable);
    }

    @Override
    public void initialize() throws InitializationException {
        if (disposed) {
            throw new InitializationException("This object has been disposed");
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
    public void load(LoadingContext context) throws InitializationException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            throw new InitializationException("Frame has not been initialized");
        }

        onLoad(context);

        loaded = true;
    }

    @Override
    public void loaded(LoadingContext context) {
        onLoaded(context);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void dispose(FrameDisposal disposal) {
        switch (disposal) {
            case NONE:
                break;
            case UNLOAD:
                unload();
                break;
            case DISPOSE:
                dispose();
                break;
        }
    }

    protected void unload() {
        // Dispose all resources created after the initialization phase.
        disposer.disposeUntilBreak();
        commonCleanUp();
    }

    @Override
    public void dispose() {
        if (!disposed) {
            disposer.dispose();
            commonCleanUp();
            initialized = false;
            game = null;
            disposer = null;
            updaters = null;
            disposed = true;
        }
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

        while (!invokeLater.isEmpty()) {
            invokeLater.pollFirst().run();
        }
    }

    @Override
    public Game getGame() {
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

    @Override
    public void begin() {
        if (!isLoaded()) {
            throw new GameLibrary2DRuntimeException("Frame has not been loaded");
        }

        onBegin();
    }

    @Override
    public void end() {
        onEnd();
        invokeLater.clear();
    }

    protected abstract void onInitialize() throws InitializationException;

    protected abstract void onLoad(LoadingContext context) throws InitializationException;

    protected abstract void onLoaded(LoadingContext context);

    protected abstract void onBegin();

    protected abstract void onEnd();

    private static class DisposerStack {
        private final static Disposable breakMark = () -> {
        };

        private final Deque<Disposable> stack = new ArrayDeque<>();

        void push(Disposable disposable) {
            stack.addLast(disposable);
        }

        void pushBreak() {
            stack.addLast(breakMark);
        }

        void disposeUntilBreak() {
            while (!stack.isEmpty()) {
                Disposable e = stack.pollLast();
                if (e == breakMark) {
                    stack.addLast(e);
                    return;
                }
                e.dispose();
            }
        }

        void dispose() {
            while (!stack.isEmpty()) {
                stack.pollLast().dispose();
            }
        }
    }
}