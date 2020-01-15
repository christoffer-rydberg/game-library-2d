package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;
import com.gamelibrary2d.renderers.Renderer;

public class ParticleUpdateSettings {

    public final static int STRIDE = 54;
    private final static int LIFE = 0;
    private final static int LIFE_VAR = 1;
    private final static int DELAY = 2;
    private final static int DELAY_VAR = 3;

    private final static int SPEED = 4;
    private final static int SPEED_VAR = 5;
    private final static int END_SPEED_FACTOR = 6;
    private final static int END_SPEED_FACTOR_VAR = 7;

    private final static int INITIAL_YAW = 8;
    private final static int INITIAL_YAW_VAR = 9;
    private final static int INITIAL_PITCH = 10;
    private final static int INITIAL_PITCH_VAR = 11;

    private final static int INITIAL_DIR_FROM_GRAVITY_CENTER = 12;
    private final static int SCALE_X = 13;
    private final static int SCALE_Y = 14;
    private final static int UPDATE_SCALE = 15;

    private final static int SCALE_VAR = 16;
    private final static int END_SCALE_X = 17;
    private final static int END_SCALE_Y = 18;
    private final static int END_SCALE_VAR = 19;

    private final static int ACCELERATION_X = 20;
    private final static int ACCELERATION_Y = 21;
    private final static int ACCELERATION_Z = 22;
    private final static int ACCELERATION_X_VAR = 23;

    private final static int ACCELERATION_Y_VAR = 24;
    private final static int ACCELERATION_Z_VAR = 25;
    private final static int RADIAL_ACC = 26;
    private final static int RADIAL_ACC_VAR = 27;

    private final static int TANGENTIAL_ACC = 28;
    private final static int TANGENTIAL_ACC_VAR = 29;
    private final static int UPDATE_COLOR = 30;
    private final static int COLOR_R = 31;

    private final static int COLOR_G = 32;
    private final static int COLOR_B = 33;
    private final static int COLOR_R_VAR = 34;
    private final static int COLOR_G_VAR = 35;

    private final static int COLOR_B_VAR = 36;
    private final static int END_COLOR_R = 37;
    private final static int END_COLOR_G = 38;
    private final static int END_COLOR_B = 39;

    private final static int END_COLOR_R_VAR = 40;
    private final static int END_COLOR_G_VAR = 41;
    private final static int END_COLOR_B_VAR = 42;
    private final static int ALPHA = 43;

    private final static int ALPHA_VAR = 44;
    private final static int END_ALPHA = 45;
    private final static int END_ALPHA_VAR = 46;
    private final static int ROTATED_FORWARD = 47;

    private final static int ROTATION = 48;
    private final static int ROTATION_VAR = 49;
    private final static int ROTATION_SPEED = 50;
    private final static int ROTATION_SPEED_VAR = 51;

    private final static int ROTATION_ACCELERATION = 52;
    private final static int ROTATION_ACCELERATION_VAR = 53;
    private float[] internalState;
    private Renderer renderer;

    /**
     * Default constructor
     */
    public ParticleUpdateSettings() {
        internalState = new float[STRIDE];
        internalState[LIFE] = 1;
        internalState[COLOR_R] = 255;
        internalState[COLOR_G] = 255;
        internalState[COLOR_B] = 255;
        internalState[ALPHA] = 1;
        internalState[SCALE_X] = 1;
        internalState[SCALE_Y] = 1;
        internalState[END_SPEED_FACTOR] = 1;
    }

    /**
     * Copies settings from the serialized buffer.
     */
    public ParticleUpdateSettings(DataBuffer buffer) {
        internalState = new float[STRIDE];
        for (int i = 0; i < STRIDE; ++i)
            internalState[i] = buffer.getFloat();
    }

    public void serialize(DataBuffer buffer) {
        for (int i = 0; i < STRIDE; ++i)
            buffer.putFloat(internalState[i]);
    }

