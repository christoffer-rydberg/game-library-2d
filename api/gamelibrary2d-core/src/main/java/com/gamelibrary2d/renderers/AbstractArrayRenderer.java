package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.util.BlendMode;

public abstract class AbstractArrayRenderer<T extends OpenGLBuffer> implements ArrayRenderer<T> {
    private final static String alphaUniformName = "alpha";
    private final static String colorUniformName = "colorFactor";
    private final float[] colorArray = new float[4];
    private final DrawMode drawMode;

    private Color color;
    private boolean maskingOutBackground = false;
    private BlendMode blendMode = BlendMode.ADDITIVE;

    protected AbstractArrayRenderer(DrawMode drawMode) {
        this.drawMode = drawMode;
        setColor(Color.WHITE);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        colorArray[0] = color.getR();
        colorArray[1] = color.getG();
        colorArray[2] = color.getB();
        colorArray[3] = color.getA();
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
    public void render(float alpha, T array, int offset, int len) {
        ShaderProgram shaderProgram = getShaderProgram();
        shaderProgram.bind();
        shaderProgram.updateModelMatrix(ModelMatrix.instance());

        int glUniformAlpha = shaderProgram.getUniformLocation(alphaUniformName);
        OpenGL.instance().glUniform1f(glUniformAlpha, alpha);

        int glUniformColor = shaderProgram.getUniformLocation(colorUniformName);
        OpenGL.instance().glUniform4fv(glUniformColor, colorArray);

        renderPrepare(shaderProgram);

        array.bind();

        var drawMode = getOpenGlDrawMode();
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

    protected abstract ShaderProgram getShaderProgram();

    protected abstract void renderPrepare(ShaderProgram shaderProgram);

    protected abstract void renderCleanup();

    protected enum DrawMode {
        POINTS,
        LINE
    }
}