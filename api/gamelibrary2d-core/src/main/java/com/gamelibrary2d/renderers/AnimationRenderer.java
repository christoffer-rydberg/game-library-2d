package com.gamelibrary2d.renderers;

import com.gamelibrary2d.animation.Animation;
import com.gamelibrary2d.animation.AnimationFrame;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.util.BlendMode;

public class AnimationRenderer extends AbstractRenderer {
    private final Disposer disposer;
    private Animation animation;
    private BackgroundBuffer backgroundBuffer;
    private boolean looping;
    private int previousFrameIndex = -1;
    private float globalFrameDuration = -1;

    public AnimationRenderer(Animation animation, boolean loop) {
        disposer = null;
        setAnimation(animation, loop);
    }

    public AnimationRenderer(Animation animation, boolean loop, Disposer disposer) {
        this.disposer = disposer;
        setAnimation(animation, loop);
    }

    public Animation getAnimation() {
        return animation;
    }

    private boolean requiresBackgroundBuffering() {
        for (var frame : animation.getFrames()) {
            if (frame.getRenderToBackgroundHint())
                return true;
        }
        return false;
    }

    private void setAnimation(Animation animation, boolean loop) {
        this.animation = animation;
        this.looping = loop;
        this.previousFrameIndex = -1;

        if (backgroundBuffer != null) {
            backgroundBuffer.dispose();
            backgroundBuffer = null;
        }

        if (requiresBackgroundBuffering()) {
            backgroundBuffer = new BackgroundBuffer(disposer);
        }
    }

    /**
     * The global frame duration is used to determine the duration of each frame.
     * It can be enabled by invoking {@link #setGlobalFrameDuration} and disabled by invoking {@link #disableGlobalFrameDuration}.
     * If disabled, the {@link AnimationFrame#getDurationHint() duration hint} for each frame will be respected.
     *
     * @return The global frame duration if enabled, otherwise -1.
     */
    public float getGlobalFrameDuration() {
        return this.globalFrameDuration;
    }

    /**
     * Sets the {@link #getGlobalFrameDuration() global frame duration}.
     */
    public void setGlobalFrameDuration(float duration) {
        if (duration <= 0f) {
            throw new IllegalStateException("The global frame duration must be greater than 0");
        }

        this.globalFrameDuration = duration;
    }

