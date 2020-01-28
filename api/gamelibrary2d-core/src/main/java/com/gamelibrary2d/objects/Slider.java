package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.renderers.Renderer;

public abstract class Slider extends AbstractFocusableObject {

    private final InteractiveObject lever;

    private float valueX;
    private float valueY;
    private float minValueX;
    private float minValueY;
    private float maxValueX = 100;
    private float maxValueY;
    private boolean verticalSlider;
    private boolean horizontalSlider = true;
    private Rectangle bounds;

    protected Slider() {
        lever = new InteractiveObject();
        lever.setListeningToMouseClickEvents(true);
        setListeningToMouseClickEvents(true);
        setListeningToMouseDragEvents(true);
    }

    public Renderer getLeverRenderer() {
        return lever.getRenderer();
    }

    public void setLeverRenderer(Renderer renderer) {
        lever.setRenderer(renderer);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isVerticalSlider() {
        return verticalSlider;
    }

    public void setVerticalSlider(boolean verticalSlider) {
        this.verticalSlider = verticalSlider;
    }

    public boolean isHorizontalSlider() {
        return horizontalSlider;
    }

    public void setHorizontalSlider(boolean horizontalSlider) {
        this.horizontalSlider = horizontalSlider;
    }

    public float getMinValueX() {
        return minValueX;
    }

    public void setMinValueX(float minValueX) {
        this.minValueX = minValueX;
    }

    public float getMaxValueX() {
        return maxValueX;
    }

    public void setMaxValueX(float maxValueX) {
        this.maxValueX = maxValueX;
    }

    public float getMinValueY() {
        return minValueY;
    }

    public void setMinValueY(float minValueY) {
        this.minValueY = minValueY;
    }

    public float getMaxValueY() {
        return maxValueY;
    }

    public void setMaxValueY(float maxValueY) {
        this.maxValueY = maxValueY;
    }

    @Override
    protected boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        if (lever.mouseButtonDownEvent(button, mods, projectedX, projectedY)) {
            onDragStart(projectedX, projectedY);
            return true;
        }

        return false;
    }

    public float getLeverPosX() {
        return lever.getPosition().getX();
    }

    public float getLeverPosY() {
        return lever.getPosition().getY();
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public void setLeverPos(float posX, float posY, boolean triggerSliderValueChanged) {
        posX = horizontalSlider ? getValueWithinInterval(posX, bounds.getXMin(), bounds.getXMax()) : 0;
        posY = verticalSlider ? getValueWithinInterval(posY, bounds.getYMin(), bounds.getYMax()) : 0;
        lever.getPosition().set(posX, posY);
        valueX = getActualValue(posX, bounds.getXMin(), bounds.getXMax(), minValueX, maxValueX);
        valueY = getActualValue(posY, bounds.getYMin(), bounds.getYMax(), minValueY, maxValueY);
        if (triggerSliderValueChanged) {
            sliderValueChanged(posX, posY, valueX, valueY);
        }
    }

    public void setValue(float valueX, float valueY, boolean triggerSliderValueChanged) {
        valueX = horizontalSlider ? getValueWithinInterval(valueX, minValueX, maxValueX) : 0;
        valueY = verticalSlider ? getValueWithinInterval(valueY, minValueY, maxValueY) : 0;
        float posX = getActualValue(valueX, minValueX, maxValueX, bounds.getXMin(), bounds.getXMax());
        float posY = getActualValue(valueY, minValueY, maxValueY, bounds.getYMin(), bounds.getYMax());

        this.valueX = valueX;
        this.valueY = valueY;

        lever.getPosition().set(posX, posY);
        if (triggerSliderValueChanged) {
            sliderValueChanged(posX, posY, valueX, valueY);
        }
    }

    private float getValueWithinInterval(float value, float min, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private float getActualValue(float value, float min, float max, float actualMin, float actualMax) {
        return (value - min) * ((actualMax - actualMin) / (max - min));
    }

    @Override
    protected boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        if (drag) {
            setLeverPos(projectedX, projectedY, true);
        }
        return true;
    }

    @Override
    protected void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        if (lever.mouseButtonReleaseEvent(button, mods, projectedX, projectedY)) {
            onDragEnd(projectedX, projectedY);
        }
    }

    @Override
    protected void onRenderProjected(float alpha) {
        lever.render(alpha);
    }

    protected abstract void onDragStart(float posX, float posY);

    protected abstract void sliderValueChanged(float x, float y, float valueX, float valueY);

    protected abstract void onDragEnd(float posX, float posY);
}