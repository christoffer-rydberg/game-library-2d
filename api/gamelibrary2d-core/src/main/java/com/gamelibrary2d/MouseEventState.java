package com.gamelibrary2d;

public class MouseEventState {
    private static boolean handlingEvent;

    /**
     * Can be invoked from the main thread to determine if
     * code is running inside the handler of a mouse event.
     */
    public static boolean isHandlingEvent() {
        return handlingEvent;
    }

    static void setHandlingEvent(boolean handlingEvent) {
        MouseEventState.handlingEvent = handlingEvent;
    }
}
