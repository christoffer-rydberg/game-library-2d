package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Color;

public class ShaderParameters {
    public final static int MIN_LENGTH = 6;
    public final static int MAX_LENGTH = 100;
    public final static int COLOR_R = 0;
    public final static int COLOR_G = 1;
    public final static int COLOR_B = 2;
    public final static int ALPHA = 3;
    public final static int TIME = 4;
    public final static int IS_TEXTURED = 5;

    private final float[] parameters;

    public ShaderParameters() {
        parameters = new float[MIN_LENGTH];
        parameters[COLOR_R] = 1;
        parameters[COLOR_G] = 1;
        parameters[COLOR_B] = 1;
        parameters[ALPHA] = 1;
    }

    public ShaderParameters(float[] parameters) {
        if (parameters == null) {
            throw new IllegalStateException("Parameters cannot be null");
        } else if (parameters.length < MIN_LENGTH) {
            throw new IllegalStateException("Must have at least " + MIN_LENGTH + " parameters.");
        } else if (parameters.length > MAX_LENGTH) {
            throw new IllegalStateException("Must have at most " + MAX_LENGTH + " parameters.");
        }

        this.parameters = parameters;
    }

    public float[] getArray() {
        return parameters;
    }

    public float get(int index) {
        return parameters[index];
    }

    public void set(int index, float value) {
        parameters[index] = value;
    }

    public void setRgba(Color color) {
        setRgba(color.getR(), color.getG(), color.getB(), color.getA());
    }

    public void setRgb(Color color) {
        setRgb(color.getR(), color.getG(), color.getB());
    }

    public void setRgb(float r, float g, float b) {
        parameters[COLOR_R] = r;
        parameters[COLOR_G] = g;
        parameters[COLOR_B] = b;
    }

    public void setRgba(float r, float g, float b, float a) {
        parameters[COLOR_R] = r;
        parameters[COLOR_G] = g;
        parameters[COLOR_B] = b;
        parameters[ALPHA] = a;
    }

    public int getLength() {
        return parameters.length;
    }
}
