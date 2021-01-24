package com.gamelibrary2d.imaging;

import com.gamelibrary2d.common.Rectangle;

public class ImageAnimationFrame {
    private final Image img;
    private final Rectangle imageCoordinates;
    private final float offsetX;
    private final float offsetY;
    private final float durationHint;
    private final boolean restoreBackgroundHint;
    private final boolean renderToBackgroundHint;

    /**
     * Creates a new instance of {@link ImageAnimationFrame}.
     *
     * @param img                The frame image.
     * @param imageCoordinates   The image coordinates used for this frame.
     * @param offsetX            The offset along the X axis.
     * @param offsetY            The offset along the Y axis.
     * @param duration           Sets the {@link ImageAnimationFrame#getDurationHint} field.
     * @param restoreBackground  Sets the {@link ImageAnimationFrame#restoreBackgroundHint} field.
     * @param renderToBackground Sets the {@link ImageAnimationFrame#getRenderToBackgroundHint} field.
     */
    public ImageAnimationFrame(
            Image img,
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
    public Image getImage() {
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

    public Rectangle getImageCoordinates() {
        return imageCoordinates;
    }
}
