package com.gamelibrary2d.frames;

public interface LoadingFrame extends Frame {
    void load(Frame frame, Frame previousFrame, FrameDisposal previousFrameDisposal);
}