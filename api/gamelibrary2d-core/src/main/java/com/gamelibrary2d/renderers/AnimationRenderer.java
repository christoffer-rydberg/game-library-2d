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
    private boolean looping;
    private float frameDuration; // TODO: Respect individual frame duration
    private BackgroundBuffer backgroundBuffer;

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

    private float calcAverageFrameDuration() {
        float duration = 0;
        for (var frame : animation.getFrames()) {
            duration += frame.getDurationHint();
        }
        return duration / animation.getFrameCount();
    }

    private void setAnimation(Animation animation, boolean loop) {
        this.animation = animation;
        this.looping = loop;
        this.frameDuration = calcAverageFrameDuration();

        if (backgroundBuffer != null) {
            backgroundBuffer.dispose();
            backgroundBuffer = null;
        }

        if (requiresBackgroundBuffering()) {
            backgroundBuffer = new BackgroundBuffer(disposer);
        }
    }

    /**
     * The duration of each frame in seconds.
     */
    public float getFrameDuration() {
        return frameDuration;
    }

    /**
     * Sets the {@link #getFrameDuration frame duration}.
     *
     * @param duration The duration of each frame in seconds.
     */
    public void setFrameDuration(float duration) {
        this.frameDuration = duration;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public int getCurrentFrameIndex() {
        var size = animation.getFrameCount();
        if (size == 0)
            return -1;
        var index = getIndex();
        return looping ? index % size : Math.min(index, size - 1);
    }

    public AnimationFrame getCurrentFrame() {
        int index = getCurrentFrameIndex();
        return index < 0 ? null : animation.getFrame(index);
    }

    public boolean isAnimationFinished() {
        return !looping && getIndex() >= animation.getFrameCount();
    }

    private int getIndex() {
        var time = getParameters().get(ShaderParameters.TIME);
        return (int) (time / frameDuration);
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
        var currentFrame = getCurrentFrameIndex();
        if (currentFrame < 0)
            return;

        var activeFrame = animation.getFrame(currentFrame);
        if (backgroundBuffer != null) {
            backgroundBuffer.render(shaderProgram, animation, currentFrame);

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
            var frameRenderers = new FrameRenderer[animation.getFrameCount()];
            for (int i = 0; i < animation.getFrameCount(); ++i) {
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
                    // Make opaque. Alpha will be applied when rendered to default frame buffer.
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
