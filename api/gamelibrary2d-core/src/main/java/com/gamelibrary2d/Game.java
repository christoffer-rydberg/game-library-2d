package com.gamelibrary2d;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.eventlisteners.FrameChangedListener;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.FrameDisposal;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.markers.Updatable;

public interface Game extends Disposer, Updatable {

    /**
     * Starts the game inside the specified {@link Window}.
     */
    void start(Window window);

    Window getWindow();

    void setViewPort(int x, int y, int width, int height);

    void setBackgroundColor(Color color);

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
     * Sets the current frame. If the game is in the middle of an update cycle, the
     * call to this method will be delayed and invoked at the end of the cycle.
     *
     * @param frame                 - New frame.
     * @param previousFrameDisposal - Disposal of previous frame.
     */
    void setFrame(Frame frame, FrameDisposal previousFrameDisposal);

    /**
     * Gets the current frame.
     */
    Frame getFrame();

    /**
     * @return The current frame rate.
     */
    float getFPS();

    /**
     * @return - True if the cursor overlaps the game, otherwise false.
     */
    boolean hasCursorFocus();

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
}