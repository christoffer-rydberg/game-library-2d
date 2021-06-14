package com.gamelibrary2d;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.event.EventListener;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.components.frames.Frame;
import com.gamelibrary2d.components.frames.FrameDisposal;
import com.gamelibrary2d.components.frames.LoadingFrame;
import com.gamelibrary2d.framework.GameLoop;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.components.denotations.Updatable;

public interface Game extends Disposer, Updatable {

    /**
     * Starts the game inside the specified {@link Window}.
     */
    void start(Window window, GameLoop gameLoop) throws InitializationException;

    default void start(Window window) throws InitializationException {
        start(window, Runtime.getFramework().createDefaultGameLoop());
    }

    Window getWindow();

    void setViewPort(int x, int y, int width, int height);

    void setBackgroundColor(Color color);

    void render();

    /**
     * Called each update tick in order to update and render the current frame.
     *
     * @param delta - Time since prior update.
     */
    @Override
    void update(float delta);

    /**
     * Used to invoke code at the end of the update cycle.
     */
    void invokeLater(Runnable runnable);

    /**
     * @return The {@link LoadingFrame} that is displayed when a frame is being {@link #loadFrame loaded}.
     */
    LoadingFrame getLoadingFrame();

    /**
     * Sets the {@link #getLoadingFrame() loading frame}.
     */
    void setLoadingFrame(LoadingFrame frame);

    /**
     * Sets the specified frame.
     *
     * @param frame                 - The new frame.
     * @param previousFrameDisposal - Disposal of previous frame.
     */
    void setFrame(Frame frame, FrameDisposal previousFrameDisposal) throws InitializationException;

    /**
     * Loads the specified frame while showing the set {@link #getLoadingFrame() loading frame}.
     *
     * @param frame                 - The new frame.
     * @param previousFrameDisposal - Disposal of previous frame.
     */
    void loadFrame(Frame frame, FrameDisposal previousFrameDisposal) throws InitializationException;

    /**
     * Gets the current frame.
     */
    Frame getFrame();

    /**
     * @param id - The pointer id.
     * @return - True if the pointer is within the game area..
     */
    boolean hasPointerFocus(int id);

    /**
     * Exists the game after the current update cycle.
     */
    void exit();

    /**
     * The frame changed event occurs when a frame has been set, right before <@link
     * {@link Frame#begin()} is invoked.
     */
    void addFrameChangedListener(FrameChangedListener listener);

    /**
     * Removes the specified {@link #addFrameChangedListener frame changed} event
     * listener.
     */
    void removeFrameChangedListener(FrameChangedListener listener);

    interface FrameChangedListener extends EventListener<Frame> {

    }
}