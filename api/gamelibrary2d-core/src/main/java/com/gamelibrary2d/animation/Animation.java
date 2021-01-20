package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Bounded;

import java.util.Collection;
import java.util.List;

public class Animation implements Bounded {
    private final List<AnimationFrame> frames;
    private final float[] timeIndex;
    private final Rectangle bounds;

    public Animation(Collection<AnimationFrame> frames) {
        if (frames.isEmpty()) {
            throw new IllegalStateException("An animation must contain at least one frame");
        }

        this.frames = List.copyOf(frames);
        this.timeIndex = createTimeIndex(this.frames);
        this.bounds = calculateBounds(this.frames);
    }

    private static Rectangle calculateBounds(List<AnimationFrame> frames) {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
        for (var frame : frames) {
            var bounds = frame.getSurface().getBounds();
            xMin = Math.min(xMin, bounds.getLowerX());
            yMin = Math.min(yMin, bounds.getLowerY());
            xMax = Math.max(xMax, bounds.getUpperX());
            yMax = Math.max(yMax, bounds.getUpperY());
        }

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    private static float[] createTimeIndex(List<AnimationFrame> frames) {
        var index = new float[frames.size()];
        float duration = 0f;
        for (int i = 0; i < index.length; ++i) {
            var frame = frames.get(i);
            duration += frame.getDurationHint();
            index[i] = duration;
        }

        return index;
    }

    public float getDuration() {
        return timeIndex[timeIndex.length - 1];
    }

    public boolean isFrameActive(int frameIndex, float animationTime) {
        var frameStart = frameIndex == 0 ? 0 : timeIndex[frameIndex - 1];
        var frameEnd = timeIndex[frameIndex];
        return animationTime >= frameStart && animationTime < frameEnd;
    }

    public int getFrameIndex(float time) {
        if (time < 0f) {
            return 0;
        } else if (time > getDuration()) {
            return frames.size() - 1;
        } else {
            int start = 0, end = timeIndex.length;
            while (true) {
                var index = (start + end) / 2;
                var frameStart = index == 0 ? 0 : timeIndex[index - 1];
                var frameEnd = timeIndex[index];
                if (time >= frameEnd) {
                    start = index;
                } else if (time < frameStart) {
                    end = index;
                } else {
                    return index;
                }
            }
        }
    }

    public List<AnimationFrame> getFrames() {
        return frames;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public AnimationFrame getFrame(int index) {
        return frames.get(index);
    }
}
