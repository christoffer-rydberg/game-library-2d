package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.PointSmoothing;
import com.gamelibrary2d.resources.PositionArray;

public class PointArrayRenderer extends AbstractArrayRenderer<PositionArray> {
    private float pointSize = 1f;
    private PointSmoothing pointSmoothing = PointSmoothing.FASTEST;

    public PointArrayRenderer() {

    }

    public PointArrayRenderer(float pointSize) {
        this.pointSize = pointSize;
    }

    public PointArrayRenderer(float pointSize, PointSmoothing pointSmoothing) {
        this.pointSize = pointSize;
        this.pointSmoothing = pointSmoothing;
    }

    public PointSmoothing getPointSmoothing() {
        return pointSmoothing;
    }

    public void setPointSmoothing(PointSmoothing pointSmoothing) {
        this.pointSmoothing = pointSmoothing;
    }

    public float getPointSize() {
        return pointSize;
    }

    public void setPointSize(float pointSize) {
        this.pointSize = pointSize;
    }

    @Override
    protected void renderPrepare(ShaderProgram shaderProgram) {
        OpenGL.instance().glPointSize(pointSize);
        switch (pointSmoothing) {
            case FASTEST:
                OpenGL.instance().glEnable(OpenGL.GL_POINT_SMOOTH);
                OpenGL.instance().glHint(OpenGL.GL_POINT_SMOOTH_HINT, OpenGL.GL_FASTEST);
                break;
            case NICEST:
                OpenGL.instance().glEnable(OpenGL.GL_POINT_SMOOTH);
                OpenGL.instance().glHint(OpenGL.GL_POINT_SMOOTH_HINT, OpenGL.GL_NICEST);
                break;
            case NONE:
                OpenGL.instance().glDisable(OpenGL.GL_POINT_SMOOTH);
                break;
            default:
                throw new GameLibrary2DRuntimeException("Argument out of range");
        }
    }

    @Override
    protected void renderCleanup() {
        OpenGL.instance().glPointSize(1f);
        OpenGL.instance().glDisable(OpenGL.GL_POINT_SMOOTH);
    }

    @Override
    protected ShaderProgram getShaderProgram() {
        return ShaderProgram.getPointShaderProgram();
    }
}
