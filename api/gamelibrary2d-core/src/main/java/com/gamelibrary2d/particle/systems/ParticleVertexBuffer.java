package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.FloatTransferBuffer;
import com.gamelibrary2d.resources.AbstractVertexArray;

class ParticleVertexBuffer extends AbstractVertexArray {

    final static int STRIDE = 12;
    private final static int POS_X = 0;
    private final static int POS_Y = 1;
    private final static int POS_Z = 2;
    private final static int ROTATION = 3;
    private final static int COLOR_R = 4;
    private final static int COLOR_G = 5;
    private final static int COLOR_B = 6;
    private final static int COLOR_A = 7;
    private final static int SCALE_X = 8;
    private final static int SCALE_Y = 9;
    private final static int TIME = 10;
    private final float[] internalState;

    private ParticleVertexBuffer(float[] internalState, Disposer disposer) {
        super(new FloatTransferBuffer(internalState, STRIDE, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer));
        this.internalState = internalState;
    }

    static ParticleVertexBuffer create(float[] array, Disposer disposer) {
        ParticleVertexBuffer buffer = new ParticleVertexBuffer(array, disposer);
        disposer.register(buffer);
        return buffer;
    }

    float getPosX(int offset) {
        return internalState[offset + POS_X];
    }

    void setPosX(int offset, float value) {
        internalState[offset + POS_X] = value;
    }

    float getPosY(int offset) {
        return internalState[offset + POS_Y];
    }

    void setPosY(int offset, float value) {
        internalState[offset + POS_Y] = value;
    }

    float getPosZ(int offset) {
        return internalState[offset + POS_Z];
    }

    void setPosZ(int offset, float value) {
        internalState[offset + POS_Z] = value;
    }

    float getRotation(int offset) {
        return internalState[offset + ROTATION];
    }

    void setRotation(int offset, float value) {
        internalState[offset + ROTATION] = value;
    }

    float getColorR(int offset) {
        return internalState[offset + COLOR_R];
    }

    void setColorR(int offset, float value) {
        internalState[offset + COLOR_R] = value;
    }

    float getColorG(int offset) {
        return internalState[offset + COLOR_G];
    }

    void setColorG(int offset, float value) {
        internalState[offset + COLOR_G] = value;
    }

    float getColorB(int offset) {
        return internalState[offset + COLOR_B];
    }

    void setColorB(int offset, float value) {
        internalState[offset + COLOR_B] = value;
    }

    float getColorA(int offset) {
        return internalState[offset + COLOR_A];
    }

    void setColorA(int offset, float value) {
        internalState[offset + COLOR_A] = value;
    }

    float getScaleX(int offset) {
        return internalState[offset + SCALE_X];
    }

    void setScaleX(int offset, float value) {
        internalState[offset + SCALE_X] = value;
    }

    float getScaleY(int offset) {
        return internalState[offset + SCALE_Y];
    }

    void setScaleY(int offset, float value) {
        internalState[offset + SCALE_Y] = value;
    }

    float getTime(int offset) {
        return internalState[offset + TIME];
    }

    void setTime(int offset, float value) {
        internalState[offset + TIME] = value;
    }
}