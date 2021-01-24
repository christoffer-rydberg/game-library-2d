package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.imaging.ImageAnimation;
import com.gamelibrary2d.markers.Bounded;

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

        this.frames = List.copyOf(frames);
        this.timeIndex = createTimeIndex(this.frames);
        this.bounds = calculateBounds(this.frames);
    }

    private static Rectangle calculateBounds(List<AnimationFrame> frames) {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
        for (var frame : frames) {
            var bounds = frame.getBounds();
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

    private static Animation fromImageAnimation(ImageAnimation imageAnimation, float scaleX, float scaleY, float offsetX, float offsetY, Disposer disposer) {
        var frames = new ArrayList<AnimationFrame>(imageAnimation.getFrames().size());
        for (var frame : imageAnimation.getFrames()) {
            var imageWidth = frame.getImage().getWidth();
            var imageHeight = frame.getImage().getHeight();

            var frameBounds = new Rectangle(0, 0, imageWidth, imageHeight)
                    .move(frame.getOffsetX(), frame.getOffsetY())
                    .resize(scaleX, scaleY)
                    .move(offsetX, offsetY);

            frames.add(AnimationFrame.fromImageAnimationFrame(frame, frameBounds, disposer));
        }

        return new Animation(frames);
    }

    public static Animation fromImageAnimation(ImageAnimation imageAnimation, Rectangle animationBounds, Disposer disposer) {
        var bounds = imageAnimation.getBounds();

        var scaleX = animationBounds.getWidth() / bounds.getWidth();
        var scaleY = animationBounds.getHeight() / bounds.getHeight();

        var offsetX = animationBounds.getLowerX() - (bounds.getLowerX() * scaleX);
        var offsetY = animationBounds.getLowerY() - (bounds.getLowerY() * scaleY);

        return fromImageAnimation(imageAnimation, scaleX, scaleY, offsetX, offsetY, disposer);
    }

    public static Animation fromImageAnimation(ImageAnimation imageAnimation, Rectangle scale, float maxWidth, float maxHeight, Disposer disposer) {
        var bounds = imageAnimation.getBounds();

        // Scale width and height:
        var scaledWith = scale.getWidth() * bounds.getWidth();
        var scaledHeight = scale.getHeight() * bounds.getHeight();
        var aspectRatio = scaledWith / scaledHeight;

        // Restrict to max size:
        var restrictedWidth = Math.min(scaledWith, maxWidth);
        var restrictedHeight = Math.min(scaledHeight, maxHeight);
        var restrictedAspectRatio = restrictedWidth / restrictedHeight;

        // Maintain aspect ratio:
        if (restrictedAspectRatio > aspectRatio) {
            // Restricted animation is too wide
            restrictedWidth = restrictedHeight * aspectRatio;
        } else if (restrictedAspectRatio < aspectRatio) {
            // Restricted animation is too tall
            restrictedHeight = restrictedWidth / aspectRatio;
        }

        // Compute offset:
        var offsetX = (scale.getLowerX() / scale.getWidth()) * restrictedWidth;
        var offsetY = (scale.getLowerY() / scale.getHeight()) * restrictedHeight;

        var animationBounds = new Rectangle(
                offsetX,
                offsetY,
                restrictedWidth + offsetX,
                restrictedHeight + offsetY);

        return fromImageAnimation(imageAnimation, animationBounds, disposer);
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
