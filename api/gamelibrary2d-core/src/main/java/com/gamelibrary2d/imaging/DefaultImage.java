package com.gamelibrary2d.imaging;

import com.gamelibrary2d.framework.Image;

public class DefaultImage implements Image {
    private final byte[] data;
    private final int width;
    private final int height;
    private final int channels;

    public DefaultImage(byte[] data, int width, int height, int channels) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public byte[] getData() {
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
