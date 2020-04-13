package com.gamelibrary2d.frames;

import com.gamelibrary2d.exceptions.InitializationException;

public interface LoadingFrame extends Frame {
    void load(Frame frame, Frame previousFrame, FrameDisposal previousFrameDisposal) throws InitializationException;
}