    /**
     * Disables the {@link #getGlobalFrameDuration() global frame duration}.
     */
    public void disableGlobalFrameDuration() {
        this.globalFrameDuration = -1;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    private int getFrameIndexFromAnimation(int previousFrame, float time) {
        if (previousFrame > 0) {
            // Try previous frame again:
            if (animation.isFrameActive(previousFrame, time)) {
                return previousFrame;
            }

            var nextFrame = looping
                    ? (previousFrame + 1) % animation.getFrames().size()
                    : previousFrame + 1;

            // Try next frame:
            if (animation.isFrameActive(nextFrame, time)) {
                return nextFrame;
            }
        }

        // Search for frame:
        return animation.getFrameIndex(time);
    }

    private int getFrameIndex(int previousIndex) {
        var time = getParameters().get(ShaderParameters.TIME);
        if (globalFrameDuration > 0f) {
            var size = animation.getFrames().size();
            var index = (int) (time / globalFrameDuration);
            return looping ? index % size : Math.min(index, size - 1);
        } else {
            if (looping) {
                var duration = animation.getDuration();
                var roundTrips = (int) (time / duration);
                var timeWithinAnimation = time - roundTrips * duration;
                return getFrameIndexFromAnimation(previousIndex, timeWithinAnimation);
            }

            return getFrameIndexFromAnimation(previousIndex, time);
        }
    }

    private int getFrameIndex() {
        previousFrameIndex = getFrameIndex(previousFrameIndex);
        return previousFrameIndex;
    }

    public AnimationFrame getFrame() {
        return animation.getFrame(getFrameIndex());
    }

    public boolean isAnimationFinished() {
        return !looping && getFrameIndex() == animation.getFrames().size() - 1;
    }

    @Override
    public Rectangle getBounds() {
        return animation.getBounds();
    }

    @Override
    protected void applyParameters(float alpha) {
        getParameters().set(ShaderParameters.IS_TEXTURED, 1);
        super.applyParameters(alpha);
    }

    @Override
    protected void onRender(ShaderProgram shaderProgram) {
        var frameIndex = getFrameIndex();

        var activeFrame = animation.getFrame(frameIndex);
        if (backgroundBuffer != null) {
            backgroundBuffer.render(shaderProgram, animation, frameIndex);

            if (!activeFrame.getRenderToBackgroundHint()) {
                render(shaderProgram, activeFrame);
            }
        } else {
            render(shaderProgram, activeFrame);
        }
    }

    private void render(ShaderProgram shaderProgram, AnimationFrame frame) {
        frame.getTexture().bind();
        frame.getSurface().render(shaderProgram);
    }

    private static class BackgroundBuffer {
        private final DefaultDisposer resourceDisposer;
        private FrameBuffer frameBuffer;
        private SurfaceRenderer frameBufferRenderer;
        private FrameRenderer[] frameRenderers;
        private int previousFrame;

        BackgroundBuffer(Disposer disposer) {
            resourceDisposer = new DefaultDisposer(disposer);
        }

        private static FrameRenderer[] createFrameRenderers(Animation animation) {
            var size = animation.getFrames().size();
            var frameRenderers = new FrameRenderer[size];
            for (int i = 0; i < size; ++i) {
                var frame = animation.getFrame(i);
                var frameSurface = frame.getSurface();
                var frameTexture = frame.getTexture();

                var animationBounds = animation.getBounds();
                var frameBounds = frameSurface.getBounds();
                var textureWidth = frameTexture.getWidth();
                var textureHeight = frameTexture.getHeight();

                var invertedScaleX = textureWidth / frameBounds.getWidth();
                var invertedScaleY = textureHeight / frameBounds.getHeight();

                frameRenderers[i] = new FrameRenderer(
                        frameSurface,
                        -animationBounds.getLowerX(),
                        -animationBounds.getLowerY(),
                        invertedScaleX,
                        invertedScaleY
                );
            }

            return frameRenderers;
        }

        private void render(ShaderProgram shaderProgram, Animation animation, int currentFrame) {
            if (frameBuffer == null) {
                var texture = DefaultTexture.create(animation.getOriginalWidth(), animation.getOriginalHeight(), resourceDisposer);
                var quad = Quad.create(animation.getBounds(), resourceDisposer);
                frameBuffer = FrameBuffer.create(texture, resourceDisposer);
                frameBufferRenderer = new SurfaceRenderer(quad, texture);
                frameRenderers = createFrameRenderers(animation);
            }

            // Prepare model matrix
            var modelMatrix = ModelMatrix.instance();
            modelMatrix.pushMatrix();
            modelMatrix.clearMatrix();

            float alpha = shaderProgram.getParameter(ShaderParameters.ALPHA);
            var blendMode = OpenGLUtils.getBlendMode();

            // Disable blend mode
            OpenGLUtils.setBlendMode(BlendMode.TRANSPARENT);

            // Render to background buffer
            renderToFrameBuffer(shaderProgram, animation, currentFrame);

            // Restore model matrix
            modelMatrix.popMatrix();

            // Render frame buffer texture to the default frame buffer
            frameBufferRenderer.setShaderProgram(shaderProgram);
            frameBufferRenderer.setBlendMode(blendMode);
            frameBufferRenderer.render(alpha);
        }

        private void renderToFrameBuffer(ShaderProgram shaderProgram, Animation animation, int currentFrame) {
            frameBuffer.bind();

            if (currentFrame < previousFrame) {
                frameBuffer.clear();
                previousFrame = 0;
            }

            for (int i = previousFrame; i <= currentFrame; ++i) {
                var frame = animation.getFrame(i);

                if (frame.getRestoreBackgroundHint()) {
                    frameBuffer.clear();
                }

                if (frame.getRenderToBackgroundHint()) {
                    if (shaderProgram.setParameter(ShaderParameters.ALPHA, 1f)) {
                        shaderProgram.applyParameters();
                    }
                    frame.getTexture().bind();

                    frameRenderers[i].render(shaderProgram);
                }
            }
            previousFrame = currentFrame;

            frameBuffer.unbind(true);
        }

        void dispose() {
            if (frameBuffer != null) {
                resourceDisposer.dispose();
                frameBuffer = null;
                frameBufferRenderer = null;
                frameRenderers = null;
            }
        }

        private static class FrameRenderer {
            private final Surface frameSurface;
            private final float translationX;
            private final float translationY;
            private final float scaleX;
            private final float scaleY;

            FrameRenderer(Surface frameSurface, float translationX, float translationY, float scaleX, float scaleY) {
                this.frameSurface = frameSurface;
                this.translationX = translationX;
                this.translationY = translationY;
                this.scaleX = scaleX;
                this.scaleY = scaleY;
            }

            void render(ShaderProgram shaderProgram) {
                var modelMatrix = ModelMatrix.instance();
                modelMatrix.pushMatrix();
                modelMatrix.scalef(scaleX, scaleY, 0f);
                modelMatrix.translatef(translationX, translationY, 0f);
                shaderProgram.updateModelMatrix(modelMatrix);
                frameSurface.render(shaderProgram);
                modelMatrix.popMatrix();
            }
        }
    }
}
