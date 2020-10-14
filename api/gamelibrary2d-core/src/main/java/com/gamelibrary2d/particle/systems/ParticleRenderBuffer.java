package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.AbstractMirroredVertexArrayBuffer;
import com.gamelibrary2d.glUtil.MirroredFloatBuffer;

import java.util.Arrays;

public class ParticleRenderBuffer extends AbstractMirroredVertexArrayBuffer<MirroredFloatBuffer> {

    public final static int STRIDE = 8;

    private final static int POS_X = 0;
    private final static int POS_Y = 1;
    private final static int SCALE = 2;
    private final static int ROTATION = 3;

    private final static int COLOR_R = 4;
    private final static int COLOR_G = 5;
    private final static int COLOR_B = 6;
    private final static int COLOR_A = 7;

    private float[] internalState;

    private ParticleRenderBuffer(MirroredFloatBuffer internalState) {
        super(internalState, STRIDE, 4);
        this.internalState = internalState.data();
    }

    static ParticleRenderBuffer create(int capacity, Disposer disposer) {
        var data = new float[capacity * STRIDE];
        var buffer = new ParticleRenderBuffer(
                MirroredFloatBuffer.create(data, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer));
        disposer.registerDisposal(buffer);
        return buffer;
    }

    public float getPosX(int offset) {
        return internalState[offset + POS_X];
    }

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = internalState.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = oldCapacity * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            internalState = Arrays.copyOf(internalState, newCapacity);
            getBuffer().allocate(internalState);
        }
    }

    public void setPosX(int offset, float value) {
        internalState[offset + POS_X] = value;
    }

    public float getPosY(int offset) {
        return internalState[offset + POS_Y];
    }

    public void setPosY(int offset, float value) {
        internalState[offset + POS_Y] = value;
    }

    public float getRotation(int offset) {
        return internalState[offset + ROTATION];
    }

    public void setRotation(int offset, float value) {
        internalState[offset + ROTATION] = value;
    }

    public float getColorR(int offset) {
        return internalState[offset + COLOR_R];
    }

    public void setColorR(int offset, float value) {
        internalState[offset + COLOR_R] = value;
    }

    public float getColorG(int offset) {
        return internalState[offset + COLOR_G];
    }

    public void setColorG(int offset, float value) {
        internalState[offset + COLOR_G] = value;
    }

    public float getColorB(int offset) {
        return internalState[offset + COLOR_B];
    }

    public void setColorB(int offset, float value) {
        internalState[offset + COLOR_B] = value;
    }

    public float getColorA(int offset) {
        return internalState[offset + COLOR_A];
    }

    public void setColorA(int offset, float value) {
        internalState[offset + COLOR_A] = value;
    }

    public float getScale(int offset) {
        return internalState[offset + SCALE];
    }

    public void setScale(int offset, float value) {
        internalState[offset + SCALE] = value;
    }
}