package com.gamelibrary2d.imaging;

import com.gamelibrary2d.common.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageAnimation {
    private final List<ImageAnimationFrame> frames;
    private final Rectangle bounds;

    public ImageAnimation(Collection<ImageAnimationFrame> frames) {
        if (frames.isEmpty()) {
            throw new IllegalStateException("An animation must contain at least one frame");
        }

        this.frames = new ArrayList<>(frames);
        this.bounds = calculateBounds(frames);
    }

    private static Rectangle calculateBounds(Collection<ImageAnimationFrame> frames) {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
        for (ImageAnimationFrame frame : frames) {
            float lowerX = frame.getOffsetX();
            float upperX = lowerX + frame.getImage().getWidth();
            float lowerY = frame.getOffsetY();
            float upperY = lowerY + frame.getImage().getHeight();

            xMin = Math.min(xMin, lowerX);
            yMin = Math.min(yMin, lowerY);
            xMax = Math.max(xMax, upperX);
            yMax = Math.max(yMax, upperY);
        }

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    public List<ImageAnimationFrame> getFrames() {
        return frames;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
