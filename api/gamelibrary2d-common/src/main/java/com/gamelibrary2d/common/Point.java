package com.gamelibrary2d.common;

public class Point {

    private float x;
    private float y;

    public Point() {
    }

    public Point(float x, float y) {
        set(x, y);
    }

    public Point(Point p) {
        set(p);
    }

    public static Point id() {
        return new Point(1f, 1f);
    }

    private static double getAngleRadian(float x, float y) {
        return Math.atan2(x, y);
    }

    private static float toDegrees(double radians) {
        return (float) (radians * (180 / Math.PI));
    }

    private static float getLength(float x, float y) {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void set(Point p) {
        set(p.x, p.y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void lerp(float x, float y, float alpha) {
        this.x = this.x * (1f - alpha) + x * alpha;
        this.y = this.y * (1f - alpha) + y * alpha;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void add(Point p) {
        x += p.x;
        y += p.y;
    }

    public void subtract(Point p) {
        subtract(p.getX(), p.getY());
    }

    public void subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
    }

    public void multiply(float factor) {
        x *= factor;
        y *= factor;
    }

    public void divide(float divisor) {
        x /= divisor;
        y /= divisor;
    }

    public void normalize() {
        if (x != 0 || y != 0) {
            divide(getLength());
        }
    }

    public double dot(float x, float y) {
        return this.x * x + this.y * y;
    }

    /**
     * Rotates the point clockwise.
     *
     * @param angle The angle in degrees.
     */
    public void rotate(float angle) {
        rotateRadian(angle * Math.PI / 180);
    }

    /**
     * Rotates the point clockwise around the given center point.
     *
     * @param angle   The angle in degrees.
     * @param centerX The X-coordinate of the center point.
     * @param centerY The Y-coordinate of the center point.
     */
    public void rotate(float angle, float centerX, float centerY) {
        rotateRadian(angle * Math.PI / 180, centerX, centerY);
    }

    /**
     * Rotates the point clockwise around the given center point.
     *
     * @param angle   The angle in radians.
     * @param centerX The X-coordinate of the center point.
     * @param centerY The Y-coordinate of the center point.
     */
    public void rotateRadian(double angle, float centerX, float centerY) {

        // Translate point to origin
        x -= centerX;
        y -= centerY;

        // Rotate point
        rotateRadian(angle);

        // Translate point back
        x += centerX;
        y += centerY;
    }

    /**
     * Rotates the point clockwise.
     *
     * @param angle The angle in radians.
     */
    public void rotateRadian(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        float tmp = (float) (x * cos + y * sin);
        y = (float) (-x * sin + y * cos);
        x = tmp;
    }

    /**
     * Gets the angle from the positive y-axis in the interval (-PI, PI]. The angle
     * increases clockwise.
     */
    public double getAngleRadian() {
        return getAngleRadian(x, y);
    }

    /**
     * Gets the angle from the positive y-axis in the interval (-180, 180]. The
     * angle increases clockwise.
     */
    public float getAngleDegrees() {
        return (float) (getAngleRadian() * (180 / Math.PI));
    }

    public float getDirectionDegrees(Point p) {
        return toDegrees(getDirectionRadians(p));
    }

    public float getDirectionDegrees(float x, float y) {
        return toDegrees(getDirectionRadians(x, y));
    }

    public double getDirectionRadians(Point p) {
        return getAngleRadian(p.x - x, p.y - y);
    }

    public double getDirectionRadians(float x, float y) {
        return getAngleRadian(x - this.x, y - this.y);
    }

    public float getSmallestAngleDegrees(Point p) {
        return (float) (getSmallestAngleRadian(p) * (180 / Math.PI));
    }

    public float getSmallestAngleDegrees(float otherX, float otherY) {
        return toDegrees(getSmallestAngleRadian(otherX, otherY));
    }

    public double getSmallestAngleRadian(Point p) {
        return getSmallestAngleRadian(p.x, p.y);
    }

    /**
     * Returns the smallest angle from this vector to another vector.
     *
     * @param x The x-coordinate of the other vector.
     * @param y The y-coordinate of the other vector.
     * @return The angle in radians
     */
    public double getSmallestAngleRadian(float x, float y) {
        double result = getAngleRadian(x, y) - getAngleRadian();
        if (result > Math.PI)
            return result - Math.PI * 2;
        if (result < -Math.PI)
            return result + Math.PI * 2;
        return result;
    }

    public float getLength() {
        return getLength(x, y);
    }

    public float getDistance(Point other) {
        return getLength(x - other.x, y - other.y);
    }

    public float getDistance(float x, float y) {
        return getLength(this.x - x, this.y - y);
    }
}