package com.gamelibrary2d.lightning;

/**
 * The purpose of a {@link LightMap} is to spread and store light. Different light
 * maps can manage different types of light, and be combined and renderer by a
 * {@link LightRenderer}.
 */
public interface LightMap {

    /**
     * Prepares the {@link LightRenderer} to store light in the area defined by the
     * specified game cell size and column/row interval. This method is invoked upon
     * calling {@link LightRenderer#prepare}.
     */
    void prepare(int alphaMapCols, int alphaMapRows, int gameColOffset, int gameColSpan, int gameRowOffset, int gameRowSpan);

    /**
     * Applies the light map to the specified {@link LightRenderer}. This method is
     * invoked upon calling {@link LightRenderer#apply}.
     */
    void apply(LightRenderer renderer);
}