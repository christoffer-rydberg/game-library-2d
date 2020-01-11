package com.gamelibrary2d.lightning;

/**
 * Represents a matrix structure used to decide the strength of spread light
 * depending on the distance from the source.
 *
 * @author Christoffer Rydberg
 */
public interface LightSpreadMatrix {

    /**
     * @return The maximum range.
     */
    int getRange();

    /**
     * Multiply this factor to the strength of the light source in order to get the
     * light strength at the specified distance from the source.
     *
     * @param distX The distance along the x-axis.
     * @param distY The distance along the y-axis.
     */
    float getLightStrengthFactor(int distX, int distY);

}
