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
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
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
        var activeIndex = getCurrentFrameIndex();
        if (activeIndex < 0)
            return;

        var activeFrame = animation.getFrame(activeIndex);
        if (backgroundBuffer != null) {
            backgroundBuffer.render(shaderProgram, animation, activeIndex);

            if (!activeFrame.getRenderToBackgroundHint()) {
                render(shaderProgram, activeFrame);
            }
        } else {
            render(shaderProgram, activeFrame);
        }
    }

    private void render(ShaderProgram shaderProgram, AnimationFrame frame) {
        frame.getTexture().bind();
        frame.getQuad().render(shaderProgram);
    }

    private static class BackgroundBuffer {
        private final DefaultDisposer resourceDisposer;
        private FrameBuffer frameBuffer;
        private SurfaceRenderer frameBufferRenderer;
        private Quad[] backgroundQuads;
        private int previousFrame;

        BackgroundBuffer(Disposer disposer) {
            resourceDisposer = new DefaultDisposer(disposer);
        }

        private static Quad[] createBackgroundQuads(Animation animation, Disposer disposer) {
            var quads = new Quad[animation.getFrameCount()];
            for (int i = 0; i < animation.getFrameCount(); ++i) {
                var frame = animation.getFrame(i);

                var quad = frame.getQuad();
                var bounds = quad.getBounds();
                var scaledWidth = bounds.width();
                var scaledHeight = bounds.height();

                var width = frame.getTexture().getImageWidth();
                var height = frame.getTexture().getImageHeight();
                var offsetX = Math.round((width / scaledWidth) * (bounds.xMin() - animation.getBounds().xMin()));
                var offsetY = Math.round((height / scaledHeight) * (bounds.yMin() - animation.getBounds().yMin()));

                var fullSizeBounds = new Rectangle(offsetX, offsetY, width + offsetX, height + offsetY);
                quads[i] = Quad.create(fullSizeBounds, disposer);
            }
            return quads;
        }

        private void render(ShaderProgram shaderProgram, Animation animation, int activeIndex) {
            if (frameBuffer == null) {
                var texture = Texture.create(animation.getOriginalWidth(), animation.getOriginalHeight(), resourceDisposer);
                var quad = Quad.create(animation.getBounds(), resourceDisposer);
                frameBuffer = FrameBuffer.create(texture, resourceDisposer);
                frameBufferRenderer = new SurfaceRenderer(quad, texture);
                backgroundQuads = createBackgroundQuads(animation, resourceDisposer);
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
            shaderProgram.updateModelMatrix(ModelMatrix.instance());
            renderToFrameBuffer(shaderProgram, animation, activeIndex);

            // Restore model matrix
            modelMatrix.popMatrix();

            // Render frame buffer texture to the default frame buffer
            frameBufferRenderer.setShaderProgram(shaderProgram);
            frameBufferRenderer.setBlendMode(blendMode);
            frameBufferRenderer.render(alpha);
        }

        private void renderToFrameBuffer(ShaderProgram shaderProgram, Animation animation, int activeIndex) {
            frameBuffer.bind();

            if (activeIndex < previousFrame) {
                frameBuffer.clear();
                previousFrame = 0;
            }

            for (int i = previousFrame; i <= activeIndex; ++i) {
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
                    backgroundQuads[i].render(shaderProgram);
                }
            }
            previousFrame = activeIndex;

            frameBuffer.unbind(true);
        }

        void dispose() {
            if (frameBuffer != null) {
                resourceDisposer.dispose();
                frameBuffer = null;
                frameBufferRenderer = null;
                backgroundQuads = null;
            }
        }
    }
}
