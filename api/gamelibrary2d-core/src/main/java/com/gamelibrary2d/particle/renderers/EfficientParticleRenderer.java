package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.particle.systems.Particle;
import com.gamelibrary2d.renderers.AbstractArrayRenderer;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.util.PointSmoothing;

public class EfficientParticleRenderer extends AbstractArrayRenderer<OpenGLBuffer> implements ParticleRenderer {

    private final static String boundsUniformName = "bounds";

    private final static String texturedUniformName = "textured";
    private final float[] boundsArray = new float[4];
    private float pointSize = 1f;
    private ParticleShape particleShape = ParticleShape.RECTANGLE;
    private PointSmoothing pointSmoothing = PointSmoothing.FASTEST;
    private Texture texture;
    private Rectangle bounds;

    public EfficientParticleRenderer() {
        super(DrawMode.POINTS);
        setBounds(Rectangle.centered(16f, 16f));
    }

    public EfficientParticleRenderer(Texture texture) {
        this();
        setTexture(texture);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        boundsArray[0] = bounds.xMin();
        boundsArray[1] = bounds.yMin();
        boundsArray[2] = bounds.xMax();
        boundsArray[3] = bounds.yMax();
    }

    public PointSmoothing getPointSmoothing() {
        return pointSmoothing;
    }

    public void setPointSmoothing(PointSmoothing pointSmoothing) {
        this.pointSmoothing = pointSmoothing;
    }

    public ParticleShape getParticleShape() {
        return particleShape;
    }

    public void setParticleShape(ParticleShape particleShape) {
        this.particleShape = particleShape;
    }

    public float getPointSize() {
        return pointSize;
    }

    public void setPointSize(float pointSize) {
        this.pointSize = pointSize;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(Particle[] particles, OpenGLBuffer vertexBuffer, boolean gpuOutdated, int offset, int len,
                       float alpha) {
        if (gpuOutdated) {
            vertexBuffer.bind();
            vertexBuffer.updateGPU(offset, len);
        }

        super.render(alpha, vertexBuffer, offset, len);

        OpenGL.instance().glDisable(OpenGL.GL_POINT_SMOOTH);
        if (particleShape == ParticleShape.RECTANGLE && texture != null) {
            texture.unbind();
        }
    }

    @Override
    protected void renderPrepare(ShaderProgram shaderProgram) {
        if (particleShape == ParticleShape.RECTANGLE) {
            var glBoundsUniform = shaderProgram.getUniformLocation(boundsUniformName);
            OpenGL.instance().glUniform4fv(glBoundsUniform, boundsArray);

            var glTexturedUniform = shaderProgram.getUniformLocation(texturedUniformName);

            var texture = getTexture();
            if (texture != null) {
                texture.bind();
                OpenGL.instance().glUniform1f(glTexturedUniform, 1);
            } else {
                OpenGL.instance().glUniform1f(glTexturedUniform, 0);
            }
        } else {
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
    }

    @Override
    protected void renderCleanup() {

    }

    @Override
    protected ShaderProgram getShaderProgram() {
        switch (particleShape) {
            case POINT:
                return ShaderProgram.getPointParticleShaderProgram();
            case RECTANGLE:
                return ShaderProgram.getQuadParticleShaderProgram();
            default:
                throw new IllegalStateException("Unexpected value: " + particleShape);
        }
    }
}
