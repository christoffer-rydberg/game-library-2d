package com.gamelibrary2d.components.frames;

public enum FrameDisposal {

    /**
     * Nothing is disposed.
     */
    NONE,

    /**
     * The frame is unloaded. Initialization and resources created in {@link Frame#initialize} is left intact.
     * The frame can be reloaded by calling {@link Frame#load}.
     */
    UNLOAD,

    /**
     * The frame is fully disposed using this option is the same as calling {@link Frame#dispose()}.
     */
    DISPOSE,
}