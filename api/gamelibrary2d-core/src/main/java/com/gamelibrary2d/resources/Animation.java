package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.imaging.ImageAnimation;
import com.gamelibrary2d.imaging.ImageAnimationFrame;
import com.gamelibrary2d.components.denotations.Bounded;

import java.util.ArrayList;
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

        this.frames = new ArrayList<>(frames);
        this.timeIndex = createTimeIndex(this.frames);
        this.bounds = calculateBounds(this.frames);
    }

    private static Rectangle calculateBounds(List<AnimationFrame> frames) {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
        for (AnimationFrame frame : frames) {
            Rectangle bounds = frame.getBounds();
            xMin = Math.min(xMin, bounds.getLowerX());
            yMin = Math.min(yMin, bounds.getLowerY());
            xMax = Math.max(xMax, bounds.getUpperX());
            yMax = Math.max(yMax, bounds.getUpperY());
        }

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    private static float[] createTimeIndex(List<AnimationFrame> frames) {
        float[] index = new float[frames.size()];
        float duration = 0f;
        for (int i = 0; i < index.length; ++i) {
            AnimationFrame frame = frames.get(i);
            duration += frame.getDurationHint();
            index[i] = duration;
        }

        return index;
    }

    private static Animation fromImageAnimation(ImageAnimation imageAnimation, float scaleX, float scaleY, float offsetX, float offsetY, Disposer disposer) {
        List<AnimationFrame> frames = new ArrayList<>(imageAnimation.getFrames().size());
        for (ImageAnimationFrame frame : imageAnimation.getFrames()) {
            int imageWidth = frame.getImage().getWidth();
            int imageHeight = frame.getImage().getHeight();

            Rectangle frameBounds = new Rectangle(0, 0, imageWidth, imageHeight)
                    .move(frame.getOffsetX(), frame.getOffsetY())
                    .resize(scaleX, scaleY)
                    .move(offsetX, offsetY);

            frames.add(AnimationFrame.fromImageAnimationFrame(frame, frameBounds, disposer));
        }

        return new Animation(frames);
    }

    public static Animation fromImageAnimation(ImageAnimation imageAnimation, Rectangle size, Disposer disposer) {
        Rectangle bounds = imageAnimation.getBounds();

        float scaleX = size.getWidth() / bounds.getWidth();
        float scaleY = size.getHeight() / bounds.getHeight();

        float offsetX = size.getLowerX() - (bounds.getLowerX() * scaleX);
        float offsetY = size.getLowerY() - (bounds.getLowerY() * scaleY);

        return fromImageAnimation(imageAnimation, scaleX, scaleY, offsetX, offsetY, disposer);
    }

    public float getDuration() {
        return timeIndex[timeIndex.length - 1];
    }

    public boolean isFrameActive(int frameIndex, float animationTime) {
        float frameStart = frameIndex == 0 ? 0 : timeIndex[frameIndex - 1];
        float frameEnd = timeIndex[frameIndex];
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
                int index = (start + end) / 2;
                float frameStart = index == 0 ? 0 : timeIndex[index - 1];
                float frameEnd = timeIndex[index];
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
