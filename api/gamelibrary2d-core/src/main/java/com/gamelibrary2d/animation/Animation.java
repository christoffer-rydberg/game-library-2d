package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Animation {
    public static Animation EMPTY = new Animation(new ArrayList<>(), Rectangle.EMPTY);

    private final Rectangle bounds;
    private final ArrayList<AnimationFrame> frames;
    private final int originalWidth;
    private final int originalHeight;

    public Animation(Collection<AnimationFrame> frames, Rectangle bounds, int originalWidth, int originalHeight) {
        this.frames = new ArrayList<>(frames);
        this.bounds = bounds;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
    }

    public Animation(Collection<AnimationFrame> frames, Rectangle bounds) {
        this(frames, bounds, Math.round(bounds.getWidth()), Math.round(bounds.getHeight()));
    }

    public Animation(ArrayList<AnimationFrame> frames) {
        this(frames, computeBounds(frames));
    }

    private static Rectangle computeBounds(Iterable<AnimationFrame> frames) {
        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float xMax = -Float.MAX_VALUE;
        float yMax = -Float.MAX_VALUE;
        for (var frame : frames) {
            var bounds = frame.getQuad().getBounds();
            xMin = Math.min(xMin, bounds.getXMin());
            yMin = Math.min(yMin, bounds.getYMin());
            xMax = Math.max(xMax, bounds.getXMax());
            yMax = Math.max(yMax, bounds.getYMax());
        }

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    public Iterable<AnimationFrame> getFrames() {
        return Collections.unmodifiableCollection(frames);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public AnimationFrame getFrame(int index) {
        return frames.get(index);
    }

    public int getFrameCount() {
        return frames.size();
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }
}
