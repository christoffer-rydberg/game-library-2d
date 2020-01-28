package com.gamelibrary2d.layers;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.exceptions.LoadInterruptedException;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.updaters.Updater;

public interface Frame extends Layer<Renderable>, KeyAware, Disposer {

    /**
     * @return The instance of the game that is running the frame.
     */
    Game getGame();

    /**
     * <p>
     * Called prior to {@link #load} in order to perform non-thread-safe preparations, such
     * as loading textures, which are independent of anything that happens in
     * {@link #load}.
     * </p>
     * <p>
     * This method is called from the main thread of the application.
     * </p>
     */
    void prepare();

    /**
     * @return True if the frame has been prepared by invoking {@link #prepare}, false otherwise.
     */
    boolean isPrepared();

    /**
     * <p>
     * Called after {@link #prepare} but prior to {@link #finish} by the LoadingFrame in a
     * separate thread. This allows the LoadingFrame to be updated and rendered
     * while the next frame is loaded in the background.
     * </p>
     * <p>
     * The drawback is that only thread-safe code is permitted in this method.
     * Additionally, the background thread has no OpenGL-context. Any OpenGL-related
     * functionality (such as loading textures) must be done in {@link #prepare} or
     * {@link #finish}.
     *
     * @throws LoadInterruptedException The load was interrupted.
     */
    void load() throws LoadInterruptedException;

    /**
     * @return True if the frame has been loaded by invoking {@link #load}, false otherwise.
     */
    boolean isLoaded();

    /**
     * <p>
     * Called after to {@link #load} in order to perform non-thread-safe initializations,
     * such as loading textures, which are dependent of what happens in {@link #load}.
     * </p>
     * <p>
     * This method is called from the main thread of the application.
     * </p>
     */
    void finish();

    /**
     * @return True if the frame has been completed by invoking {@link #finish}, false otherwise.
     */
    boolean isFinished();

    /**
     * <p>
     * Called when the frame begins, after any calls to {@link #prepare}, {@link #load} or {@link #finish}.
     * </p>
     * <p>
     * This method is called from the main thread of the application.
     * </p>
     */
    void onBegin();

    /**
     * <p>
     * Called when the frame ends, before any call to {@link #reset}.
     * </p>
     * <p>
     * This method is called from the main thread of the application.
     * </p>
     */
    void onEnd();

    /**
     * Pauses the game.
     */
    void pause();

    /**
     * Resumes the game if {@link #pause paused}.
     */
    void resume();

    /**
     * @return True if the frame has been {@link #pause paused}, false otherwise.
     */
    boolean isPaused();

    /**
     * <p>
     * Called when the frame is changed with the option FrameDisposal.Reset.
     * </p>
     * <p>
     * The method should undo the preparations done in {@link #load} and {@link #finish},
     * leaving only the initialization from {@link #prepare} intact (since it is
     * independent of {@link #load}).
     * </p>
     * <p>
     * The use-case is that a frame can be reloaded faster, for example when
     * providing a "play again"-functionality.
     * </p>
     * <p>
     * This method is called from the main thread of the application
     * </p>
     */
    void reset();

    /**
     * Runs the specified updater until it is {@link Updater#isFinished finished}.
     * Several updaters can run in parallel, but only one instance of the same
     * {@link Updater} can run at once. Invoking this method with an {@link Updater}
     * that is already running will reset the updater if reset is specified to true.
     *
     * @param updater The updater.
     * @param reset   {@link Updater#reset} will be invoked to ensure the updater
     *                runs from the beginning, if this property is set to true.
     */
    void run(Updater updater, boolean reset);
}