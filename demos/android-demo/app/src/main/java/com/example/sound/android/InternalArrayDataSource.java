package com.example.sound.android;

import android.media.MediaDataSource;

class InternalArrayDataSource extends MediaDataSource {
    private final byte[] data;

    public InternalArrayDataSource(byte[] data) {
        this.data = data;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) {
        int sourceOffset = (int) position;
        int remaining = data.length - sourceOffset;
        int length = Math.min(remaining, size);

        if (length == 0) {
            return -1;
        }

        System.arraycopy(data, sourceOffset, buffer, offset, length);

        return length;
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public void close() {

    }
}
