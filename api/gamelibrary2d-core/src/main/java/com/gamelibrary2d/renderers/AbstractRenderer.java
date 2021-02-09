package com.gamelibrary2d.renderers;

import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.BlendMode;

public abstract class AbstractRenderer implements Renderer {
    private final ShaderParameters parameters;
    private BlendMode blendMode;
    private ShaderProgram shaderProgram;

    protected AbstractRenderer() {
        parameters = new ShaderParameters();
        blendMode = BlendMode.TRANSPARENT;
        shaderProgram = ShaderProgram.getDefaultShaderProgram();
    }

    protected AbstractRenderer(ShaderParameters parameters) {
        this.parameters = parameters;
        blendMode = BlendMode.TRANSPARENT;
        shaderProgram = ShaderProgram.getDefaultShaderProgram();
    }

    @Override
    public ShaderParameters getParameters() {
        return parameters;
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
        applyParameters(alpha);
        OpenGLUtils.setBlendMode(blendMode);
        onRender(shaderProgram);
    }

    protected void applyParameters(float alpha) {
        ShaderParameters parameters = getParameters();
        float alphaSetting = parameters.get(ShaderParameters.ALPHA);

        try {
            parameters.set(ShaderParameters.ALPHA, alphaSetting * alpha);
            shaderProgram.setParameters(parameters.getArray(), 0, parameters.getLength());
            shaderProgram.applyParameters();
        } finally {
            parameters.set(ShaderParameters.ALPHA, alphaSetting);
        }
    }

    protected abstract void onRender(ShaderProgram shaderProgram);
}
