package com.example.framework.android;

import android.graphics.Bitmap;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.Image;

import java.nio.ByteBuffer;

public class BitmapParser {
    private static int[] getArgb(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    private static int[] flipYAxis(int[] argb, int width, int height) {
        int startOffset = 0;
        int endOffset = argb.length - width;
        int[] flipped = new int[argb.length];
        for (int row = 0; row < height; ++row) {
            System.arraycopy(argb, startOffset, flipped, endOffset, width);
            startOffset += width;
            endOffset -= width;
        }

        return flipped;
    }

    private static Image parseImage(int[] argb, int width, int height, boolean hasAlphaChannel) {
        int channels = hasAlphaChannel ? 4 : 3;
        ByteBuffer buffer = BufferUtils.createByteBuffer(argb.length * channels);
        for (int pixel : argb) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
            buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
            buffer.put((byte) (pixel & 0xFF));         // B
            if (hasAlphaChannel) {
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }

        buffer.flip();

        return new DefaultImage(buffer, width, height, channels);
    }

    public Image parse(Bitmap bitmap) {
        int[] argb = getArgb(bitmap);
        int[] argbFlipped = flipYAxis(argb, bitmap.getWidth(), bitmap.getHeight());
        return parseImage(argbFlipped, bitmap.getWidth(), bitmap.getHeight(), bitmap.hasAlpha());
    }

    private static class DefaultImage implements Image {
        private final ByteBuffer data;
        private final int width;
        private final int height;
        private final int channels;

        DefaultImage(ByteBuffer data, int width, int height, int channels) {
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
}
