package com.gamelibrary2d.widgets;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.input.ButtonAction;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.objects.AbstractMouseAwareObject;

abstract class InternalAbstractWidget<T extends Renderable>
        extends AbstractMouseAwareObject<T> implements FocusAware, MouseWhenFocusedAware {

    private boolean awareOfMouseEvent;

    protected InternalAbstractWidget() {

    }

    protected InternalAbstractWidget(T content) {
        super(content);
    }

    @Override
    protected final void handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        awareOfMouseEvent = true;
        if (focusOnMouseButtonAction(ButtonAction.PRESSED, button, mods, projectedX, projectedY)) {
            FocusManager.focus(this, false);
        }
        onHandleMouseButtonDown(button, mods, projectedX, projectedY);
    }

    @Override
    protected final void handleMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        awareOfMouseEvent = true;
        if (focusOnMouseButtonAction(ButtonAction.RELEASED, button, mods, projectedX, projectedY)) {
            FocusManager.focus(this, false);
        }
        onHandleMouseButtonReleased(button, mods, projectedX, projectedY);
    }

    @Override
    public final void onMouseButtonWhenFocused(int button, ButtonAction action, int mods) {
        if (!awareOfMouseEvent) {
            onMissedMouseButtonEvent(button, action, mods);
        }
        awareOfMouseEvent = false;
    }

    /**
     * Override in order to change which buttons and actions are used to focus this object.
     */
    protected boolean focusOnMouseButtonAction(ButtonAction buttonAction, int button, int mods, float projectedX, float projectedY) {
        return buttonAction == ButtonAction.RELEASED;
    }

    /**
     * Invoked if this object is focused and a mouse button event is detected outside the object.
     *
     * @param button The mouse button that was pressed/released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    protected void onMissedMouseButtonEvent(int button, ButtonAction action, int mods) {
        FocusManager.unfocus(this, false);
    }

    protected abstract void onHandleMouseButtonDown(int button, int mods, float projectedX, float projectedY);

    protected abstract void onHandleMouseButtonReleased(int button, int mods, float projectedX, float projectedY);
}