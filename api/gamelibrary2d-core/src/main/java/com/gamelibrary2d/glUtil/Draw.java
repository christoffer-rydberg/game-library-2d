package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.util.BlendMode;

public class Draw {
    private static final float DEFAULT_R = 1f;
    private static final float DEFAULT_G = 1f;
    private static final float DEFAULT_B = 1f;
    private static final float DEFAULT_A = 1f;
    private static final BlendMode DEFAULT_BLEND_MODE = BlendMode.TRANSPARENT;

    private static final float[] parameters = {DEFAULT_R, DEFAULT_G, DEFAULT_B, DEFAULT_A};
    private static BlendMode blendMode = DEFAULT_BLEND_MODE;
    private static ShaderProgram shaderProgram = ShaderProgram.getDefaultShaderProgram();

    public static void setShaderProgram(ShaderProgram shaderProgram) {
        Draw.shaderProgram = shaderProgram;
    }

    public static void setBlendMode(BlendMode blendMode) {
        Draw.blendMode = blendMode;
    }

    public static void setColor(float r, float g, float b, float a) {
        parameters[0] = r;
        parameters[1] = g;
        parameters[2] = b;
        parameters[3] = a;
    }

    public static void setColor(float r, float g, float b) {
        setColor(r, g, b, 1f);
    }

    public static void setColor(Color color) {
        setColor(color.getR(), color.getG(), color.getB(), color.getA());
    }

    public static void setColor(Color color, float alpha) {
        setColor(color.getR(), color.getG(), color.getB(), alpha);
    }

    public static void reset() {
        setShaderProgram(ShaderProgram.getDefaultShaderProgram());
        setBlendMode(DEFAULT_BLEND_MODE);
        setColor(DEFAULT_R, DEFAULT_G, DEFAULT_B, DEFAULT_A);
    }

    public static void rectangle(float lowerX, float lowerY, float upperX, float upperY) {
        shaderProgram.bind();
        shaderProgram.setParameters(parameters, 0, parameters.length);
        shaderProgram.applyParameters();

        OpenGLUtils.setBlendMode(blendMode);

        var openGl = OpenGL.instance();
        openGl.glBegin(OpenGL.GL_QUADS);
        openGl.glVertex2f(lowerX, lowerY);
        openGl.glVertex2f(upperX, lowerY);
        openGl.glVertex2f(upperX, upperY);
        openGl.glVertex2f(lowerX, upperY);
        openGl.glEnd();
    }

    public static void rectangle(Rectangle rectangle) {
        rectangle(
                rectangle.getLowerX(),
                rectangle.getLowerY(),
                rectangle.getUpperX(),
                rectangle.getUpperY());
    }
}
