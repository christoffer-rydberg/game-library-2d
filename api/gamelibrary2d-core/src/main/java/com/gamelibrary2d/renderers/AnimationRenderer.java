package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.glUtil.*;
import com.gamelibrary2d.resources.*;
import com.gamelibrary2d.util.BlendMode;

public class AnimationRenderer extends AbstractRenderer {
    private final Disposer disposer;
    private Animation animation;
    private AnimationBackgroundBuffer backgroundBuffer;
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
        for (AnimationFrame frame : animation.getFrames()) {
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
            backgroundBuffer = new AnimationBackgroundBuffer(animation, disposer);
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

        setGlobalFrameDurationInternal(duration);
    }

    /**
     * Disables the {@link #getGlobalFrameDuration() global frame duration}.
     */
    public void disableGlobalFrameDuration() {
        setGlobalFrameDurationInternal(-1);
    }

    private void setGlobalFrameDurationInternal(float globalFrameDuration) {
        if (this.globalFrameDuration != globalFrameDuration) {
            this.globalFrameDuration = globalFrameDuration;
            this.previousFrameIndex = -1;
        }
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

            int nextFrame = looping
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
        float time = getParameters().get(ShaderParameters.TIME);
        if (globalFrameDuration > 0f) {
            int size = animation.getFrames().size();
            int index = (int) (time / globalFrameDuration);
            return looping ? index % size : Math.min(index, size - 1);
        } else {
            if (looping) {
                float duration = animation.getDuration();
                int roundTrips = (int) (time / duration);
                float timeWithinAnimation = time - roundTrips * duration;
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
        int frameIndex = getFrameIndex();

        AnimationFrame activeFrame = animation.getFrame(frameIndex);
        if (backgroundBuffer != null) {
            backgroundBuffer.render(shaderProgram, frameIndex);

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

    private static class AnimationBackgroundBuffer {
        private final Animation animation;
        private final DefaultDisposer resourceDisposer;
        private FrameBuffer frameBuffer;
        private SurfaceRenderer frameBufferRenderer;
        private FrameRenderer[] frameRenderers;
        private int previousFrame;

        AnimationBackgroundBuffer(Animation animation, Disposer disposer) {
            this.animation = animation;
            resourceDisposer = new DefaultDisposer(disposer);
        }

        private FrameRenderer[] createFrameRenderers() {
            int size = animation.getFrames().size();
            FrameRenderer[] frameRenderers = new FrameRenderer[size];
            for (int i = 0; i < size; ++i) {
                AnimationFrame frame = animation.getFrame(i);
                Surface frameSurface = frame.getSurface();
                Texture frameTexture = frame.getTexture();

                Rectangle animationBounds = animation.getBounds();
                Rectangle frameBounds = frameSurface.getBounds();
                float textureWidth = frameTexture.getWidth();
                float textureHeight = frameTexture.getHeight();

                float invertedScaleX = textureWidth / frameBounds.getWidth();
                float invertedScaleY = textureHeight / frameBounds.getHeight();

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

        private Texture createBackgroundTexture(Animation animation) {
            float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
            float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
            for (AnimationFrame frame : animation.getFrames()) {
                Rectangle bounds = frame.getBounds();

                Texture texture = frame.getTexture();
                float width = texture.getWidth();
                float height = texture.getHeight();

                Rectangle imageBounds = bounds.resize(width / bounds.getWidth(), height / bounds.getHeight());
                xMin = Math.min(xMin, imageBounds.getLowerX());
                yMin = Math.min(yMin, imageBounds.getLowerY());
                xMax = Math.max(xMax, imageBounds.getUpperX());
                yMax = Math.max(yMax, imageBounds.getUpperY());
            }

            return DefaultTexture.create(
                    Math.round(xMax - xMin),
                    Math.round(yMax - yMin),
                    resourceDisposer);
        }

        private void render(ShaderProgram shaderProgram, int currentFrame) {
            if (frameBuffer == null) {
                // Create OpenGL resources when rendering for the first time
                Texture texture = createBackgroundTexture(animation);
                frameBuffer = DefaultFrameBuffer.create(texture, resourceDisposer);
                frameBufferRenderer = new SurfaceRenderer<>(
                        Quad.create(animation.getBounds(), resourceDisposer),
                        texture);
                frameRenderers = createFrameRenderers();
            }

            // Prepare model matrix
            ModelMatrix modelMatrix = ModelMatrix.instance();
            modelMatrix.pushMatrix();
            modelMatrix.clearMatrix();

            float alpha = shaderProgram.getParameter(ShaderParameters.ALPHA);
            BlendMode blendMode = OpenGLUtils.getBlendMode();

            // Disable blend mode
            OpenGLUtils.setBlendMode(BlendMode.TRANSPARENT);

            // Render to background buffer
            renderToFrameBuffer(shaderProgram, currentFrame);

            // Restore model matrix
            modelMatrix.popMatrix();

            // Render frame buffer texture to the default frame buffer
            frameBufferRenderer.setShaderProgram(shaderProgram);
            frameBufferRenderer.setBlendMode(blendMode);
            frameBufferRenderer.render(alpha);
        }

        private void renderToFrameBuffer(ShaderProgram shaderProgram, int currentFrame) {
            int previousFbo = frameBuffer.bind();
            try {
                if (currentFrame < previousFrame) {
                    frameBuffer.clear();
                    previousFrame = 0;
                }

                for (int i = previousFrame; i <= currentFrame; ++i) {
                    AnimationFrame frame = animation.getFrame(i);

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
            } finally {
                OpenGLState.bindFrameBuffer(previousFbo);
            }
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
                ModelMatrix modelMatrix = ModelMatrix.instance();
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
