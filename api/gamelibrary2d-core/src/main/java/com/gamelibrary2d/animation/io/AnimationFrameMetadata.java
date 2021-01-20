package com.gamelibrary2d.animation.io;

import com.gamelibrary2d.animation.AnimationFrame;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;

import java.awt.image.BufferedImage;

public class AnimationFrameMetadata {
    private final BufferedImage img;
    private final Rectangle imageCoordinates;
    private final float offsetX;
    private final float offsetY;
    private final float durationHint;
    private final boolean restoreBackgroundHint;
    private final boolean renderToBackgroundHint;

    /**
     * Creates a new instance of {@link AnimationFrameMetadata}.
     *
     * @param img                The frame image.
     * @param imageCoordinates   The image coordinates used for this frame.
     * @param offsetX            The offset along the X axis.
     * @param offsetY            The offset along the Y axis.
     * @param duration           Sets the {@link AnimationFrameMetadata#getDurationHint} field.
     * @param restoreBackground  Sets the {@link AnimationFrameMetadata#restoreBackgroundHint} field.
     * @param renderToBackground Sets the {@link AnimationFrameMetadata#getRenderToBackgroundHint} field.
     */
    public AnimationFrameMetadata(
            BufferedImage img,
            Rectangle imageCoordinates,
            float offsetX,
            float offsetY,
            float duration,
            boolean restoreBackground,
            boolean renderToBackground) {
        this.img = img;
        this.imageCoordinates = imageCoordinates;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.durationHint = duration;
        this.restoreBackgroundHint = restoreBackground;
        this.renderToBackgroundHint = renderToBackground;
    }

    /**
     * The frame image.
     */
    public BufferedImage getImage() {
        return img;
    }

    /**
     * Indicates if the animation background should be restored before rendering this frame.
     */
    public boolean getRestoreBackgroundHint() {
        return restoreBackgroundHint;
    }

    /**
     * Indicates if the animation frame should be rendered to the animation background.
     */
    public boolean getRenderToBackgroundHint() {
        return renderToBackgroundHint;
    }

    /**
     * Indicates the intended duration for this frame.
     */
    public float getDurationHint() {
        return durationHint;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public AnimationFrame createAnimationFrame(Rectangle bounds, Disposer disposer) {
        var texture = DefaultTexture.create(img, disposer);
        var surface = Quad.create(bounds, imageCoordinates, disposer);
        return new AnimationFrame(surface, texture, durationHint, restoreBackgroundHint, renderToBackgroundHint);
    }
}
