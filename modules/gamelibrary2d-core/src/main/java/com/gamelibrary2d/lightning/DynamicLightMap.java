package com.gamelibrary2d.lightning;

/**
 * Defines light that can be dynamically added on each update. All light is
 * cleared in the beginning of the update, when {@link LightMap#prepare prepare}
 * is invoked. The cells in the {@link DynamicLightMap} matches those of the
 * {@link LightRenderer} it is attached to.
 *
 * @author Christoffer Rydberg
 */
public interface DynamicLightMap extends LightMap {

    /**
     * @return The maximum spread range of an added light source.
     */
    int getRange();

    /**
     * Adds a light source at the specified grid coordinates. The coordinates must
     * be within the grid setup by invoking {@link #prepare}.
     *
     * @param col   The grid column.
     * @param row   The grid row.
     * @param light The strength of the light source.
     */
    void add(int col, int row, float light);

    /**
     * Adds a light source and interpolates among surrounding grid coordinates. The
     * coordinates must be within the area setup by invoking {@link #prepare}.
     *
     * @param col   The grid column.
     * @param row   The grid row.
     * @param light The strength of the light source.
     * @return True if light was added, false otherwise.
     */
    boolean addInterpolated(float col, float row, float light);

}
