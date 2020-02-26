package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.renderers.Renderer;

public class Particle {

    private int index = -1;
    private int renderOffset;
    private int updateOffset;
    private Renderer renderer;

    private InternalParticleBuffer renderBuffer;
    private ParticleUpdateBuffer updateBuffer;

    Particle(InternalParticleBuffer renderBuffer, ParticleUpdateBuffer updateBuffer, int index) {
        this.renderBuffer = renderBuffer;
        this.updateBuffer = updateBuffer;
        setIndex(index);
    }

    public float getTime() {
        return renderBuffer.getTime(renderOffset);
    }

    public void setTime(float time) {
        renderBuffer.setTime(renderOffset, time);
    }

    public float getDelay() {
        return updateBuffer.getDelay(updateOffset);
    }

    public void setDelay(float delay) {
        updateBuffer.setDelay(updateOffset, delay);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public float getLife() {
        return updateBuffer.getLife(updateOffset);
    }

    public void setLife(float life) {
        updateBuffer.setLife(updateOffset, life);
    }

    public float getPosX() {
        return renderBuffer.getPosX(renderOffset);
    }

    public float getPosY() {
        return renderBuffer.getPosY(renderOffset);
    }

    public float getPosZ() {
        return renderBuffer.getPosZ(renderOffset);
    }

    public float getGravityCenterX() {
        return updateBuffer.getGravityCenterX(updateOffset);
    }

    public float getGravityCenterY() {
        return updateBuffer.getGravityCenterY(updateOffset);
    }

    public float getGravityCenterZ() {
        return updateBuffer.getGravityCenterZ(updateOffset);
    }

    public float getAccelerationX() {
        return updateBuffer.getAccelerationX(updateOffset);
    }

    public float getAccelerationY() {
        return updateBuffer.getAccelerationY(updateOffset);
    }

    public float getAccelerationZ() {
        return updateBuffer.getAccelerationZ(updateOffset);
    }

    public float getRadialAcceleration() {
        return updateBuffer.getRadialAcceleration(updateOffset);
    }

    public void setRadialAcceleration(float radialAcc) {
        updateBuffer.setRadialAcceleration(updateOffset, radialAcc);
    }

    public float getTangentialAcceleration() {
        return updateBuffer.getTangentialAcceleration(updateOffset);
    }

    public void setTangentialAcceleration(float tangentialAcc) {
        updateBuffer.setTangentialAcceleration(updateOffset, tangentialAcc);
    }

    public float getColorR() {
        return renderBuffer.getColorR(renderOffset);
    }

    public float getColorG() {
        return renderBuffer.getColorG(renderOffset);
    }

    public float getColorB() {
        return renderBuffer.getColorB(renderOffset);
    }

    public float getColorA() {
        return renderBuffer.getColorA(renderOffset);
    }

    public float getDeltaColorR() {
        return updateBuffer.getDeltaColorR(updateOffset);
    }

    public float getDeltaColorG() {
        return updateBuffer.getDeltaColorG(updateOffset);
    }

    public float getDeltaColorB() {
        return updateBuffer.getDeltaColorB(updateOffset);
    }

    public float getDeltaColorA() {
        return updateBuffer.getDeltaColorA(updateOffset);
    }

    public float getScaleX() {
        return renderBuffer.getScaleX(renderOffset);
    }

    public float getScaleY() {
        return renderBuffer.getScaleY(renderOffset);
    }

    public float getDeltaScaleX() {
        return updateBuffer.getDeltaScaleX(updateOffset);
    }

    public float getDeltaScaleY() {
        return updateBuffer.getDeltaScaleY(updateOffset);
    }

    public float getRotation() {
        return renderBuffer.getRotation(renderOffset);
    }

    public void setRotation(float rotation) {
        renderBuffer.setRotation(renderOffset, rotation);
    }

    public float getRotationSpeed() {
        return updateBuffer.getDeltaRotation(updateOffset);
    }

    public void setRotationSpeed(float rotationSpeed) {
        updateBuffer.setDeltaRotation(updateOffset, rotationSpeed);
    }

    public float getRotationAcceleration() {
        return updateBuffer.getRotationAcceleration(updateOffset);
    }

    public void setRotationAcceleration(float rotationAcc) {
        updateBuffer.setRotationAcceleration(updateOffset, rotationAcc);
    }

    public boolean isRotatedForward() {
        return updateBuffer.isRotatedForward(updateOffset);
    }

    public void setRotatedForward(boolean rotatedForward) {
        updateBuffer.setRotatedForward(updateOffset, rotatedForward);
    }

    public boolean hasExpired() {
        return getTime() >= getLife();
    }

    void setRenderBuffer(InternalParticleBuffer renderBuffer) {
        this.renderBuffer = renderBuffer;
    }

    void setUpdateBuffer(ParticleUpdateBuffer updateBuffer) {
        this.updateBuffer = updateBuffer;
    }

    void setIndex(int index) {
        if (this.index != index) {
            this.index = index;
            renderOffset = index * renderBuffer.stride();
            updateOffset = index * updateBuffer.stride();
        }
    }

    public void setPosition(float x, float y, float z) {
        renderBuffer.setPosX(renderOffset, x);
        renderBuffer.setPosY(renderOffset, y);
        renderBuffer.setPosZ(renderOffset, z);
    }

    public void setGravityCenter(float x, float y, float z) {
        updateBuffer.setGravityCenterX(updateOffset, x);
        updateBuffer.setGravityCenterY(updateOffset, y);
        updateBuffer.setGravityCenterZ(updateOffset, z);
    }

    public void setAxisAcceleration(float accelerationX, float accelerationY, float accelerationZ) {
        updateBuffer.setAccelerationX(updateOffset, accelerationX);
        updateBuffer.setAccelerationY(updateOffset, accelerationY);
        updateBuffer.setAccelerationZ(updateOffset, accelerationZ);
    }

    public void setColor(float r, float g, float b, float a) {
        renderBuffer.setColorR(renderOffset, r);
        renderBuffer.setColorG(renderOffset, g);
        renderBuffer.setColorB(renderOffset, b);
        renderBuffer.setColorA(renderOffset, a);
    }

    public void setDeltaColor(float deltaR, float deltaG, float deltaB, float deltaA) {
        updateBuffer.setDeltaColorR(updateOffset, deltaR);
        updateBuffer.setDeltaColorG(updateOffset, deltaG);
        updateBuffer.setDeltaColorB(updateOffset, deltaB);
        updateBuffer.setDeltaColorA(updateOffset, deltaA);
    }

    public void setScale(float scaleX, float scaleY) {
        renderBuffer.setScaleX(renderOffset, scaleX);
        renderBuffer.setScaleY(renderOffset, scaleY);
    }

    public void setDeltaScale(float deltaScaleX, float deltaScaleY) {
        updateBuffer.setDeltaScaleX(updateOffset, deltaScaleX);
        updateBuffer.setDeltaScaleY(updateOffset, deltaScaleY);
    }

    public void setVelocity(float deltaX, float deltaY, float deltaZ) {
        updateBuffer.setDeltaX(updateOffset, deltaX);
        updateBuffer.setDeltaY(updateOffset, deltaY);
        updateBuffer.setDeltaZ(updateOffset, deltaZ);
    }

    public float getEndSpeedFactor() {
        return updateBuffer.getEndSpeedFactor(updateOffset);
    }

    public void setEndSpeedFactor(float endSpeedFactor) {
        updateBuffer.setEndSpeedFactor(updateOffset, endSpeedFactor);
    }

    public void onEmitted(float[] externalSpeed) {
        setInitialized(false);
        setExternalSpeedX(externalSpeed[0]);
        setExternalSpeedY(externalSpeed[1]);
        setExternalSpeedZ(externalSpeed[2]);
    }

    private boolean isInitialized() {
        return updateBuffer.isInitialized(updateOffset);
    }

    private void setInitialized(boolean initialized) {
        updateBuffer.setInitialized(updateOffset, initialized);
    }

    private float getExternalSpeedX() {
        return updateBuffer.getExternalSpeedX(updateOffset);
    }

    private void setExternalSpeedX(float externalSpeedX) {
        updateBuffer.setExternalSpeedX(updateOffset, externalSpeedX);
    }

    private float getExternalSpeedY() {
        return updateBuffer.getExternalSpeedY(updateOffset);
    }

    private void setExternalSpeedY(float externalSpeedY) {
        updateBuffer.setExternalSpeedY(updateOffset, externalSpeedY);
    }

    private float getExternalSpeedZ() {
        return updateBuffer.getExternalSpeedZ(updateOffset);
    }

    private void setExternalSpeedZ(float externalSpeedZ) {
        updateBuffer.setExternalSpeedZ(updateOffset, externalSpeedZ);
    }

    private float getDeltaX() {
        return updateBuffer.getDeltaX(updateOffset);
    }

    private void setDeltaX(float deltaX) {
        updateBuffer.setDeltaX(updateOffset, deltaX);
    }

    private float getDeltaY() {
        return updateBuffer.getDeltaY(updateOffset);
    }

    private void setDeltaY(float deltaY) {
        updateBuffer.setDeltaY(updateOffset, deltaY);
    }

    private float getDeltaZ() {
        return updateBuffer.getDeltaZ(updateOffset);
    }

    private void setDeltaZ(float deltaZ) {
        updateBuffer.setDeltaZ(updateOffset, deltaZ);
    }

    void update(float[] externalAcceleration, float deltaTime) {

        float deltaX, deltaY, deltaZ;
        if (!isInitialized()) {

            float time = getTime();
            if (time < getDelay()) {
                setTime(time + deltaTime);
                float externalSpeedX = getExternalSpeedX() + externalAcceleration[0] * deltaTime;
                float externalSpeedY = getExternalSpeedY() + externalAcceleration[1] * deltaTime;
                float externalSpeedZ = getExternalSpeedZ() + externalAcceleration[2] * deltaTime;
                setExternalSpeedX(externalSpeedX);
                setExternalSpeedY(externalSpeedY);
                setExternalSpeedZ(externalSpeedZ);
                setPosition(getPosX() + externalSpeedX * deltaTime, getPosY() + externalSpeedY * deltaTime,
                        getPosZ() + externalSpeedZ * deltaTime);

                if (isRotatedForward()) {
                    if (externalSpeedX != 0 || externalSpeedY != 0) {
                        double rotation = Math.atan2(externalSpeedY, externalSpeedX);
                        setRotation((float) -(rotation * 180.0 / Math.PI) + 90);
                    }
                }

                return;
            }

            setTime(0);
            setInitialized(true);
            deltaX = getDeltaX() + getExternalSpeedX();
            deltaY = getDeltaY() + getExternalSpeedY();
            deltaZ = getDeltaZ() + getExternalSpeedZ();
            setDeltaX(deltaX);
            setDeltaY(deltaY);
            setDeltaZ(deltaZ);

        } else {
            deltaX = getDeltaX();
            deltaY = getDeltaY();
            deltaZ = getDeltaZ();
        }

        setTime(getTime() + deltaTime);
        float time = getTime();

        float posX = getPosX();
        float posY = getPosY();
        float posZ = getPosZ();
        float gravityCenterX = getGravityCenterX();
        float gravityCenterY = getGravityCenterY();
        float gravityCenterZ = getGravityCenterZ();
        float radialDirX, radialDirY, radialDirZ;
        if (gravityCenterX == posX && gravityCenterY == posY && gravityCenterZ == posZ) {

            double initialYaw = -Math.atan2(deltaY, deltaX);
            double initialPitch = Math.atan2(deltaZ, Math.sqrt(deltaX * deltaX + deltaY * deltaY));

            // Get radial direction from initial direction
            double cosPitch = Math.cos(initialPitch);
            radialDirX = (float) (Math.cos(initialYaw) * cosPitch);
            radialDirY = (float) -(Math.sin(initialYaw) * cosPitch);
            radialDirZ = (float) Math.sin(initialPitch);

        } else {

            // Calculate radial direction
            radialDirX = gravityCenterX - posX;
            radialDirY = gravityCenterY - posY;
            radialDirZ = gravityCenterZ - posZ;

            // Calculate distance to origin
            float distance = (float) Math
                    .sqrt(radialDirX * radialDirX + radialDirY * radialDirY + radialDirZ * radialDirZ);

            // Normalize radial direction
            radialDirX = radialDirX / distance;
            radialDirY = radialDirY / distance;
            radialDirZ = radialDirZ / distance;
        }

        // Get tangential direction
        float tangentialDirX = -radialDirY;
        float tangentialDirY = radialDirX;

        // Get acceleration sums of radial and tangential accelerations
        float radialAcc = getRadialAcceleration();
        float tangentialAcc = getTangentialAcceleration();
        float accSumX = radialDirX * radialAcc + tangentialDirX * tangentialAcc;
        float accSumY = radialDirY * radialAcc + tangentialDirY * tangentialAcc;
        float accSumZ = radialDirZ * radialAcc;

        // Update velocity
        deltaX += (accSumX + externalAcceleration[0] + getAccelerationX()) * deltaTime;
        deltaY += (accSumY + externalAcceleration[1] + getAccelerationY()) * deltaTime;
        deltaZ += (accSumZ + externalAcceleration[2] + getAccelerationZ()) * deltaTime;
        setDeltaX(deltaX);
        setDeltaY(deltaY);
        setDeltaZ(deltaZ);

        // Update velocity factor
        float progress = time / getLife();
        float velocityFactor = (1f - progress) + progress * getEndSpeedFactor();

        // Update position
        float velocityFactorTimesDeltaTime = velocityFactor * deltaTime;
        setPosition(posX + deltaX * velocityFactorTimesDeltaTime, posY + deltaY * velocityFactorTimesDeltaTime,
                posZ + deltaZ * velocityFactorTimesDeltaTime);

        // Update color
        setColor(getColorR() + getDeltaColorR() * deltaTime, getColorG() + getDeltaColorG() * deltaTime,
                getColorB() + getDeltaColorB() * deltaTime, getColorA() + getDeltaColorA() * deltaTime);

        // Update scale
        setScale(getScaleX() + getDeltaScaleX() * deltaTime, getScaleY() + getDeltaScaleY() * deltaTime);

        // Update rotation
        if (isRotatedForward()) {
            if (deltaX != 0 || deltaY != 0) {
                double rotation = Math.atan2(deltaY, deltaX);
                setRotation((float) -(rotation * 180.0 / Math.PI) + 90);
            }
        } else {
            float rotationSpeed = getRotationSpeed() + getRotationAcceleration() * deltaTime;
            setRotationSpeed(rotationSpeed);
            setRotation(getRotation() + rotationSpeed * deltaTime);
        }
    }
}