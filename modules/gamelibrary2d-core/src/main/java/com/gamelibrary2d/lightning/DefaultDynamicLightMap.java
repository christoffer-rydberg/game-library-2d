package com.gamelibrary2d.lightning;

import java.util.Arrays;

public class DefaultDynamicLightMap implements DynamicLightMap {

    /**
     * Holds all added light sources.
     */
    private InternalDynamicLight dynamicLight;

    /**
     * Matrix determining how light is spread (factors in each cell)
     */
    private LightSpreadMatrix lightSpreadMatrix;

    /**
     * Buffer used to store the spread light.
     */
    private float[] spreadLightBuffer;

    /**
     * The number of columns for the dynamic light
     */
    private int cols;

    /**
     * The number of rows for the dynamic light
     */
    private int rows;

    private int gameColOffset;
    private int gameColSpan;
    private int gameRowOffset;
    private int gameRowSpan;

    public DefaultDynamicLightMap(LightSpreadMatrix lightSpreadMatrix) {
        this.lightSpreadMatrix = lightSpreadMatrix;
    }

    @Override
    public int getRange() {
        return lightSpreadMatrix.getRange();
    }

    private int getIndex(int col, int row) {
        return row * cols + col;
    }

    @Override
    public void prepare(int alphaMapCols, int alphaMapRows, int gameColOffset, int gameColSpan, int gameRowOffset, int gameRowSpan) {
        if (this.cols != alphaMapCols || this.rows != alphaMapRows) {
            this.cols = alphaMapCols;
            this.rows = alphaMapRows;
            spreadLightBuffer = new float[alphaMapCols * alphaMapRows];
            dynamicLight = new InternalDynamicLight(alphaMapCols, alphaMapRows, lightSpreadMatrix.getRange());
        }
        this.gameColOffset = gameColOffset;
        this.gameColSpan = gameColSpan;
        this.gameRowOffset = gameRowOffset;
        this.gameRowSpan = gameRowSpan;
    }

    @Override
    public void add(int col, int row, float light) {
        dynamicLight.add(col, row, light);
    }

    @Override
    public boolean addInterpolated(float col, float row, float light) {
        final int lightNodeFrequency = 2;

        // Get light grid coordinates:
        float lightCol = col * lightNodeFrequency - gameColOffset * lightNodeFrequency;
        float lightRow = row * lightNodeFrequency - gameRowOffset * lightNodeFrequency;

        int dynamicLightRange = getRange();

        // Get left affected column
        int lightCol0 = (int) (Math.floor(lightCol));
        boolean rightOfScreen = lightCol0 >= gameColSpan * lightNodeFrequency + dynamicLightRange;
        if (rightOfScreen)
            return false;

        // Get right affected column
        int lightCol1 = lightCol0 + 1;
        boolean leftOfScreen = lightCol1 < -dynamicLightRange;
        if (leftOfScreen)
            return false;

        // Get top affected row
        int lightRow0 = (int) (Math.floor(lightRow));
        boolean belowScreen = lightRow0 > gameRowSpan * lightNodeFrequency + dynamicLightRange;
        if (belowScreen)
            return false;

        // Get bottom affected row
        int lightRow1 = lightRow0 + 1;
        boolean aboveScreen = lightRow1 < -dynamicLightRange;
        if (aboveScreen)
            return false;

        // The horizontal/vertical distance from the light source to the column/row
        // axes:
        float col0DistSquared = (float) Math.pow(Math.abs(lightCol - lightCol0), 2);
        float row0DistSquared = (float) Math.pow(Math.abs(lightRow - lightRow0), 2);
        float col1DistSquared = (float) Math.pow(Math.abs(lightCol - lightCol1), 2);
        float row1DistSquared = (float) Math.pow(Math.abs(lightRow - lightRow1), 2);

        // The distance from the light source to each corner:
        float dist00 = (float) Math.sqrt(col0DistSquared + row0DistSquared);
        float dist10 = (float) Math.sqrt(col1DistSquared + row0DistSquared);
        float dist01 = (float) Math.sqrt(col0DistSquared + row1DistSquared);
        float dist11 = (float) Math.sqrt(col1DistSquared + row1DistSquared);

        // Distance factors used to determine light strength for each corner:
        float distFactor00 = 1f - Math.min(1, dist00);
        float distFactor10 = 1f - Math.min(1, dist10);
        float distFactor01 = 1f - Math.min(1, dist01);
        float distFactor11 = 1f - Math.min(1, dist11);

        // Apply the (normalized) distance factors to the light source for each corner
        float distFactorSum = distFactor00 + distFactor10 + distFactor01 + distFactor11;
        float light00 = (distFactor00 / distFactorSum) * light;
        float light10 = (distFactor10 / distFactorSum) * light;
        float light11 = (distFactor11 / distFactorSum) * light;
        float light01 = (distFactor01 / distFactorSum) * light;

        // Add the dynamic dynamic light to each corner
        if (light00 > 0)
            add(lightCol0, lightRow0, light00);
        if (light10 > 0)
            add(lightCol1, lightRow0, light10);
        if (light11 > 0)
            add(lightCol1, lightRow1, light11);
        if (light01 > 0)
            add(lightCol0, lightRow1, light01);

        return true;
    }

    public void apply(LightRenderer renderer) {
        // Reset the light spread buffer
        Arrays.fill(spreadLightBuffer, 0);

        // Read all light sources
        while (dynamicLight.moveNext()) {

            // Get light source value
            float value = dynamicLight.currentValue();

            // Get light source coordinates
            int sourceX = dynamicLight.currentSourceX();
            int sourceY = dynamicLight.currentSourceY();

            // Get affected cells
            int maxSpreadDistance = lightSpreadMatrix.getRange() - 1;
            int xMin = Math.max(sourceX - maxSpreadDistance, 0);
            int xMax = Math.min(sourceX + maxSpreadDistance, cols - 1);
            int yMin = Math.max(sourceY - maxSpreadDistance, 0);
            int yMax = Math.min(sourceY + maxSpreadDistance, rows - 1);

            // The number of affected columns
            int affectedColumns = xMax - xMin + 1;

            // Update the light spread buffer
            int index = getIndex(xMin, yMin);
            for (int y = yMin; y <= yMax; ++y) {
                int distY = y - sourceY;
                for (int x = xMin; x <= xMax; ++x) {
                    int distX = x - sourceX;
                    spreadLightBuffer[index] += value * lightSpreadMatrix.getLightStrengthFactor(distX, distY);
                    ++index;
                }
                index += cols - affectedColumns;
            }
        }

        for (int i = 0; i < spreadLightBuffer.length; ++i) {
            if (spreadLightBuffer[i] > 0) {
                renderer.addLight(i, spreadLightBuffer[i]);
            }
        }

        dynamicLight.reset();
    }
}