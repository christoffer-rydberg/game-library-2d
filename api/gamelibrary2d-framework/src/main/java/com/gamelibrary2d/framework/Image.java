package com.gamelibrary2d.framework;

import java.nio.ByteBuffer;

public interface Image {
    ByteBuffer getData();

    int getWidth();

    int getHeight();

    int getChannels();
}
