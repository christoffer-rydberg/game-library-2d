package com.gamelibrary2d.renderers;

import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.PositionBuffer;

public class LineRenderer extends AbstractArrayRenderer<PositionBuffer> {
    private float lineWidth = 1f;

    public LineRenderer() {
        super(DrawMode.LINE);
    }

    public LineRenderer(float lineWidth) {
        this();
        this.lineWidth = lineWidth;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    protected void renderPrepare(ShaderProgram shaderProgram) {
        OpenGL.instance().glLineWidth(lineWidth);
    }

    @Override
    protected void renderCleanup() {
        OpenGL.instance().glLineWidth(1f);
    }

    @Override
    protected ShaderProgram getShaderProgram() {
        return ShaderProgram.getPointShaderProgram();
    }
}
