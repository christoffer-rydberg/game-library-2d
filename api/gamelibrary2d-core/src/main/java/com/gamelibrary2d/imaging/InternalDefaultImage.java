package com.gamelibrary2d.imaging;

import java.nio.ByteBuffer;

class InternalDefaultImage implements Image {
    private final ByteBuffer data;
    private final int width;
    private final int height;
    private final int channels;

    InternalDefaultImage(ByteBuffer data, int width, int height, int channels) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }
}
