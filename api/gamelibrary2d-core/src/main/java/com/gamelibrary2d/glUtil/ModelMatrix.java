package com.gamelibrary2d.glUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelMatrix {

    private static ModelMatrix instance;

    private int stackPointer = -1;
    private List<float[]> bufferStack = new ArrayList<>();
    private MatrixBuffer matrixbuffer = new MatrixBuffer();

    private ModelMatrix() {
        instance = this;
    }

    public static ModelMatrix instance() {
        return instance != null ? instance : new ModelMatrix();
    }

    public void clearMatrix() {
        matrixbuffer.clear();
    }

    public void pushMatrix() {
        ++stackPointer;
        float[] stored;
        if (stackPointer == bufferStack.size()) {
            stored = new float[16];
            bufferStack.add(stored);
        } else {
            stored = bufferStack.get(stackPointer);
        }
        matrixbuffer.serialize(stored);
    }

    public void popMatrix() {
        if (stackPointer == -1) return;
        float[] stored = bufferStack.get(stackPointer);
        matrixbuffer.deserialize(stored);
        --stackPointer;
    }

    public void translatef(float x, float y, float z) {
        matrixbuffer.translate(x, y, z);
    }

    public void scalef(float x, float y, float z) {
        if (x != 1 || y != 1 || z != 1) {
            matrixbuffer.scale(x, y, z);
        }
    }

    /**
     * Rotation is clockwise in degrees around the specified axis.
     */
    public void rotatef(float angle, float x, float y, float z) {
        if (angle != 0) {
            if (z == 1) {
                matrixbuffer.rotateZ(angle);
            } else {
                matrixbuffer.rotate(angle, x, y, z);
            }
        }
    }

    public FloatBuffer getFloatBuffer() {
        return matrixbuffer.getFloatBuffer();
    }
}