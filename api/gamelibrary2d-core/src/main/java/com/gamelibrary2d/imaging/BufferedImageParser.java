package com.gamelibrary2d.imaging;

import com.gamelibrary2d.framework.Image;

import java.awt.image.BufferedImage;

public class BufferedImageParser {
    private static int[] getArgb(BufferedImage image) {
        return image.getRGB(
                0,
                0,
                image.getWidth(),
                image.getHeight(),
                null,
                0,
                image.getWidth());
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

        int i = 0;
        byte[] buffer = new byte[argb.length * channels];
        for (int pixel : argb) {
            buffer[i] = (byte) ((pixel >> 16) & 0xFF);    // R
            buffer[i + 1] = (byte) ((pixel >> 8) & 0xFF); // G
            buffer[i + 2] = (byte) (pixel & 0xFF);        // B
            if (hasAlphaChannel) {
                buffer[i + 3] = (byte) ((pixel >> 24) & 0xFF); // A
                i += 4;
            } else {
                i += 3;
            }
        }

        return new DefaultImage(buffer, width, height, channels);
    }

    public Image parse(BufferedImage image) {
        int[] argb = getArgb(image);
        int[] argbFlipped = flipYAxis(argb, image.getWidth(), image.getHeight());
        return parseImage(argbFlipped, image.getWidth(), image.getHeight(), image.getColorModel().hasAlpha());
    }
}
