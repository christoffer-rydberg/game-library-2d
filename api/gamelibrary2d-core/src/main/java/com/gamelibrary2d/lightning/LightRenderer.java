package com.gamelibrary2d.lightning;

import com.gamelibrary2d.framework.Renderable;

/**
 * The name "LightRenderer" is a bit misleading, since it does not actually
 * render light. It is more precise to say that the {@link LightRenderer}
 * renders darkness. This is done by creating a grid of light values. The grid
 * represents the light in the current visible game area. Upon rendering, the
 * light values are interpolated and used to determine a dark semi-transparent
 * color for each pixel. This is done efficiently using shaders. No light
 * results in a fully opaque color and maximum light results in a fully
 * transparent color.
 */
public interface LightRenderer extends Renderable {

    /**
     * The number of columns in the light grid.
     */
    int getCols();

    /**
     * The number of rows in the light grid.
     */
    int getRows();

    /**
     * Adds additional light to the cell specified by the index. Indices are used
     * instead of rows and columns to avoid unnecessary arithmetic operations (since
     * the underlying data structure is a buffer). The index of a specific row and
     * column is computed as: row * {@link #getCols() cols} + column.
     *
     * @param index The index of the light cell.
     * @param light The light strength.
     */
    void addLight(int index, float light);

    /**
     * Light maps are used to add light to the {@link LightRenderer}. The light of
     * each light map will be combined when {@link #apply applied} to the
     * {@link LightRenderer}.
     *
     * @param lightMap The light map.
     */
    void addLightMap(LightMap lightMap);

    /**
     * Removes an added light map.
     */
    void removeLightMap(LightMap lightMap);

    /**
     * Removes all added light maps.
     */
    void clearLightMaps();

    /**
     * For docs: {@link #prepare(float, float, int, int, int, int) see overload}
     *
     * @param gameCellWidth  The width of a cell in the game grid.
     * @param gameCellHeight The height of a cell in the game grid.
     */
    void prepare(float gameCellWidth, float gameCellHeight);

    /**
     * For docs: {@link #prepare(float, float, int, int, int, int) see overload}
     *
     * @param gameCellWidth  The width of a cell in the game grid.
     * @param gameCellHeight The height of a cell in the game grid.
     * @param colOffset      The column offset.
     * @param rowOffset      The row offset.
     */
    void prepare(float gameCellWidth, float gameCellHeight, int colOffset, int rowOffset);

    /**
     * <p>
     * Prepares the {@link LightRenderer} to render light in the area defined by the
     * specified game cell size and column/row interval. In a tiled-based game, the
     * game cell size matches the size of a tile. Even if the game in itself is not
     * tile-based, the light renderer is, which is why a game cell size must be
     * decided. The game cell size determines the distance between light nodes, as
     * each game cell will be affected by 9 light cells.
     * </p>
     * <p>
     * This method must be invoked on each update as soon as the view area has been
     * decided, but before any light is added. Light is added by invoking
     * {@link #addLight}. Note that added {@link LightMap light maps} will add light
     * when {@link #apply} is invoked. However, the prepare-method will also invoke
     * the {@link LightMap#prepare} method (and possibly
     * {@link LightMap#onReallocate}) for all added light maps. It is equally
     * important for most light maps that this is done in the beginning of the
     * update, before any light values are changed. Because of this, it is important
     * to respect the following turn order during an update:
     * </p>
     *
     * <p>
     * 1. Decide the view area.
     * </p>
     * <p>
     * 2. Invoke this method.
     * </p>
     * <p>
     * 3. Adjust light.
     * </p>
     * <p>
     * 4. Invoke {@link #apply}.
     * </p>
     *
     * @param gameCellWidth  The width of a cell in the game grid.
     * @param gameCellHeight The height of a cell in the game grid.
     * @param colOffset      The column offset.
     * @param colSpan        The column span.
     * @param rowOffset      The row offset.
     * @param rowSpan        The row span.
     */
    void prepare(float gameCellWidth, float gameCellHeight, int colOffset, int colSpan, int rowOffset, int rowSpan);

    /**
     * Applies light from all added {@link LightMap light maps}.
     */
    void apply();

    /**
     * Same as calling {@link #prepare(float, float, int, int, int, int) prepare} with the same arguments as before.
     */
    void reset();

    /**
     * Renders the light grid.
     *
     * @param alpha The opacity factor which will be multiplied to the opacity of
     *              the rendered darkness.
     */
    @Override
    void render(float alpha);
}
