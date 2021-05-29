package com.gamelibrary2d.common;

public class FloatUtils {

    private static final float PI = (float) Math.PI;

    /**
     * Returns the floor modulus of the float arguments.
     */
    public static float floorMod(float x, float y) {
        return (x - (float) Math.floor(x / y) * y);
    }

    /**
     * Normalizes an angle to fit in the interval (-180, 180]
     *
     * @param degrees The angle in degrees.
     */
    public static float normalizeDegrees(float degrees) {
        float wrapped = floorMod(degrees, 360f);
        if (wrapped > 180f) {
            return wrapped - 360f;
        } else if (wrapped <= -180f) {
            return wrapped + 360f;
        } else {
            return wrapped;
        }
    }

    /**
     * Normalizes an angle to fit in the interval (-{@link #PI}, {@link #PI}]
     *
     * @param radians The angle in radians.
     */
    public static float normalizeRadians(float radians) {
        final float doublePi = 2 * PI;
        float wrapped = floorMod(radians, doublePi);
        if (wrapped > PI) {
            return wrapped - doublePi;
        } else if (wrapped <= -PI) {
            return wrapped + doublePi;
        } else {
            return wrapped;
        }
    }

}
