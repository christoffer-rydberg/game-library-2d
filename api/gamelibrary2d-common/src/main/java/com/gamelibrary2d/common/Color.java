package com.gamelibrary2d.common;

public class Color {

    public static final Color EMPTY = new Color(0, 0, 0, 0);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1, 1, 1);
    public static final Color RED = new Color(1, 0, 0);
    public static final Color LIGHT_CORAL = new Color(240f / 255f, 128f / 255f, 128f / 255f);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color LIGHT_BLUE = new Color(173f / 255f, 216f / 255f, 230f / 255f);
    public static final Color SILVER = new Color(192f / 255f, 192f / 255f, 192f / 255f);
    public static final Color SAND = new Color(194f / 255f, 178f / 255f, 128f / 255f);
    public static final Color GOLD = new Color(255f / 255f, 215f / 255f, 0f);
    public static final Color PINK = new Color(255f / 255f, 192 / 255f, 203 / 255f);
    public static final Color YELLOW = new Color(255f / 255f, 255f / 255f, 0);
    public static final Color LIGHT_YELLOW = new Color(255f / 255f, 255f / 255f, 204f / 255f);
    public static final Color ORANGE = new Color(255f / 255f, 165f / 255f, 0);
    public static final Color BROWN = new Color(139f / 255f, 69f / 255f, 19f / 255f);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color LAVENDER = new Color(230f/255f, 230f/255f, 250f/255f);

    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color add(Color c) {
        return new Color(r + c.r, g + c.g, b + c.b, a + c.a);
    }

    public Color subtract(Color c) {
        return new Color(r - c.r, g - c.g, b - c.b, a - c.a);
    }

    public Color multiply(Color c) {
        return new Color(r * c.r, g * c.g, b * c.b, a * c.a);
    }

    public Color multiply(float factor) {
        return new Color(r * factor, g * factor, b * factor, a * factor);
    }

    public Color divide(Color c) {
        return new Color(r / c.r, g / c.g, b / c.b, a / c.a);
    }

    public Color divide(float divisor) {
        return new Color(r / divisor, g / divisor, b / divisor, a / divisor);
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }
}