    public float[] getInternalStateArray() {
        return internalState;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setScale(float x, float y) {
        internalState[SCALE_X] = x;
        internalState[SCALE_Y] = y;
    }

    public void setUpdateScale(boolean updateScale) {
        internalState[UPDATE_SCALE] = updateScale ? 1f : 0f;
    }

    public void setEndScale(float x, float y) {
        internalState[END_SCALE_X] = x;
        internalState[END_SCALE_Y] = y;
    }

    public void setAcceleration(float x, float y, float z) {
        internalState[ACCELERATION_X] = x;
        internalState[ACCELERATION_Y] = y;
        internalState[ACCELERATION_Z] = z;
    }

    public void setAccelerationVar(float x, float y, float z) {
        internalState[ACCELERATION_X_VAR] = x;
        internalState[ACCELERATION_Y_VAR] = y;
        internalState[ACCELERATION_Z_VAR] = z;
    }

    public void setUpdateColor(boolean updateColor) {
        internalState[UPDATE_COLOR] = updateColor ? 1f : 0f;
    }

    public void setColor(float r, float g, float b) {
        internalState[COLOR_R] = r;
        internalState[COLOR_G] = g;
        internalState[COLOR_B] = b;
    }

    public void setColorVar(float r, float g, float b) {
        internalState[COLOR_R_VAR] = r;
        internalState[COLOR_G_VAR] = g;
        internalState[COLOR_B_VAR] = b;
    }

    public void setEndColor(float r, float g, float b) {
        internalState[END_COLOR_R] = r;
        internalState[END_COLOR_G] = g;
        internalState[END_COLOR_B] = b;
    }

    public void setEndColorVar(float r, float g, float b) {
        internalState[END_COLOR_R_VAR] = r;
        internalState[END_COLOR_G_VAR] = g;
        internalState[END_COLOR_B_VAR] = b;
    }

    public boolean isRotatedForward() {
        return internalState[ROTATED_FORWARD] == 1f;
    }

    public void setRotatedForward(boolean rotatedForward) {
        internalState[ROTATED_FORWARD] = rotatedForward ? 1f : 0f;
    }

    public float getDelay() {
        return internalState[DELAY];
    }

    public void setDelay(float delay) {
        internalState[DELAY] = delay;
    }

    public float getDelayVar() {
        return internalState[DELAY_VAR];
    }

    public void setDelayVar(float delayVar) {
        internalState[DELAY_VAR] = delayVar;
    }

    public float getLife() {
        return internalState[LIFE];
    }

    public void setLife(float life) {
        internalState[LIFE] = life;
    }

    public float getLifeVar() {
        return internalState[LIFE_VAR];
    }

    public void setLifeVar(float lifeVar) {
        internalState[LIFE_VAR] = lifeVar;
    }

    public float getSpeed() {
        return internalState[SPEED];
    }

    public void setSpeed(float speed) {
        internalState[SPEED] = speed;
    }

    public float getSpeedVar() {
        return internalState[SPEED_VAR];
    }

    public void setSpeedVar(float speedVar) {
        internalState[SPEED_VAR] = speedVar;
    }

    public float getEndSpeedFactor() {
        return internalState[END_SPEED_FACTOR];
    }

    public void setEndSpeedFactor(float endSpeedFactor) {
        internalState[END_SPEED_FACTOR] = endSpeedFactor;
    }

    public float getEndSpeedFactorVar() {
        return internalState[END_SPEED_FACTOR_VAR];
    }

    public void setEndSpeedFactorVar(float endSpeedFactorVar) {
        internalState[END_SPEED_FACTOR_VAR] = endSpeedFactorVar;
    }

    public float getInitialYaw() {
        return internalState[INITIAL_YAW];
    }

    public void setInitialYaw(float initialYaw) {
        internalState[INITIAL_YAW] = initialYaw;
    }

    public float getInitialYawVar() {
        return internalState[INITIAL_YAW_VAR];
    }

    public void setInitialYawVar(float initialYawVar) {
        internalState[INITIAL_YAW_VAR] = initialYawVar;
    }

    public float getInitialPitch() {
        return internalState[INITIAL_PITCH];
    }

    public void setInitialPitch(float initialPitch) {
        internalState[INITIAL_PITCH] = initialPitch;
    }

    public float getInitialPitchVar() {
        return internalState[INITIAL_PITCH_VAR];
    }

    public void setInitialPitchVar(float initialPitchVar) {
        internalState[INITIAL_PITCH_VAR] = initialPitchVar;
    }

    public boolean isInitialDirFromGravityCenter() {
        return internalState[INITIAL_DIR_FROM_GRAVITY_CENTER] == 1f;
    }

    public void setInitialDirFromGravityCenter(boolean initialDirFromGravityCenter) {
        internalState[INITIAL_DIR_FROM_GRAVITY_CENTER] = initialDirFromGravityCenter ? 1f : 0f;
    }

    public float getScaleX() {
        return internalState[SCALE_X];
    }

    public void setScaleX(float x) {
        internalState[SCALE_X] = x;
    }

    public float getScaleY() {
        return internalState[SCALE_Y];
    }

    public void setScaleY(float y) {
        internalState[SCALE_Y] = y;
    }

    public boolean isUpdatingScale() {
        return internalState[UPDATE_SCALE] == 1f;
    }

    public float getScaleVar() {
        return internalState[SCALE_VAR];
    }

    public void setScaleVar(float scaleVar) {
        internalState[SCALE_VAR] = scaleVar;
    }

    public float getEndScaleX() {
        return internalState[END_SCALE_X];
    }

    public void setEndScaleX(float x) {
        internalState[END_SCALE_X] = x;
    }

    public float getEndScaleY() {
        return internalState[END_SCALE_Y];
    }

    public void setEndScaleY(float y) {
        internalState[END_SCALE_Y] = y;
    }

    public float getEndScaleVar() {
        return internalState[END_SCALE_VAR];
    }

    public void setEndScaleVar(float endScaleVar) {
        internalState[END_SCALE_VAR] = endScaleVar;
    }

    public float getAccelerationX() {
        return internalState[ACCELERATION_X];
    }

    public void setAccelerationX(float x) {
        internalState[ACCELERATION_X] = x;
    }

    public float getAccelerationY() {
        return internalState[ACCELERATION_Y];
    }

    public void setAccelerationY(float y) {
        internalState[ACCELERATION_Y] = y;
    }

    public float getAccelerationZ() {
        return internalState[ACCELERATION_Z];
    }

    public void setAccelerationZ(float z) {
        internalState[ACCELERATION_Z] = z;
    }

    public float getAccelerationXVar() {
        return internalState[ACCELERATION_X_VAR];
    }

    public void setAccelerationXVar(float x) {
        internalState[ACCELERATION_X_VAR] = x;
    }

    public float getAccelerationYVar() {
        return internalState[ACCELERATION_Y_VAR];
    }

    public void setAccelerationYVar(float y) {
        internalState[ACCELERATION_Y_VAR] = y;
    }

    public float getAccelerationZVar() {
        return internalState[ACCELERATION_Z_VAR];
    }

    public void setAccelerationZVar(float z) {
        internalState[ACCELERATION_Z_VAR] = z;
    }

    public float getRadialAcc() {
        return internalState[RADIAL_ACC];
    }

    public void setRadialAcc(float radialAcc) {
        internalState[RADIAL_ACC] = radialAcc;
    }

    public float getRadialAccVar() {
        return internalState[RADIAL_ACC_VAR];
    }

    public void setRadialAccVar(float radialAccVar) {
        internalState[RADIAL_ACC_VAR] = radialAccVar;
    }

    public float getTangentalAcc() {
        return internalState[TANGENTIAL_ACC];
    }

    public void setTangentalAcc(float tangentalAcc) {
        internalState[TANGENTIAL_ACC] = tangentalAcc;
    }

    public float getTangentalAccVar() {
        return internalState[TANGENTIAL_ACC_VAR];
    }

    public void setTangentalAccVar(float tangentalAccVar) {
        internalState[TANGENTIAL_ACC_VAR] = tangentalAccVar;
    }

    public boolean isUpdatingColor() {
        return internalState[UPDATE_COLOR] == 1f;
    }

    public float getColorR() {
        return internalState[COLOR_R];
    }

    public void setColorR(float r) {
        internalState[COLOR_R] = r;
    }

    public float getColorG() {
        return internalState[COLOR_G];
    }

    public void setColorG(float g) {
        internalState[COLOR_G] = g;
    }

    public float getColorB() {
        return internalState[COLOR_B];
    }

    public void setColorB(float b) {
        internalState[COLOR_B] = b;
    }

    public float getColorRVar() {
        return internalState[COLOR_R_VAR];
    }

    public void setColorRVar(float r) {
        internalState[COLOR_R_VAR] = r;
    }

    public float getColorGVar() {
        return internalState[COLOR_G_VAR];
    }

    public void setColorGVar(float g) {
        internalState[COLOR_G_VAR] = g;
    }

    public float getColorBVar() {
        return internalState[COLOR_B_VAR];
    }

    public void setColorBVar(float b) {
        internalState[COLOR_B_VAR] = b;
    }

    public float getEndColorR() {
        return internalState[END_COLOR_R];
    }

    public void setEndColorR(float r) {
        internalState[END_COLOR_R] = r;
    }

    public float getEndColorG() {
        return internalState[END_COLOR_G];
    }

    public void setEndColorG(float g) {
        internalState[END_COLOR_G] = g;
    }

    public float getEndColorB() {
        return internalState[END_COLOR_B];
    }

    public void setEndColorB(float b) {
        internalState[END_COLOR_B] = b;
    }

    public float getEndColorRVar() {
        return internalState[END_COLOR_R_VAR];
    }

    public void setEndColorRVar(float r) {
        internalState[END_COLOR_R_VAR] = r;
    }

    public float getEndColorGVar() {
        return internalState[END_COLOR_G_VAR];
    }

    public void setEndColorGVar(float g) {
        internalState[END_COLOR_G_VAR] = g;
    }

    public float getEndColorBVar() {
        return internalState[END_COLOR_B_VAR];
    }

    public void setEndColorBVar(float b) {
        internalState[END_COLOR_B_VAR] = b;
    }

    public float getAlpha() {
        return internalState[ALPHA];
    }

    public void setAlpha(float alpha) {
        internalState[ALPHA] = alpha;
    }

    public float getAlphaVar() {
        return internalState[ALPHA_VAR];
    }

    public void setAlphaVar(float alphaVar) {
        internalState[ALPHA_VAR] = alphaVar;
    }

    public float getEndAlpha() {
        return internalState[END_ALPHA];
    }

    public void setEndAlpha(float endAlpha) {
        internalState[END_ALPHA] = endAlpha;
    }

    public float getEndAlphaVar() {
        return internalState[END_ALPHA_VAR];
    }

    public void setEndAlphaVar(float endAlphaVar) {
        internalState[END_ALPHA_VAR] = endAlphaVar;
    }

    public float getRotation() {
        return internalState[ROTATION];
    }

    public void setRotation(float rotation) {
        internalState[ROTATION] = rotation;
    }

    public float getRotationVar() {
        return internalState[ROTATION_VAR];
    }

    public void setRotationVar(float rotationVar) {
        internalState[ROTATION_VAR] = rotationVar;
    }

    public float getRotationSpeed() {
        return internalState[ROTATION_SPEED];
    }

    public void setRotationSpeed(float rotationSpeed) {
        internalState[ROTATION_SPEED] = rotationSpeed;
    }

    public float getRotationSpeedVar() {
        return internalState[ROTATION_SPEED_VAR];
    }

    public void setRotationSpeedVar(float rotationSpeedVar) {
        internalState[ROTATION_SPEED_VAR] = rotationSpeedVar;
    }

    public float getRotationAcc() {
        return internalState[ROTATION_ACCELERATION];
    }

    public void setRotationAcc(float rotationAcceleration) {
        internalState[ROTATION_ACCELERATION] = rotationAcceleration;
    }

    public float getRotationAccVar() {
        return internalState[ROTATION_ACCELERATION_VAR];
    }

    public void setRotationAccVar(float rotationAccelerationVar) {
        internalState[ROTATION_ACCELERATION_VAR] = rotationAccelerationVar;
    }

    public void apply(Particle particle) {

        particle.setTime(0);

        particle.setRenderer(getRenderer());

        float emittedLife = getLife() + getLifeVar() * RandomInstance.random11();

        float emittedColor0 = getColorR() + getColorRVar() * RandomInstance.random11();
        float emittedColor1 = getColorG() + getColorGVar() * RandomInstance.random11();
        float emittedColor2 = getColorB() + getColorBVar() * RandomInstance.random11();
        float emittedAlpha = getAlpha() + getAlphaVar() * RandomInstance.random11();

        float randomizedScaleVar = getScaleVar() * RandomInstance.random11();
        float emittedScale0 = getScaleX() + randomizedScaleVar;
        float emittedScale1 = getScaleY() + randomizedScaleVar;

        float speed = getSpeed() + getSpeedVar() * RandomInstance.random11();

        float acceleration = getTangentalAcc() + getTangentalAccVar() * RandomInstance.random11();

        boolean speedSet = false;

        if (isInitialDirFromGravityCenter()) {

            float dirX = particle.getPosX() - particle.getGravityCenterX();
            float dirY = particle.getPosY() - particle.getGravityCenterY();
            float dirZ = particle.getPosZ() - particle.getGravityCenterZ();

            if (dirX != 0 && dirY != 0 && dirZ != 0) {

                float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

                particle.setVelocity((dirX / length) * speed, (dirY / length) * speed, (dirZ / length) * speed);

                speedSet = true;
            }
        }

        if (!speedSet) {
            float yaw = getInitialYaw() + getInitialYawVar() * RandomInstance.random11();
            float pitch = getInitialPitch() + getInitialPitchVar() * RandomInstance.random11();
            double initialYaw = (yaw - 90.0) * Math.PI / 180d;
            double initialPitch = pitch * Math.PI / 180d;
            double cosPitchTimesSpeed = Math.cos(initialPitch) * speed;
            float deltaX = (float) (Math.cos(initialYaw) * cosPitchTimesSpeed);
            float deltaY = (float) (-Math.sin(initialYaw) * cosPitchTimesSpeed);
            float deltaZ = (float) Math.sin(initialPitch) * speed;
            particle.setVelocity(deltaX, deltaY, deltaZ);
        }

        particle.setDelay(getDelay() + getDelayVar() * RandomInstance.random11());

        float endSpeedFactor = getEndSpeedFactor() + getEndSpeedFactorVar() * RandomInstance.random11();
        particle.setEndSpeedFactor(endSpeedFactor);

        particle.setAxisAcceleration(getAccelerationX() + getAccelerationXVar() * RandomInstance.random11(),
                getAccelerationY() + getAccelerationYVar() * RandomInstance.random11(),
                getAccelerationZ() + getAccelerationZVar() * RandomInstance.random11());

        particle.setRadialAcceleration(getRadialAcc() + getRadialAccVar() * RandomInstance.random11());

        particle.setTangentialAcceleration(acceleration);

        particle.setLife(emittedLife);

        particle.setScale(emittedScale0, emittedScale1);

        if (isUpdatingScale()) {
            float randomizedEndScaleVar = getEndScaleVar() * RandomInstance.random11();
            particle.setDeltaScale(((getEndScaleX() + randomizedEndScaleVar) - emittedScale0) / emittedLife,
                    ((getEndScaleY() + randomizedEndScaleVar) - emittedScale1) / emittedLife);
        } else {
            particle.setDeltaScale(0, 0);
        }

        particle.setColor(emittedColor0 / 255f, emittedColor1 / 255f, emittedColor2 / 255f, emittedAlpha);

        if (isUpdatingColor()) {
            particle.setDeltaColor(
                    ((getEndColorR() + getEndColorRVar() * RandomInstance.random11()) - emittedColor0)
                            / (255f * emittedLife),
                    ((getEndColorG() + getEndColorGVar() * RandomInstance.random11()) - emittedColor1)
                            / (255f * emittedLife),
                    ((getEndColorB() + getEndColorBVar() * RandomInstance.random11()) - emittedColor2)
                            / (255f * emittedLife),
                    ((getEndAlpha() + getEndAlphaVar() * RandomInstance.random11()) - emittedAlpha) / emittedLife);
        } else {
            particle.setDeltaColor(0, 0, 0,
                    ((getEndAlpha() + getEndAlphaVar() * RandomInstance.random11()) - emittedAlpha) / emittedLife);
        }

        if (isRotatedForward()) {
            particle.setRotatedForward(true);
        } else {
            particle.setRotation(getRotation() + getRotationVar() * RandomInstance.random11());
            particle.setRotationSpeed(getRotationSpeed() + getRotationSpeedVar() * RandomInstance.random11());
            particle.setRotationAcceleration(getRotationAcc() + getRotationAccVar() * RandomInstance.random11());
        }
    }
}