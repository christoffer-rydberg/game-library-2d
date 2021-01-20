package com.gamelibrary2d.animation.io;

import com.gamelibrary2d.animation.Animation;
import com.gamelibrary2d.animation.AnimationFrame;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnimationMetadata {
    private final List<AnimationFrameMetadata> frames;
    private final Rectangle bounds;

    public AnimationMetadata(Collection<AnimationFrameMetadata> frames) {
        if (frames.isEmpty()) {
            throw new IllegalStateException("An animation must contain at least one frame");
        }

        this.frames = List.copyOf(frames);
        this.bounds = calculateBounds(frames);
    }

    private static Rectangle calculateBounds(Collection<AnimationFrameMetadata> frames) {
        float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
        for (var frame : frames) {
            var lowerX = frame.getOffsetX();
            var upperX = lowerX + frame.getImage().getWidth();
            var lowerY = frame.getOffsetY();
            var upperY = lowerY + frame.getImage().getHeight();

            xMin = Math.min(xMin, lowerX);
            yMin = Math.min(yMin, lowerY);
            xMax = Math.max(xMax, upperX);
            yMax = Math.max(yMax, upperY);
        }

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    public List<AnimationFrameMetadata> getFrames() {
        return frames;
    }

    private Animation createAnimationInternal(float scaleX, float scaleY, float offsetX, float offsetY, Disposer disposer) {
        var frames = new ArrayList<AnimationFrame>(this.frames.size());
        for (var frame : this.frames) {
            var imageWidth = frame.getImage().getWidth();
            var imageHeight = frame.getImage().getHeight();

            var frameBounds = new Rectangle(0, 0, imageWidth, imageHeight)
                    .move(frame.getOffsetX(), frame.getOffsetY())
                    .resize(scaleX, scaleY)
                    .move(offsetX, offsetY);

            frames.add(frame.createAnimationFrame(frameBounds, disposer));
        }

        return new Animation(frames);
    }

    public Animation createAnimation(Rectangle animationBounds, Disposer disposer) {
        var scaleX = animationBounds.getWidth() / this.bounds.getWidth();
        var scaleY = animationBounds.getHeight() / this.bounds.getHeight();

        var offsetX = animationBounds.getLowerX() - (this.bounds.getLowerX() * scaleX);
        var offsetY = animationBounds.getLowerY() - (this.bounds.getLowerY() * scaleY);

        return createAnimationInternal(scaleX, scaleY, offsetX, offsetY, disposer);
    }

    public Animation createAnimation(Rectangle scale, float maxWidth, float maxHeight, Disposer disposer) {
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

        return createAnimation(animationBounds, disposer);
    }
}
