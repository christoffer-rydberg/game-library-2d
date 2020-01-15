package com.gamelibrary2d.objects;

public interface LoadingFrame extends Frame {

    Frame getFallBackFrame();

    void setFallBackFrame(Frame fallBackFrame);

    Frame getPreviousFrame();

    void setPreviousFrame(Frame previousFrame);

    Frame getNextFrame();

    void setNextFrame(Frame nextFrame);

    void loadNextFrame();

}