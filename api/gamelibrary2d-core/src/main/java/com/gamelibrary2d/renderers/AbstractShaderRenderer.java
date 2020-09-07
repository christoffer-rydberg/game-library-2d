package com.gamelibrary2d.renderers;

import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.BlendMode;

public abstract class AbstractShaderRenderer extends AbstractRenderer {

    private BlendMode blendMode;
    private ShaderProgram shaderProgram;

    protected AbstractShaderRenderer() {
        blendMode = BlendMode.TRANSPARENCY;
        shaderProgram = ShaderProgram.getDefaultShaderProgram();
    }

    protected AbstractShaderRenderer(RenderingParameters parameters) {
        super(parameters);
        blendMode = BlendMode.TRANSPARENCY;
        shaderProgram = ShaderProgram.getDefaultShaderProgram();
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram != null ? shaderProgram : ShaderProgram.getDefaultShaderProgram();
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    @Override
    public void render(float alpha) {
        ShaderProgram shaderProgram = getShaderProgram();
        shaderProgram.bind();
        shaderProgram.updateModelMatrix(ModelMatrix.instance());
        OpenGLUtils.applyBlendMode(blendMode);
        applyParameters(alpha);
        onRender(shaderProgram);
    }

    protected void applyParameters(float alpha) {
        var parameters = getParameters();
        float alphaSetting = parameters.get(RenderingParameters.ALPHA);

        try {
            parameters.set(RenderingParameters.ALPHA, alphaSetting * alpha);
            shaderProgram.setParameters(parameters.getArray(), 0, parameters.getLength());
            shaderProgram.applyParameters();
        } finally {
            parameters.set(RenderingParameters.ALPHA, alphaSetting);
        }
    }

    protected abstract void onRender(ShaderProgram shaderProgram);
}
