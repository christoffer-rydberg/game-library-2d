package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.BlendMode;
import com.gamelibrary2d.resources.VertexArray;

public abstract class AbstractArrayRenderer<T extends VertexArray> implements ArrayRenderer<T> {
    private final static String alphaUniformName = "alpha";
    private final static String colorUniformName = "colorFactor";
    private final float[] colorArray = new float[4];

    private Color color;
    private boolean maskingOutBackground = false;
    private BlendMode blendMode = BlendMode.ADDITIVE;

    protected AbstractArrayRenderer() {
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

    @Override
    public void render(float alpha, T array) {
        render(alpha, array, 0, array.getCapacity());
    }

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

        if (blendMode != BlendMode.NONE && isMaskingOutBackground()) {
            OpenGLUtils.applyBlendMode(BlendMode.MASK);
            OpenGL.instance().glDrawArrays(OpenGL.GL_POINTS, offset, len);
        }

        OpenGLUtils.applyBlendMode(blendMode);
        OpenGL.instance().glDrawArrays(OpenGL.GL_POINTS, offset, len);

        // Cleanup
        array.unbind();
        OpenGLUtils.applyBlendMode(BlendMode.NONE);
    }

    protected abstract ShaderProgram getShaderProgram();

    protected abstract void renderPrepare(ShaderProgram shaderProgram);

    protected abstract void renderCleanup();

}