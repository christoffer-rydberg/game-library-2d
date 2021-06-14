package com.gamelibrary2d.renderers;

import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.resources.BlendMode;

public abstract class AbstractArrayRenderer<T extends OpenGLBuffer> implements ArrayRenderer<T> {
    private final DrawMode drawMode;
    private final ShaderParameters parameters;
    private boolean maskingOutBackground = false;
    private BlendMode blendMode = BlendMode.ADDITIVE;

    protected AbstractArrayRenderer(DrawMode drawMode) {
        this.drawMode = drawMode;
        parameters = new ShaderParameters();
    }

    public ShaderParameters getParameters() {
        return parameters;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    public boolean isMaskingOutBackground() {
        return maskingOutBackground;
    }

    /**
     * Setting this property to true will ensure that the color of the background
     * does not get blended into the rendered vertices. This is achieved by masking
     * out the background, which requires an extra render operation.
     *
     * @param maskingOutBackground True to mask out the background, false otherwise.
     */
    public void setMaskingOutBackground(boolean maskingOutBackground) {
        this.maskingOutBackground = maskingOutBackground;
    }

    private int getOpenGlDrawMode() {
        switch (drawMode) {
            case POINTS:
                return OpenGL.GL_POINTS;
            case LINE:
                return OpenGL.GL_LINE_STRIP;
            default:
                throw new IllegalStateException("Unexpected value: " + drawMode);
        }
    }

    @Override
    public void render(float alpha, OpenGLBuffer array, int offset, int len) {
        ShaderProgram shaderProgram = getShaderProgram();
        shaderProgram.bind();
        shaderProgram.updateModelMatrix(ModelMatrix.instance());

        renderPrepare(shaderProgram);

        array.bind();

        applyParameters(alpha);

        int drawMode = getOpenGlDrawMode();
        if (blendMode != BlendMode.NONE && isMaskingOutBackground()) {
            OpenGLUtils.setBlendMode(BlendMode.MASKED);
            OpenGL.instance().glDrawArrays(drawMode, offset, len);
        }

        OpenGLUtils.setBlendMode(blendMode);

        OpenGL.instance().glDrawArrays(drawMode, offset, len);

        // Cleanup
        renderCleanup();
        array.unbind();
        OpenGLUtils.setBlendMode(BlendMode.NONE);
    }

    protected void applyParameters(float alpha) {
        float alphaSetting = parameters.get(ShaderParameters.ALPHA);

        try {
            ShaderProgram program = getShaderProgram();
            parameters.set(ShaderParameters.ALPHA, alphaSetting * alpha);
            program.setParameters(parameters.getArray(), 0, parameters.getLength());
            program.applyParameters();
        } finally {
            parameters.set(ShaderParameters.ALPHA, alphaSetting);
        }
    }

    protected abstract ShaderProgram getShaderProgram();

    protected abstract void renderPrepare(ShaderProgram shaderProgram);

    protected abstract void renderCleanup();

    protected enum DrawMode {
        POINTS,
        LINE
    }
}