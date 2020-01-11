package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.LoadInterruptedException;
import com.gamelibrary2d.updaters.Updater;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractFrame extends AbstractModifiableContainer<GameObject> implements Frame {

    private Game game;

    private InternalDisposerStack disposer;

    private Deque<Updater> updaters;

    private boolean paused;

    private boolean disposed;
    private boolean prepared;
    private boolean loaded;
    private boolean finished;

    protected AbstractFrame(Game game) {
        this.game = game;
        setAutoClearing(true);
    }

    @Override
    public Rectangle getBounds() {
        return Rectangle.INFINITE;
    }

    @Override
    public void register(Disposable disposable) {
        disposer.push(disposable);
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused) {
            throw new GameLibrary2DRuntimeException("Frames are not focusable.");
        }
    }

    @Override
    public void prepare() {

        if (isDisposed()) {
            throw new GameLibrary2DRuntimeException("This object has been disposed.");
        }

        if (isPrepared())
            return;

        game.register(this);

        disposer = new InternalDisposerStack();

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
        onReset();
        // Dispose all resources created after the preparation phase.
        disposer.disposeUntilBreak();
        commonCleanUp();
    }

    @Override
    public void dispose() {
        if (isDisposed())
            return;

        onDispose();
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
    public void update(float deltaTime) {
        if (!isPaused()) {
            super.update(deltaTime);
            for (int i = 0; i < updaters.size(); ++i) {
                Updater updater = updaters.pollFirst();
                updater.update(deltaTime);
                if (!updater.isFinished()) {
                    updaters.addLast(updater);
                }
            }
        }

        onUpdate(deltaTime);
    }

    @Override
    public Point getScaleAndRotationCenter() {
        Point scaleAndRotationCenter = super.getScaleAndRotationCenter();
        scaleAndRotationCenter.set(game.getWindow().getWidth() / 2f, game.getWindow().getHeight() / 2f);
        return scaleAndRotationCenter;
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

    /**
     * Called on each update prior to {@link #render}.
     *
     * @param deltaTime time passed since the last update in seconds
     */
    protected abstract void onUpdate(float deltaTime);

    /**
     * Called upon resetting the frame. This method is responsible for releasing
     * resources and undoing changes, since the {@link #onPrepare} method was
     * called. The purpose is to be able to reuse the frame, either directly (play
     * again) or at a later stage. By keeping all resources allocated in the
     * preparation stage, the frame can more efficiently be reused. If the frame is
     * not to be reused, it should be {@link #dispose} instead of {@link #reset}.
     */
    protected abstract void onReset();

    /**
     * Called upon disposing the frame.
     */
    protected abstract void onDispose();
}