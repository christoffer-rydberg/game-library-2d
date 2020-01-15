package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.FloatTransferBuffer;

class ParticleUpdateBuffer extends FloatTransferBuffer {

    final static int STRIDE = 28;
    private final static int DELTA_X = 0;
    private final static int DELTA_Y = 1;
    private final static int DELTA_Z = 2;
    private final static int INITIALIZED = 3;
    private final static int LIFE = 4;
    private final static int EXTERNAL_SPEED_X = 5;
    private final static int EXTERNAL_SPEED_Y = 6;
    private final static int EXTERNAL_SPEED_Z = 7;
    private final static int DELAY = 8;
    // 9 is available in case a new setting is added
    private final static int END_SPEED_FACTOR = 10;
    private final static int ROTATION_ACC = 11;
    private final static int GRAVITY_CENTER_X = 12;
    private final static int GRAVITY_CENTER_Y = 13;
    private final static int GRAVITY_CENTER_Z = 14;
    private final static int ROTATED_FORWARD = 15;
    private final static int ACCELERATION_X = 16;
    private final static int ACCELERATION_Y = 17;
    private final static int ACCELERATION_Z = 18;
    private final static int RADIAL_ACC = 19;
    private final static int TANGENTIAL_ACC = 20;
    private final static int DELTA_SCALE_X = 21;
    private final static int DELTA_SCALE_Y = 22;
    private final static int DELTA_ROTATION = 23;
    private final static int DELTA_COLOR_R = 24;
    private final static int DELTA_COLOR_G = 25;
    private final static int DELTA_COLOR_B = 26;
    private final static int DELTA_COLOR_A = 27;
    private final float[] internalState;

    ParticleUpdateBuffer(float[] internalState, Disposer disposer) {
        super(internalState, STRIDE, OpenGL.GL_SHADER_STORAGE_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);
        this.internalState = internalState;
    }

    float getExternalSpeedX(int offset) {
        return internalState[offset + EXTERNAL_SPEED_X];
    }

    float getExternalSpeedY(int offset) {
        return internalState[offset + EXTERNAL_SPEED_Y];
    }

    float getExternalSpeedZ(int offset) {
        return internalState[offset + EXTERNAL_SPEED_Z];
    }

    boolean isInitialized(int offset) {
        return internalState[offset + INITIALIZED] != 0;
    }

    float getDelay(int offset) {
        return internalState[offset + DELAY];
    }

    float getEndSpeedFactor(int offset) {
        return internalState[offset + END_SPEED_FACTOR];
    }

    float getGravityCenterX(int offset) {
        return internalState[offset + GRAVITY_CENTER_X];
    }

    float getGravityCenterY(int offset) {
        return internalState[offset + GRAVITY_CENTER_Y];
    }

    float getGravityCenterZ(int offset) {
        return internalState[offset + GRAVITY_CENTER_Z];
    }

    boolean isRotatedForward(int offset) {
        return internalState[offset + ROTATED_FORWARD] != 0;
    }

    float getAccelerationX(int offset) {
        return internalState[offset + ACCELERATION_X];
    }

    float getAccelerationY(int offset) {
        return internalState[offset + ACCELERATION_Y];
    }

    float getAccelerationZ(int offset) {
        return internalState[offset + ACCELERATION_Z];
    }

    float getRadialAcceleration(int offset) {
        return internalState[offset + RADIAL_ACC];
    }

    float getTangentialAcceleration(int offset) {
        return internalState[offset + TANGENTIAL_ACC];
    }

    float getRotationAcceleration(int offset) {
        return internalState[offset + ROTATION_ACC];
    }

    float getDeltaScaleX(int offset) {
        return internalState[offset + DELTA_SCALE_X];
    }

    float getDeltaScaleY(int offset) {
        return internalState[offset + DELTA_SCALE_Y];
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

    float getDeltaZ(int offset) {
        return internalState[offset + DELTA_Z];
    }

    float getLife(int offset) {
        return internalState[offset + LIFE];
    }

    void setExternalSpeedX(int offset, float value) {
        internalState[offset + EXTERNAL_SPEED_X] = value;
    }

    void setExternalSpeedY(int offset, float value) {
        internalState[offset + EXTERNAL_SPEED_Y] = value;
    }

    void setExternalSpeedZ(int offset, float value) {
        internalState[offset + EXTERNAL_SPEED_Z] = value;
    }

    void setInitialized(int offset, boolean value) {
        internalState[offset + INITIALIZED] = value ? 1 : 0;
    }

    void setDelay(int offset, float value) {
        internalState[offset + DELAY] = value;
    }

    void setEndSpeedFactor(int offset, float value) {
        internalState[offset + END_SPEED_FACTOR] = value;
    }

    void setGravityCenterX(int offset, float value) {
        internalState[offset + GRAVITY_CENTER_X] = value;
    }

    void setGravityCenterY(int offset, float value) {
        internalState[offset + GRAVITY_CENTER_Y] = value;
    }

    void setGravityCenterZ(int offset, float value) {
        internalState[offset + GRAVITY_CENTER_Z] = value;
    }

    void setRotatedForward(int offset, boolean value) {
        internalState[offset + ROTATED_FORWARD] = value ? 1 : 0;
    }

    void setAccelerationX(int offset, float value) {
        internalState[offset + ACCELERATION_X] = value;
    }

    void setAccelerationY(int offset, float value) {
        internalState[offset + ACCELERATION_Y] = value;
    }

    void setAccelerationZ(int offset, float value) {
        internalState[offset + ACCELERATION_Z] = value;
    }

    void setRadialAcceleration(int offset, float value) {
        internalState[offset + RADIAL_ACC] = value;
    }

    void setTangentialAcceleration(int offset, float value) {
        internalState[offset + TANGENTIAL_ACC] = value;
    }

    void setRotationAcceleration(int offset, float value) {
        internalState[offset + ROTATION_ACC] = value;
    }

    void setDeltaScaleX(int offset, float value) {
        internalState[offset + DELTA_SCALE_X] = value;
    }

    void setDeltaScaleY(int offset, float value) {
        internalState[offset + DELTA_SCALE_Y] = value;
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

    void setDeltaZ(int offset, float value) {
        internalState[offset + DELTA_Z] = value;
    }

    void setLife(int offset, float value) {
        internalState[offset + LIFE] = value;
    }
}