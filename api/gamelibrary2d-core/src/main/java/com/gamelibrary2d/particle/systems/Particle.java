package com.gamelibrary2d.particle.systems;

public class Particle {
    private int index = -1;
    private int renderOffset;
    private int updateOffset;

    private ParticleRenderBuffer renderBuffer;
    private ParticleUpdateBuffer updateBuffer;

    Particle(ParticleRenderBuffer renderBuffer, ParticleUpdateBuffer updateBuffer, int index) {
        this.renderBuffer = renderBuffer;
        this.updateBuffer = updateBuffer;
        setIndex(index);
    }

    public float getTime() {
        return updateBuffer.getTime(updateOffset);
    }

    public void setTime(float time) {
        updateBuffer.setTime(updateOffset, time);
    }

    public float getCustom() {
        return updateBuffer.getCustom(updateOffset);
    }

    public void setCustom(float value) {
        updateBuffer.setCustom(updateOffset, value);
    }

    public float getDelay() {
        return updateBuffer.getDelay(updateOffset);
    }

    public void setDelay(float delay) {
        updateBuffer.setDelay(updateOffset, delay);
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

    public float getCenterX() {
        return updateBuffer.getCenterX(updateOffset);
    }

    public float getCenterY() {
        return updateBuffer.getCenterY(updateOffset);
    }

    public float getHorizontalAcceleration() {
        return updateBuffer.getHorizontalAcceleration(updateOffset);
    }

    public void setHorizontalAcceleration(float horizontalAcceleration) {
        updateBuffer.setHorizontalAcceleration(updateOffset, horizontalAcceleration);
    }

    public float getVerticalAcceleration() {
        return updateBuffer.getVerticalAcceleration(updateOffset);
    }

    public void setVerticalAcceleration(float verticalAcceleration) {
        updateBuffer.setVerticalAcceleration(updateOffset, verticalAcceleration);
    }

    public float getCentripetalAcceleration() {
        return updateBuffer.getCentripetalAcceleration(updateOffset);
    }

    public void setCentripetalAcceleration(float centripetalAcc) {
        updateBuffer.setCentripetalAcceleration(updateOffset, centripetalAcc);
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

    public float getScale() {
        return renderBuffer.getScale(renderOffset);
    }

    public void setScale(float scale) {
        renderBuffer.setScale(renderOffset, scale);
    }

    public float getDeltaScale() {
        return updateBuffer.getDeltaScale(updateOffset);
    }

    public void setDeltaScale(float deltaScale) {
        updateBuffer.setDeltaScale(updateOffset, deltaScale);
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
        return isInitialized() && getTime() >= getLife();
    }

    void setRenderBuffer(ParticleRenderBuffer renderBuffer) {
        this.renderBuffer = renderBuffer;
    }

    void setUpdateBuffer(ParticleUpdateBuffer updateBuffer) {
        this.updateBuffer = updateBuffer;
    }

    void setIndex(int index) {
        if (this.index != index) {
            this.index = index;
            renderOffset = index * renderBuffer.getStride();
            updateOffset = index * updateBuffer.getStride();
        }
    }

    public void setPosition(float x, float y) {
        renderBuffer.setPosX(renderOffset, x);
        renderBuffer.setPosY(renderOffset, y);
    }

    public void setCenter(float x, float y) {
        updateBuffer.setCenterX(updateOffset, x);
        updateBuffer.setCenterY(updateOffset, y);
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

    public void setVelocity(float deltaX, float deltaY) {
        updateBuffer.setDeltaX(updateOffset, deltaX);
        updateBuffer.setDeltaY(updateOffset, deltaY);
    }

    public float getEndSpeedFactor() {
        return updateBuffer.getEndSpeedFactor(updateOffset);
    }

    public void setEndSpeedFactor(float endSpeedFactor) {
        updateBuffer.setEndSpeedFactor(updateOffset, endSpeedFactor);
    }

    private boolean isInitialized() {
        return updateBuffer.isInitialized(updateOffset);
    }

    void setInitialized(boolean initialized) {
        updateBuffer.setInitialized(updateOffset, initialized);
    }

    private float getExternalSpeedX() {
        return updateBuffer.getExternalSpeedX(updateOffset);
    }

    void setExternalSpeedX(float externalSpeedX) {
        updateBuffer.setExternalSpeedX(updateOffset, externalSpeedX);
    }

    private float getExternalSpeedY() {
        return updateBuffer.getExternalSpeedY(updateOffset);
    }

    void setExternalSpeedY(float externalSpeedY) {
        updateBuffer.setExternalSpeedY(updateOffset, externalSpeedY);
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

    void update(float[] externalAcceleration, float deltaTime) {
        if (!isInitialized()) {
            float time = getTime();
            if (time < getDelay()) {
                setTime(time + deltaTime);
                float externalSpeedX = getExternalSpeedX() + externalAcceleration[0] * deltaTime;
                float externalSpeedY = getExternalSpeedY() + externalAcceleration[1] * deltaTime;
                setExternalSpeedX(externalSpeedX);
                setExternalSpeedY(externalSpeedY);
                setPosition(getPosX() + externalSpeedX * deltaTime, getPosY() + externalSpeedY * deltaTime);

                if (isRotatedForward()) {
                    if (externalSpeedX != 0 || externalSpeedY != 0) {
                        double rotation = Math.atan2(externalSpeedY, externalSpeedX);
                        setRotation((float) -(rotation * 180.0 / Math.PI) + 90);
                    }
                }

                return;
            }

            setTime(0);
            setDeltaX(getDeltaX() + getExternalSpeedX());
            setDeltaY(getDeltaY() + getExternalSpeedY());
            setInitialized(true);
        }

        float time = getTime() + deltaTime;
        setTime(time);

        float posX = getPosX();
        float posY = getPosY();
        float centerX = getCenterX();
        float centerY = getCenterY();

        float deltaX = getDeltaX();
        float deltaY = getDeltaY();

        float centripetalDirX, centripetalDirY;
        if (centerX == posX && centerY == posY) {
            double direction = -Math.atan2(deltaY, deltaX);
            centripetalDirX = (float) Math.cos(direction);
            centripetalDirY = (float) -Math.sin(direction);
        } else {
            centripetalDirX = centerX - posX;
            centripetalDirY = centerY - posY;
            float distance = (float) Math.sqrt(centripetalDirX * centripetalDirX + centripetalDirY * centripetalDirY);
            centripetalDirX /= distance;
            centripetalDirY /= distance;
        }

        // Get tangential direction
        float tangentialDirX = -centripetalDirY;
        float tangentialDirY = centripetalDirX;

        // Get acceleration sums of centripetal and tangential accelerations
        float centripetalAcc = getCentripetalAcceleration();
        float tangentialAcc = getTangentialAcceleration();
        float accSumX = centripetalDirX * centripetalAcc + tangentialDirX * tangentialAcc;
        float accSumY = centripetalDirY * centripetalAcc + tangentialDirY * tangentialAcc;

        // Update velocity
        deltaX += (accSumX + externalAcceleration[0] + getHorizontalAcceleration()) * deltaTime;
        deltaY += (accSumY + externalAcceleration[1] + getVerticalAcceleration()) * deltaTime;
        setDeltaX(deltaX);
        setDeltaY(deltaY);

        // Update velocity factor
        float progress = time / getLife();
        float velocityFactor = (1f - progress) + progress * getEndSpeedFactor();

        // Update position
        float velocityFactorTimesDeltaTime = velocityFactor * deltaTime;
        setPosition(posX + deltaX * velocityFactorTimesDeltaTime, posY + deltaY * velocityFactorTimesDeltaTime);

        // Update scale
        setScale(getScale() + getDeltaScale() * deltaTime);

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

        // Update color
        setColor(getColorR() + getDeltaColorR() * deltaTime, getColorG() + getDeltaColorG() * deltaTime,
                getColorB() + getDeltaColorB() * deltaTime, getColorA() + getDeltaColorA() * deltaTime);
    }
}