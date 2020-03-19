package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.updaters.Updater;

public interface Frame extends Layer<Renderable>, Disposer {

    /**
     * Initializes the frame. This method is always invoked from the main thread with an OpenGL context available.
     * Thread-safe and OpenGL-unrelated initialization is performed by the {@link #load} method.
     */
    void initialize();

    /**
     * @return True if the frame has been {@link #initialize initialized}, false otherwise.
     */
    boolean isInitialized();

    /**
     * Typically invoked from a separate thread by a {@link LoadingFrame}.
     * No OpenGL context is available and the implementation of this method must be thread safe.
     * This method is invoked after {@link #initialize}.
     */
    void load() throws LoadFailedException;

    /**
     * @return True if the frame has been {@link #load loaded}, false otherwise.
     */
    boolean isLoaded();

    /**
     * Invoked when the frame begins.
     */
    void begin();

    /**
     * Invoked when the frame ends.
     */
    void end();

    /**
     * @return The game instance.
     */
    Game getGame();

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
     * Invoked when the frame is changed with the option FrameDisposal.Reset.
     * {@link #isLoaded Loaded} is set to false but any initialization done in {@link #initialize} is left intact.
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
    void runUpdater(Updater updater, boolean reset);

    void invokeLater(Runnable runnable);
}