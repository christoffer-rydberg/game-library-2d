package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;

public class ParticleParameters implements Serializable {
    private final static int STRIDE = 48;

    private final static int LIFE = 0;
    private final static int LIFE_VAR = 1;
    private final static int DELAY = 2;
    private final static int DELAY_VAR = 3;

    private final static int SPEED = 4;
    private final static int SPEED_VAR = 5;
    private final static int END_SPEED_FACTOR = 6;
    private final static int END_SPEED_FACTOR_VAR = 7;

    private final static int SCALE = 8;
    private final static int SCALE_VAR = 9;
    private final static int END_SCALE = 10;
    private final static int END_SCALE_VAR = 11;

    private final static int UPDATE_SCALE = 12;
    private final static int DIRECTION = 13;
    private final static int DIRECTION_VAR = 14;
    private final static int MOVE_AWAY_FROM_CENTER = 15;

    private final static int HORIZONTAL_ACCELERATION = 16;
    private final static int HORIZONTAL_ACCELERATION_VAR = 17;
    private final static int VERTICAL_ACCELERATION = 18;
    private final static int VERTICAL_ACCELERATION_VAR = 19;

    private final static int CENTRIPETAL_ACCELERATION = 20;
    private final static int CENTRIPETAL_ACCELERATION_VAR = 21;
    private final static int TANGENTIAL_ACCELERATION = 22;
    private final static int TANGENTIAL_ACCELERATION_VAR = 23;

    private final static int ROTATION = 24;
    private final static int ROTATION_VAR = 25;
    private final static int ROTATION_SPEED = 26;
    private final static int ROTATION_SPEED_VAR = 27;

    private final static int ROTATION_ACCELERATION = 28;
    private final static int ROTATION_ACCELERATION_VAR = 29;
    private final static int ROTATED_FORWARD = 30;
    private final static int UPDATE_COLOR = 31;

    private final static int COLOR_R = 32;
    private final static int COLOR_R_VAR = 33;
    private final static int COLOR_G = 34;
    private final static int COLOR_G_VAR = 35;

    private final static int COLOR_B = 36;
    private final static int COLOR_B_VAR = 37;
    private final static int END_COLOR_R = 38;
    private final static int END_COLOR_R_VAR = 39;

    private final static int END_COLOR_G = 40;
    private final static int END_COLOR_G_VAR = 41;
    private final static int END_COLOR_B = 42;
    private final static int END_COLOR_B_VAR = 43;

    private final static int ALPHA = 44;
    private final static int ALPHA_VAR = 45;
    private final static int END_ALPHA = 46;
    private final static int END_ALPHA_VAR = 47;

    private float[] internalState;

    /**
     * Default constructor
     */
    public ParticleParameters() {
        internalState = new float[STRIDE];
        internalState[LIFE] = 1;
        internalState[COLOR_R] = 255;
        internalState[COLOR_G] = 255;
        internalState[COLOR_B] = 255;
        internalState[ALPHA] = 1;
        internalState[SCALE] = 1;
        internalState[END_SPEED_FACTOR] = 1;
    }

    /**
     * Copies settings from the serialized buffer.
     */
    public ParticleParameters(DataBuffer buffer) {
        internalState = new float[STRIDE];
        for (int i = 0; i < STRIDE; ++i) {
            internalState[i] = buffer.getFloat();
        }
    }

    @Override
    public void serialize(DataBuffer buffer) {
        for (int i = 0; i < STRIDE; ++i) {
            buffer.putFloat(internalState[i]);
        }
    }

    public float[] getInternalStateArray() {
        return internalState;
    }

    public void setUpdateScale(boolean updateScale) {
        internalState[UPDATE_SCALE] = updateScale ? 1f : 0f;
    }

    public void setAcceleration(float horizontal, float vertical) {
        internalState[HORIZONTAL_ACCELERATION] = horizontal;
        internalState[VERTICAL_ACCELERATION] = vertical;
    }

