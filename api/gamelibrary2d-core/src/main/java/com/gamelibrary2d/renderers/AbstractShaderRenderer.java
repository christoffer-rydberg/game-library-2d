package com.gamelibrary2d.renderers;

import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLUtils;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.rendering.BlendMode;
import com.gamelibrary2d.rendering.RenderSettings;

public abstract class AbstractShaderRenderer extends AbstractRenderer {

    private BlendMode blendMode;
    private ShaderProgram shaderProgram;

    protected AbstractShaderRenderer() {
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
    protected void onRender(float alpha, float[] settings, int settingsSize) {
        ShaderProgram shaderProgram = getShaderProgram();
        shaderProgram.bind();
        shaderProgram.updateModelMatrix(ModelMatrix.instance());
        OpenGLUtils.applyBlendMode(blendMode);
        applySettings(alpha, settings, settingsSize);
        onRender(shaderProgram);
    }

    protected void applySettings(float alpha, float settings[], int settingsSize) {
        float alphaSetting = settings[RenderSettings.ALPHA];
        settings[RenderSettings.ALPHA] = alphaSetting * alpha;
        shaderProgram.updateSettings(settings, 0, settingsSize);
        shaderProgram.applySettings();
        settings[RenderSettings.ALPHA] = alphaSetting;
    }

    protected abstract void onRender(ShaderProgram shaderProgram);
}
