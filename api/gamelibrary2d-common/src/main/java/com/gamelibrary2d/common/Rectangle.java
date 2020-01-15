package com.gamelibrary2d.common;

import java.util.Objects;

public class Rectangle {

    /**
     * Defines the min/max value for the X and Y coordinates of the rectangle bounds.
     * It is smaller than {@link Float#MAX_VALUE} in order to avoid float overflows when performing rectangle operations.
     */
    public static final float INFINITE_VALUE = Float.MAX_VALUE / 100;

    /**
     * Used to represent a rectangle of empty size, e.g. instead of using null for something without bounds.
     */
    public static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);

    /**
     * Used to represent a rectangle of infinite size.
     */
    public static final Rectangle INFINITE = new Rectangle(-INFINITE_VALUE, -INFINITE_VALUE, INFINITE_VALUE,
            INFINITE_VALUE);

    private final float xMin, yMin, xMax, yMax;

    /**
     * Creates a new instance of {@link Rectangle} with the specified bounds. Note that each field is forcefully
     * restricted by the {@link Rectangle#INFINITE_VALUE}. As such, exceeding this value will shrink the rectangle
     * without maintaining the aspect ratio. The infinite value is typically used to indicate an
     * {@link Rectangle#INFINITE infintie rectangle}.
     *
     * @param xMin The x-value of the lower left corner.
     * @param yMin The y-value of the lower left corner.
     * @param xMax The x-value of the upper right corner.
     * @param yMax The y-value of the upper right corner.
     */
    public Rectangle(float xMin, float yMin, float xMax, float yMax) {
        this.xMin = getValidValue(xMin);
        this.yMin = getValidValue(yMin);
        this.xMax = getValidValue(xMax);
        this.yMax = getValidValue(yMax);
    }

    public Rectangle(Rectangle rect) {
        this(rect.getXMin(), rect.getYMin(), rect.getXMax(), rect.getYMax());
    }

    public static Rectangle fromBottomLeft(float width, float height) {
        return new Rectangle(0, 0, width, height);
    }

    public static Rectangle fromTopLeft(float width, float height) {
        return new Rectangle(0, -height, width, 0);
    }

    public static Rectangle fromTopRight(float width, float height) {
        return new Rectangle(-width, -height, 0, 0);
    }

    public static Rectangle fromBottomRight(float width, float height) {
        return new Rectangle(-width, 0, 0, height);
    }

    public static Rectangle centered(float width, float height) {
        return new Rectangle(-width / 2, -height / 2, width / 2, height / 2);
    }

    public static Rectangle centered(float width) {
        return centered(width, width);
    }

    private static float getInRange(float value, float min, float max) {
        var width = max - min;
        var dist = (value - min) / width;
        var distDecimals = dist - (int) dist;
        if (distDecimals < 0) {
            distDecimals += 1f;
        }
        return distDecimals * width + min;
    }

    private float getValidValue(float value) {
        return Math.max(-INFINITE_VALUE, Math.min(INFINITE_VALUE, value));
    }

    public Rectangle expandToPoint(Point p) {
        return expandToPoint(p.getX(), p.getY());
    }

    public Rectangle expandToPoint(float x, float y) {
        return isInside(x, y) ? this
                : new Rectangle(Math.min(xMin, x), Math.min(yMin, y), Math.max(xMax, x), Math.max(yMax, y));
    }

    public boolean isInside(Point p) {
        return isInside(p.getX(), p.getY());
    }

    public boolean isInside(float x, float y) {
        return !(x < xMin || y < yMin || x > xMax || y > yMax);
    }

    public boolean intersects(Rectangle rect) {
        return !(getXMin() > rect.getXMax() || getYMin() > rect.getYMax() || getXMax() < rect.getXMin()
                || getYMax() < rect.getYMin());
    }

    public Rectangle move(Point offset) {
        return move(offset.getX(), offset.getY());
    }

    public Rectangle move(float x, float y) {
        return new Rectangle(getXMin() + x, getYMin() + y, getXMax() + x, getYMax() + y);
    }

    public Rectangle center() {
        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;
        return new Rectangle(-halfWidth, -halfHeight, halfWidth, halfHeight);
    }

    public Rectangle resize(float scale) {
        return new Rectangle(xMin * scale, yMin * scale, xMax * scale, yMax * scale);
    }

    public Rectangle resize(float scaleX, float scaleY) {
        return new Rectangle(xMin * scaleX, yMin * scaleY, xMax * scaleX, yMax * scaleY);
    }

    public Rectangle resize(Point scale) {
        return resize(scale.getX(), scale.getY());
    }

    public Rectangle pad(float padding) {
        return pad(padding, padding);
    }

    public Rectangle pad(float horizontalPadding, float verticalPadding) {
        return pad(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    }

    public Rectangle pad(float xMinPadding, float yMinPadding, float xMaxPadding, float yMaxPadding) {
        return new Rectangle(getXMin() - xMinPadding, getYMin() - yMinPadding, getXMax() + xMaxPadding,
                getYMax() + yMaxPadding);
    }

    public Rectangle resize(float scaleX, float scaleY, float hotSpotX, float hotSpotY) {

        // Current Center point
        float centerX = xMin + getWidth() / 2;
        float centerY = yMin + getHeight() / 2;

        // New center point
        float newCenterX = centerX + (hotSpotX - centerX) / scaleX;
        float newCenterY = centerY + (hotSpotY - centerY) / scaleY;

        return new Rectangle(newCenterX - (getWidth() / 2) * scaleX, newCenterY - (getHeight() / 2) * scaleY,
                newCenterX + (getWidth() / 2) * scaleX, newCenterY + (getHeight() / 2) * scaleY);
    }

    /**
     * Rotates each corner of the rectangle and returns the bounds.
     *
     * @param rotation The rotation angle, in degrees.
     * @param centerX  The X-coordinate of the rotation's center point.
     * @param centerY  The Y-coordinate of the rotation's center point.
     * @return
     */
    public Rectangle getRotatedBounds(float rotation, float centerX, float centerY) {

        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMax = Float.MIN_VALUE;

        // Rotate lower left corner
        Point rotationPoint = new Point(getXMin(), getYMin());
        rotationPoint.rotate(rotation, centerX, centerY);
        xMin = Math.min(rotationPoint.getX(), xMin);
        xMax = Math.max(rotationPoint.getX(), xMax);
        yMin = Math.min(rotationPoint.getY(), yMin);
        yMax = Math.max(rotationPoint.getY(), yMax);

        // Rotate upper left corner
        rotationPoint.set(getXMin(), getYMax());
        rotationPoint.rotate(rotation, centerX, centerY);
        xMin = Math.min(rotationPoint.getX(), xMin);
        xMax = Math.max(rotationPoint.getX(), xMax);
        yMin = Math.min(rotationPoint.getY(), yMin);
        yMax = Math.max(rotationPoint.getY(), yMax);

        // Rotate upper right corner
        rotationPoint.set(getXMax(), getYMax());
        rotationPoint.rotate(rotation, centerX, centerY);
        xMin = Math.min(rotationPoint.getX(), xMin);
        xMax = Math.max(rotationPoint.getX(), xMax);
        yMin = Math.min(rotationPoint.getY(), yMin);
        yMax = Math.max(rotationPoint.getY(), yMax);

        // Rotate lower right corner
        rotationPoint.set(getXMax(), getYMin());
        rotationPoint.rotate(rotation, centerX, centerY);
        xMin = Math.min(rotationPoint.getX(), xMin);
        xMax = Math.max(rotationPoint.getX(), xMax);
        yMin = Math.min(rotationPoint.getY(), yMin);
        yMax = Math.max(rotationPoint.getY(), yMax);

        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    public float getArea() {
        return getWidth() * getHeight();
    }

    public float getWidth() {
        return xMax - xMin;
    }

    public float getHeight() {
        return yMax - yMin;
    }

    public float getXMin() {
        return xMin;
    }

    public float getYMin() {
        return yMin;
    }

    public float getXMax() {
        return xMax;
    }

    public float getYMax() {
        return yMax;
    }

    public Point getCenter() {
        return new Point(xMin + getWidth() / 2, yMin + getHeight() / 2);
    }

    public void wrap(Point p) {
        float x = getInRange(p.getX(), xMin, xMax);
        float y = getInRange(p.getY(), yMin, yMax);
        p.set(x, y);
        p.add(xMin, yMin);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Rectangle) {
            Rectangle other = (Rectangle) obj;
            return other.xMin == xMin && other.yMin == yMin && other.xMax == xMax && other.yMax == yMax;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xMin, yMin, xMax, yMax);
    }
}