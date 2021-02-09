package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.AbstractInterleavedBuffer;
import com.gamelibrary2d.glUtil.MirroredBuffer;
import com.gamelibrary2d.glUtil.MirroredFloatBuffer;

import java.util.Arrays;

public class ParticleUpdateBuffer extends AbstractInterleavedBuffer<MirroredFloatBuffer> implements MirroredBuffer {

    public final static int STRIDE = 24;

    private final static int INITIALIZED = 0;
    private final static int CENTER_X = 1;
    private final static int CENTER_Y = 2;
    private final static int DELAY = 3;

    private final static int LIFE = 4;
    private final static int END_SPEED_FACTOR = 5;
    private final static int DELTA_X = 6;
    private final static int DELTA_Y = 7;

    private final static int ACCELERATION_X = 8;
    private final static int ACCELERATION_Y = 9;
    private final static int CENTRIPETAL_ACCELERATION = 10;
    private final static int TANGENTIAL_ACCELERATION = 11;

    private final static int ROTATED_FORWARD = 12;
    private final static int ROTATION_ACCELERATION = 13;
    private final static int DELTA_ROTATION = 14;
    private final static int DELTA_SCALE = 15;

    private final static int DELTA_COLOR_R = 16;
    private final static int DELTA_COLOR_G = 17;
    private final static int DELTA_COLOR_B = 18;
    private final static int DELTA_COLOR_A = 19;

    private final static int TIME = 20;
    private final static int EXTERNAL_SPEED_X = 21;
    private final static int EXTERNAL_SPEED_Y = 22;
    private final static int CUSTOM = 23;

    private float[] internalState;

    private ParticleUpdateBuffer(float[] internalState, Disposer disposer) {
        super(MirroredFloatBuffer.create(internalState, OpenGL.GL_SHADER_STORAGE_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer), STRIDE);
        this.internalState = internalState;
    }

    public static ParticleUpdateBuffer create(int capacity, Disposer disposer) {
        float[] data = new float[capacity * STRIDE];
        return new ParticleUpdateBuffer(data, disposer);
    }

    @Override
    public void updateGPU(int offset, int len) {
        int stride = getStride();
        getBuffer().updateGPU(offset * stride, len * stride);
    }

    @Override
    public void updateCPU(int offset, int len) {
        int stride = getStride();
        getBuffer().updateCPU(offset * stride, len * stride);
    }

