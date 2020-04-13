package com.gamelibrary2d.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.updaters.Updater;

public interface Frame extends Layer<Renderable>, Disposer {

    /**
     * Initializes the frame. This method is always invoked from the main thread with an OpenGL context available.method.
     */
    void initialize() throws InitializationException;

    /**
     * @return True if the frame has been {@link #initialize initialized}, false otherwise.
     */
    boolean isInitialized();

    /**
     * Typically invoked from a separate thread by a {@link LoadingFrame}.
     * No OpenGL context is available and the implementation of this method must be thread safe.
     * Loaded resources should be registered in the specified {@link LoadingContext} and can be
     * added to the frame in a thread-safe manner when {@link #loaded} is invoked.
     * <br>
     * <br>
     * This method is invoked after {@link #initialize}.
     *
     * @param context Used to registered loaded items.
     * @throws InitializationException
     */
    void load(LoadingContext context) throws InitializationException;

    /**
     * Invoked after {@link #load} from the main thread with the loaded resources.
     *
     * @param context The loaded context.
     */
    void loaded(LoadingContext context) throws InitializationException;

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
     * Disposes the frame according to the specified {@link FrameDisposal}.
     */
    void dispose(FrameDisposal disposal);

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