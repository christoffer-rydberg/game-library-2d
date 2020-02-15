package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
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
    private boolean prepared;
    private boolean loaded;
    private boolean finished;

    protected AbstractFrame(Game game) {
        this.game = game;
    }

    @Override
    public void register(Disposable disposable) {
        disposer.push(disposable);
    }

    @Override
    public void prepare() {
        if (isDisposed()) {
            throw new GameLibrary2DRuntimeException("This object has been disposed.");
        }

        if (isPrepared())
            return;

        game.register(this);

        disposer = new DisposerStack();

        updaters = new ArrayDeque<>();

        onPrepare();

        disposer.pushBreak();

        prepared = true;
    }

    @Override
    public boolean isPrepared() {
        return prepared;
    }

    @Override
    public void load() throws LoadInterruptedException {
        if (isLoaded())
            return;

        if (!isPrepared()) {
            throw new LoadInterruptedException("Must call prepare prior to load");
        }

        try {
            onLoad();
        } catch (Exception e) {
            e.printStackTrace();
            reset();
            throw e instanceof LoadInterruptedException ? (LoadInterruptedException) e
                    : new LoadInterruptedException(e.getMessage());
        }

        loaded = true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void finish() {
        if (isFinished())
            return;

        if (!isLoaded()) {
            System.err.println("Must call load() prior to complete()");
            return;
        }

        onFinish();

        finished = true;
    }

    @Override
    public void reset() {
        // Dispose all resources created after the preparation phase.
        disposer.disposeUntilBreak();
        commonCleanUp();
    }

    @Override
    public void dispose() {
        if (isDisposed())
            return;

        disposer.dispose();
        commonCleanUp();
        prepared = false;
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
        finished = false;
        loaded = false;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    protected void run(Updater updater) {
        run(updater, true);
    }

    @Override
    public void run(Updater updater, boolean reset) {
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
    public void onCharInput(char charInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {
        // TODO Auto-generated method stub

    }

    /**
     * Called prior to {@link #load} in order to perform non-thread-safe
     * initialization, which are independent of what happens during the loading
     * phase. When {@link #reset resetting} this frame, the preparations done in
     * this method will be kept intact so that the frame efficiently can be reused.
     */
    protected abstract void onPrepare();

    /**
     * Called after {@link #prepare} but prior to {@link #finish}. All
     * initialization code (needed to reset the frame) should be placed here or in
     * the {@link #onFinish} method. If a {@link LoadingFrame} is used, this method
     * will not be invoked from the main thread. This allows the loading frame to be
     * updated and rendered while this frame is loaded in the background.
     * <p>
     * <b>Note:</b> The thread invoking this method from the loading frame has no
     * OpenGL-context. Any OpenGL-related functionality, such as loading textures,
     * must be done in {@link #prepare} or {@link #finish}.
     * </p>
     *
     * @throws LoadInterruptedException Occurs if the frame fails to load.
     */
    protected abstract void onLoad() throws LoadInterruptedException;

    /**
     * Called after {@link #load} in order to perform initialization that isn't
     * thread safe. Only code that needs to run after the frame has loaded should be
     * placed here. In other case, consider placing it in {@link #onPrepare}.
     */
    protected abstract void onFinish();

    /**
     * Called when the frame begins, after any calls to {@link #prepare},
     * {@link #load} or {@link #finish}.
     */
    public abstract void onBegin();

    /**
     * Called when the frame ends before any call to {@link #reset}.
     */
    public abstract void onEnd();

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