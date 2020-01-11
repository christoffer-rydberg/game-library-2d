package com.gamelibrary2d.animation;

import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

public class AnimationFrame {
    private final Quad quad;
    private final Texture texture;
    private final boolean restoreBackgroundHint;
    private final boolean renderToBackgroundHint;
    private final float durationHint;

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param quad    The frame quad.
     * @param texture The frame texture.
     */
    public AnimationFrame(Quad quad, Texture texture) {
        this(quad, texture, 0, false, false);
    }

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param quad     The frame quad.
     * @param texture  The frame texture.
     * @param duration Sets the {@link AnimationFrame#getDurationHint} field.
     */
    public AnimationFrame(Quad quad, Texture texture, float duration) {
        this(quad, texture, duration, false, false);
    }

    /**
     * Creates a new instance of {@link AnimationFrame}.
     *
     * @param quad               The frame quad.
     * @param texture            The frame texture.
     * @param duration           Sets the {@link AnimationFrame#getDurationHint} field.
     * @param restoreBackground  Sets the {@link AnimationFrame#restoreBackgroundHint} field.
     * @param renderToBackground Sets the {@link AnimationFrame#getRenderToBackgroundHint} field.
     */
    public AnimationFrame(Quad quad, Texture texture, float duration, boolean restoreBackground, boolean renderToBackground) {
        this.quad = quad;
        this.texture = texture;
        this.durationHint = duration;
        this.restoreBackgroundHint = restoreBackground;
        this.renderToBackgroundHint = renderToBackground;
    }

    /**
     * The frame quad.
     */
    public Quad getQuad() {
        return quad;
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
