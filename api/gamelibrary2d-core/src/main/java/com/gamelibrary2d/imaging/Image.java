package com.gamelibrary2d.imaging;

import java.nio.ByteBuffer;

public interface Image {
    ByteBuffer getData();

    int getWidth();

    int getHeight();

    int getChannels();
}