    @Override
    public void copy(int offset, int destination, int len) {
        int stride = getStride();
        getBuffer().copy(offset * stride, destination * stride, len * stride);
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

    float getExternalSpeedX(int offset) {
        return internalState[offset + EXTERNAL_SPEED_X];
    }

    float getExternalSpeedY(int offset) {
        return internalState[offset + EXTERNAL_SPEED_Y];
    }

    boolean isInitialized(int offset) {
        return internalState[offset + INITIALIZED] != 0;
    }

    float getTime(int offset) {
        return internalState[offset + TIME];
    }

    float getDelay(int offset) {
        return internalState[offset + DELAY];
    }

    float getEndSpeedFactor(int offset) {
        return internalState[offset + END_SPEED_FACTOR];
    }

    float getCenterX(int offset) {
        return internalState[offset + CENTER_X];
    }

    float getCenterY(int offset) {
        return internalState[offset + CENTER_Y];
    }

    boolean isRotatedForward(int offset) {
        return internalState[offset + ROTATED_FORWARD] != 0;
    }

    float getHorizontalAcceleration(int offset) {
        return internalState[offset + ACCELERATION_X];
    }

    float getVerticalAcceleration(int offset) {
        return internalState[offset + ACCELERATION_Y];
    }

    float getCentripetalAcceleration(int offset) {
        return internalState[offset + CENTRIPETAL_ACCELERATION];
    }

    float getTangentialAcceleration(int offset) {
        return internalState[offset + TANGENTIAL_ACCELERATION];
    }

    float getRotationAcceleration(int offset) {
        return internalState[offset + ROTATION_ACCELERATION];
    }

    float getDeltaScale(int offset) {
        return internalState[offset + DELTA_SCALE];
    }

    float getDeltaColorR(int offset) {
        return internalState[offset + DELTA_COLOR_R];
    }

    float getDeltaColorG(int offset) {
        return internalState[offset + DELTA_COLOR_G];
    }

    float getDeltaColorB(int offset) {
        return internalState[offset + DELTA_COLOR_B];
    }

    float getDeltaColorA(int offset) {
        return internalState[offset + DELTA_COLOR_A];
    }

    float getDeltaRotation(int offset) {
        return internalState[offset + DELTA_ROTATION];
    }

    float getDeltaX(int offset) {
        return internalState[offset + DELTA_X];
    }

    float getDeltaY(int offset) {
        return internalState[offset + DELTA_Y];
    }

    float getLife(int offset) {
        return internalState[offset + LIFE];
    }

    float getCustom(int offset) {
        return internalState[offset + CUSTOM];
    }

    void setExternalSpeedX(int offset, float value) {
        internalState[offset + EXTERNAL_SPEED_X] = value;
    }

    void setExternalSpeedY(int offset, float value) {
        internalState[offset + EXTERNAL_SPEED_Y] = value;
    }

    void setInitialized(int offset, boolean value) {
        internalState[offset + INITIALIZED] = value ? 1 : 0;
    }

    void setTime(int offset, float value) {
        internalState[offset + TIME] = value;
    }

    void setDelay(int offset, float value) {
        internalState[offset + DELAY] = value;
    }

    void setEndSpeedFactor(int offset, float value) {
        internalState[offset + END_SPEED_FACTOR] = value;
    }

    void setCenterX(int offset, float value) {
        internalState[offset + CENTER_X] = value;
    }

    void setCenterY(int offset, float value) {
        internalState[offset + CENTER_Y] = value;
    }

    void setRotatedForward(int offset, boolean value) {
        internalState[offset + ROTATED_FORWARD] = value ? 1 : 0;
    }

    void setHorizontalAcceleration(int offset, float value) {
        internalState[offset + ACCELERATION_X] = value;
    }

    void setVerticalAcceleration(int offset, float value) {
        internalState[offset + ACCELERATION_Y] = value;
    }

    void setCentripetalAcceleration(int offset, float value) {
        internalState[offset + CENTRIPETAL_ACCELERATION] = value;
    }

    void setTangentialAcceleration(int offset, float value) {
        internalState[offset + TANGENTIAL_ACCELERATION] = value;
    }

    void setRotationAcceleration(int offset, float value) {
        internalState[offset + ROTATION_ACCELERATION] = value;
    }

    void setDeltaScale(int offset, float value) {
        internalState[offset + DELTA_SCALE] = value;
    }

    void setDeltaColorR(int offset, float value) {
        internalState[offset + DELTA_COLOR_R] = value;
    }

    void setDeltaColorG(int offset, float value) {
        internalState[offset + DELTA_COLOR_G] = value;
    }

    void setDeltaColorB(int offset, float value) {
        internalState[offset + DELTA_COLOR_B] = value;
    }

    void setDeltaColorA(int offset, float value) {
        internalState[offset + DELTA_COLOR_A] = value;
    }

    void setDeltaRotation(int offset, float value) {
        internalState[offset + DELTA_ROTATION] = value;
    }

    void setDeltaX(int offset, float value) {
        internalState[offset + DELTA_X] = value;
    }

    void setDeltaY(int offset, float value) {
        internalState[offset + DELTA_Y] = value;
    }

    void setLife(int offset, float value) {
        internalState[offset + LIFE] = value;
    }

    void setCustom(int offset, float value) {
        internalState[offset + CUSTOM] = value;
    }
}