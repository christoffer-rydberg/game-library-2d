package com.gamelibrary2d.network;

import com.gamelibrary2d.frames.Frame;

public interface NetworkFrame<TFrameClient extends FrameClient> extends Frame {
    TFrameClient getClient();
}