    public void setAccelerationVar(float horizontalAccVar, float verticalAccVar) {
        internalState[HORIZONTAL_ACCELERATION_VAR] = horizontalAccVar;
        internalState[VERTICAL_ACCELERATION_VAR] = verticalAccVar;
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

    public float getDirection() {
        return internalState[DIRECTION];
    }

    public void setDirection(float direction) {
        internalState[DIRECTION] = direction;
    }

    public float getDirectionVar() {
        return internalState[DIRECTION_VAR];
    }

    public void setDirectionVar(float directionVar) {
        internalState[DIRECTION_VAR] = directionVar;
    }

    public boolean isMovingAwayFromCenter() {
        return internalState[MOVE_AWAY_FROM_CENTER] == 1f;
    }

    public void setMoveAwayFromCenter(boolean moveAwayFromCenter) {
        internalState[MOVE_AWAY_FROM_CENTER] = moveAwayFromCenter ? 1f : 0f;
    }

    public float getScale() {
        return internalState[SCALE];
    }

    public void setScale(float scale) {
        internalState[SCALE] = scale;
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

    public float getEndScale() {
        return internalState[END_SCALE];
    }

    public void setEndScale(float endScale) {
        internalState[END_SCALE] = endScale;
    }

    public float getEndScaleVar() {
        return internalState[END_SCALE_VAR];
    }

    public void setEndScaleVar(float endScaleVar) {
        internalState[END_SCALE_VAR] = endScaleVar;
    }

    public float getHorizontalAcceleration() {
        return internalState[HORIZONTAL_ACCELERATION];
    }

    public void setHorizontalAcceleration(float x) {
        internalState[HORIZONTAL_ACCELERATION] = x;
    }

    public float getVerticalAcceleration() {
        return internalState[VERTICAL_ACCELERATION];
    }

    public void setVerticalAcceleration(float y) {
        internalState[VERTICAL_ACCELERATION] = y;
    }

    public float getHorizontalAccelerationVar() {
        return internalState[HORIZONTAL_ACCELERATION_VAR];
    }

    public void setHorizontalAccelerationVar(float x) {
        internalState[HORIZONTAL_ACCELERATION_VAR] = x;
    }

    public float getVerticalAccelerationVar() {
        return internalState[VERTICAL_ACCELERATION_VAR];
    }

    public void setVerticalAccelerationVar(float y) {
        internalState[VERTICAL_ACCELERATION_VAR] = y;
    }

    public float getCentripetalAcceleration() {
        return internalState[CENTRIPETAL_ACCELERATION];
    }

    public void setCentripetalAcceleration(float centripetalAcc) {
        internalState[CENTRIPETAL_ACCELERATION] = centripetalAcc;
    }

    public float getCentripetalAccelerationVar() {
        return internalState[CENTRIPETAL_ACCELERATION_VAR];
    }

    public void setCentripetalAccelerationVar(float centripetalAccVar) {
        internalState[CENTRIPETAL_ACCELERATION_VAR] = centripetalAccVar;
    }

    public float getTangentalAcceleration() {
        return internalState[TANGENTIAL_ACCELERATION];
    }

    public void setTangentalAcceleration(float tangentalAcc) {
        internalState[TANGENTIAL_ACCELERATION] = tangentalAcc;
    }

    public float getTangentalAccVar() {
        return internalState[TANGENTIAL_ACCELERATION_VAR];
    }

    public void setTangentalAccVar(float tangentalAccVar) {
        internalState[TANGENTIAL_ACCELERATION_VAR] = tangentalAccVar;
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

    public float getRotationAcceleration() {
        return internalState[ROTATION_ACCELERATION];
    }

    public void setRotationAcceleration(float rotationAcceleration) {
        internalState[ROTATION_ACCELERATION] = rotationAcceleration;
    }

    public float getRotationAccVar() {
        return internalState[ROTATION_ACCELERATION_VAR];
    }

    public void setRotationAccVar(float rotationAccelerationVar) {
        internalState[ROTATION_ACCELERATION_VAR] = rotationAccelerationVar;
    }

    public void scale(float factor) {
        setSpeed(getSpeed() * factor);
        setSpeedVar(getSpeedVar() * factor);
        setScale(getScale() * factor);
        setScaleVar(getScaleVar() * factor);
        setEndScale(getEndScale() * factor);
        setEndScaleVar(getEndScaleVar() * factor);
        setAcceleration(getHorizontalAcceleration() * factor, getVerticalAcceleration() * factor);
        setAccelerationVar(getHorizontalAccelerationVar() * factor, getVerticalAccelerationVar() * factor);
        setCentripetalAcceleration(getCentripetalAcceleration() * factor);
        setCentripetalAccelerationVar(getCentripetalAccelerationVar() * factor);
        setTangentalAcceleration(getTangentalAcceleration() * factor);
        setTangentalAccVar(getTangentalAccVar() * factor);
    }

    public void apply(Particle particle) {
        float emittedLife = getLife() + getLifeVar() * RandomInstance.random11();

        float emittedColor0 = getColorR() + getColorRVar() * RandomInstance.random11();
        float emittedColor1 = getColorG() + getColorGVar() * RandomInstance.random11();
        float emittedColor2 = getColorB() + getColorBVar() * RandomInstance.random11();
        float emittedAlpha = getAlpha() + getAlphaVar() * RandomInstance.random11();

        float emittedScale = getScale() + getScaleVar() * RandomInstance.random11();

        float emittedSpeed = getSpeed() + getSpeedVar() * RandomInstance.random11();

        var centripetalAcc = getCentripetalAcceleration() + getCentripetalAccelerationVar() * RandomInstance.random11();
        if (centripetalAcc != 0 && emittedSpeed == 0) {
            emittedSpeed = 1f; // A little bump to get a direction for the velocity vector.
        }

        float velocityX = 0, velocityY = 0;
        if (emittedSpeed != 0) {
            if (isMovingAwayFromCenter()) {
                float dirX = particle.getPosX() - particle.getCenterX();
                float dirY = particle.getPosY() - particle.getCenterY();
                if (dirX != 0 || dirY != 0) {
                    float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
                    velocityX = (dirX / length) * emittedSpeed;
                    velocityY = (dirY / length) * emittedSpeed;
                } else {
                    velocityY = emittedSpeed;
                }
            } else {
                velocityY = emittedSpeed;
            }

            float direction = getDirection() + getDirectionVar() * RandomInstance.random11();
            double directionRadians = direction * Math.PI / 180d;
            double sin = Math.sin(directionRadians);
            double cos = Math.cos(directionRadians);
            float velocityX0 = velocityX;
            velocityX = (float) (velocityX0 * cos + velocityY * sin);
            velocityY = (float) (-velocityX0 * sin + velocityY * cos);
        }

        float endSpeedFactor = getEndSpeedFactor() + getEndSpeedFactorVar() * RandomInstance.random11();

        particle.setScale(emittedScale);
        particle.setRotation(getRotation() + getRotationVar() * RandomInstance.random11());

        particle.setColor(emittedColor0 / 255f, emittedColor1 / 255f, emittedColor2 / 255f, emittedAlpha);

        particle.setDelay(getDelay() + getDelayVar() * RandomInstance.random11());

        particle.setLife(emittedLife);
        particle.setEndSpeedFactor(endSpeedFactor);
        particle.setVelocity(velocityX, velocityY);

        particle.setHorizontalAcceleration(
                getHorizontalAcceleration() + getHorizontalAccelerationVar() * RandomInstance.random11());

        particle.setVerticalAcceleration(
                getVerticalAcceleration() + getVerticalAccelerationVar() * RandomInstance.random11());

        particle.setCentripetalAccelerationeleration(centripetalAcc);

        particle.setTangentialAcceleration(getTangentalAcceleration() + getTangentalAccVar() * RandomInstance.random11());

        if (isRotatedForward()) {
            particle.setRotatedForward(true);
        } else {
            particle.setRotationSpeed(getRotationSpeed() + getRotationSpeedVar() * RandomInstance.random11());
            particle.setRotationAcceleration(getRotationAcceleration() + getRotationAccVar() * RandomInstance.random11());
        }

        if (isUpdatingScale()) {
            float randomizedEndScaleVar = getEndScaleVar() * RandomInstance.random11();
            particle.setDeltaScale(((getEndScale() + randomizedEndScaleVar) - emittedScale) / emittedLife);
        } else {
            particle.setDeltaScale(0);
        }

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

        particle.setTime(0f);
    }
}