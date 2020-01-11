package com.gamelibrary2d.lightning;

/**
 * Defines a light map for tile-based games, where each cell represents the
 * light of a game tile. There are two types of light: "regular light" and
 * sunlight. What differs sunlight from regular light is the
 * {@link #setSunlightFactor sunlight factor} which affects the strength of all
 * sunlight. This can, for example, be used to illustrate if it is day or night.
 *
 * @author Christoffer Rydberg
 */
public interface GameTileLightMap extends LightMap {

    /**
     * Loads initial light.
     */
    void initialize();

    /**
     * Gets the column of the specified light map index.
     *
     * @param index The light map index.
     */
    int getCol(int index);

    /**
     * Gets the row of the specified light map index.
     *
     * @param index The light map index.
     */
    int getRow(int index);

    /**
     * Gets the light strength of the cell at the specified index.
     *
     * @param index The light cell index.
     */
    int getLight(int index);

    /**
     * Gets the sunlight strength of the cell at the specified index.
     *
     * @param index The light cell index.
     */
    int getSunLight(int index);

    /**
     * Factor applied to all sunlight. Can be used to simulate day and night.
     */
    float getSunlightFactor();

    /**
     * Sets the {@link #getSunlightFactor() sunlight factor}.
     */
    void setSunlightFactor(float sunlightFactor);

    /**
     * Updates the light and light output of the tile. This method needs to be
     * called if the light of the tile has changed. If the transmission or
     * reflection factors of the tile has changed, you must do an update for both
     * sunlight and regular light (even if only one light type has changed).
     * Otherwise light from surrounding tiles will not be forwarded and reflected
     * correctly.
     *
     * @param col        The column of the tile.
     * @param row        The row of the tile.
     * @param light      The new light of the tile.
     * @param isSunlight True if the light is sunlight, false otherwise.
     */
    void updateCell(int col, int row, int light, boolean isSunlight);

    /**
     * Updates the light output of the tile. This method needs to be called if the
     * transmission factor of the tile has changed, but not its light.
     *
     * @param col The column of the tile.
     * @param row The row of the tile.
     */
    void updateCell(int col, int row);
}