package com.gamelibrary2d.animation;

import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;

public class AnimationFrame {
    private final Surface surface;
    private final Texture texture;
    private final boolean restoreBackgroundHint;
    private final boolean renderToBackgroundHint;
    private final float durationHint;

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param surface The frame surface.
     * @param texture The frame texture.
     */
    public AnimationFrame(Quad surface, Texture texture) {
        this(surface, texture, 0, false, false);
    }

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param surface  The frame surface.
     * @param texture  The frame texture.
     * @param duration Sets the {@link AnimationFrame#getDurationHint} field.
     */
    public AnimationFrame(Quad surface, Texture texture, float duration) {
        this(surface, texture, duration, false, false);
    }

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param surface            The frame surface.
     * @param texture            The frame texture.
     * @param duration           Sets the {@link AnimationFrame#getDurationHint} field.
     * @param restoreBackground  Sets the {@link AnimationFrame#restoreBackgroundHint} field.
     * @param renderToBackground Sets the {@link AnimationFrame#getRenderToBackgroundHint} field.
     */
    public AnimationFrame(Quad surface, Texture texture, float duration, boolean restoreBackground, boolean renderToBackground) {
        this.surface = surface;
        this.texture = texture;
        this.durationHint = duration;
        this.restoreBackgroundHint = restoreBackground;
        this.renderToBackgroundHint = renderToBackground;
    }

    /**
     * The frame surface.
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * The frame texture.
     */
    public Texture getTexture() {
        return texture;
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
}
