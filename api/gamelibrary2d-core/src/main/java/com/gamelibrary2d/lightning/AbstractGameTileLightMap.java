package com.gamelibrary2d.lightning;

/**
 * Abstract implementation of a {@link GameTileLightMap}. An alpha map is
 * created from the light map, where light <= 0 -> alpha = 255f and light >= 255
 * -> alpha = 0. Light sources stronger than 255 will not show more light for
 * the specified cell, it will however spread more light to surrounding cells.
 */
public abstract class AbstractGameTileLightMap implements GameTileLightMap {

    private static final int DIRECT_LIGHT = 0;
    private static final int PRIMARY_REFLECTION = 1;
    private static final int SECONDARY_REFLECTION = 2;

    // Light bit lengths
    private static final int SUN_LIGHT_BIT_LENGTH = 16;
    private static final int LIGHT_BIT_LENGTH = 16;

    // Light bit offsets
    private static final int SUN_LIGHT_BIT_OFFSET = 0;
    private static final int LIGHT_BIT_OFFSET = SUN_LIGHT_BIT_OFFSET + SUN_LIGHT_BIT_LENGTH;

    // Light max values
    private static final int SUN_LIGHT_MAX_VALUE = (int) Math.pow(2, SUN_LIGHT_BIT_LENGTH) - 1;
    private static final int LIGHT_MAX_VALUE = (int) Math.pow(2, LIGHT_BIT_LENGTH) - 1;

    // Light bit masks
    private static final int SUN_LIGHT_BITMASK = (int) generateBitMask(SUN_LIGHT_BIT_OFFSET, SUN_LIGHT_BIT_LENGTH);
    private static final int LIGHT_BITMASK = (int) generateBitMask(LIGHT_BIT_OFFSET, LIGHT_BIT_LENGTH);
    private static final int SUN_LIGHT_INVERTED_BITMASK = ~SUN_LIGHT_BITMASK;
    private static final int LIGHT_INVERTED_BITMASK = ~LIGHT_BITMASK;

    // Light input bit length
    private static final long LIGHT_INPUT_BIT_LENGTH = 8;

    // Light input max value
    private static final long LIGHT_INPUT_MAX_VALUE = (long) Math.pow(2, LIGHT_INPUT_BIT_LENGTH) - 1;

    // Reflection bit length
    private static final long INPUT_BIT_LENGTH = 8;

    // Reflection max value
    private static final long INPUT_MAX_VALUE = (long) Math.pow(2, INPUT_BIT_LENGTH) - 1;

    // Light reflection bit offsets
    private static final long LEFT_SUN_LIGHT_INPUT_BIT_OFFSET = 0;
    private static final long TOP_SUN_LIGHT_INPUT_BIT_OFFSET = LEFT_SUN_LIGHT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;
    private static final long RIGHT_SUN_LIGHT_INPUT_BIT_OFFSET = TOP_SUN_LIGHT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;
    private static final long BOTTOM_SUN_LIGHT_INPUT_BIT_OFFSET = RIGHT_SUN_LIGHT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;

    // Light reflection bit masks
    private static final long LEFT_SUN_LIGHT_INPUT_BITMASK = generateBitMask(LEFT_SUN_LIGHT_INPUT_BIT_OFFSET,
            INPUT_BIT_LENGTH);
    private static final long TOP_SUN_LIGHT_INPUT_BITMASK = generateBitMask(TOP_SUN_LIGHT_INPUT_BIT_OFFSET,
            INPUT_BIT_LENGTH);
    private static final long RIGHT_SUN_LIGHT_INPUT_BITMASK = generateBitMask(RIGHT_SUN_LIGHT_INPUT_BIT_OFFSET,
            INPUT_BIT_LENGTH);
    private static final long BOTTOM_SUN_LIGHT_INPUT_BITMASK = generateBitMask(BOTTOM_SUN_LIGHT_INPUT_BIT_OFFSET,
            INPUT_BIT_LENGTH);
    private static final long LEFT_SUN_LIGHT_INPUT_INVERTED_BITMASK = ~LEFT_SUN_LIGHT_INPUT_BITMASK;
    private static final long TOP_SUN_LIGHT_INPUT_INVERTED_BITMASK = ~TOP_SUN_LIGHT_INPUT_BITMASK;
    private static final long RIGHT_SUN_LIGHT_INPUT_INVERTED_BITMASK = ~RIGHT_SUN_LIGHT_INPUT_BITMASK;
    private static final long BOTTOM_SUN_LIGHT_INPUT_INVERTED_BITMASK = ~BOTTOM_SUN_LIGHT_INPUT_BITMASK;

    // Other reflection bit offsets
    private static final long LEFT_INPUT_BIT_OFFSET = BOTTOM_SUN_LIGHT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;
    private static final long TOP_INPUT_BIT_OFFSET = LEFT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;
    private static final long RIGHT_INPUT_BIT_OFFSET = TOP_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;
    private static final long BOTTOM_INPUT_BIT_OFFSET = RIGHT_INPUT_BIT_OFFSET + INPUT_BIT_LENGTH;

    // Other reflection bit masks
    private static final long LEFT_INPUT_BITMASK = generateBitMask(LEFT_INPUT_BIT_OFFSET, INPUT_BIT_LENGTH);
    private static final long TOP_INPUT_BITMASK = generateBitMask(TOP_INPUT_BIT_OFFSET, INPUT_BIT_LENGTH);
    private static final long RIGHT_INPUT_BITMASK = generateBitMask(RIGHT_INPUT_BIT_OFFSET, INPUT_BIT_LENGTH);
    private static final long BOTTOM_INPUT_BITMASK = generateBitMask(BOTTOM_INPUT_BIT_OFFSET, INPUT_BIT_LENGTH);
    private static final long LEFT_INPUT_INVERTED_BITMASK = ~LEFT_INPUT_BITMASK;
    private static final long TOP_INPUT_INVERTED_BITMASK = ~TOP_INPUT_BITMASK;
    private static final long RIGHT_INPUT_INVERTED_BITMASK = ~RIGHT_INPUT_BITMASK;
    private static final long BOTTOM_INPUT_INVERTED_BITMASK = ~BOTTOM_INPUT_BITMASK;
    /**
     * The number of columns in the game grid.
     */
    private final int gameCols;
    /**
     * The number of rows in the game grid.
     */
    private final int gameRows;
    /**
     * Holds the light values of all cells in the game grid.
     */
    private final int[] lightSourceBuffer;
    /**
     */
    private final long[][] lightReflectionBuffer;
    /**
     * The column span of the game window.
     */
    private int colSpan;
    /**
     * The row span of the game window.
     */
    private int rowSpan;
    /**
     * Used to stores the light values of world cells within the game window.
     */
    private int[] gameGridLightMap;
    /**
     * Used to store the delta values that are applied to the {@link LightRenderer}
     */
    private float[] lightMapDelta;
    private float sunlightFactor = 1f;

    public AbstractGameTileLightMap(int gameCols, int gameRows) {
        this.gameCols = gameCols;
        this.gameRows = gameRows;
        lightSourceBuffer = new int[gameCols * gameRows];
        lightReflectionBuffer = new long[3][gameCols * gameRows];
    }

    private static long generateBitMask(long bitOffset, long bitLength) {
        long bitMask = 0xffffffffffffffffL;
        bitMask = bitMask << bitOffset; // Consume unused left bits
        bitMask = bitMask >>> (64 - bitLength); // Consume unused right bits
        bitMask = bitMask << bitOffset; // Correct bit position
        return bitMask;
    }

    public void initialize() {
        initializeCellLight();
        generateLightInputs(false);
        generateLightInputs(true);
        generateReflection(PRIMARY_REFLECTION, false);
        generateReflection(PRIMARY_REFLECTION, true);
        generateReflection(SECONDARY_REFLECTION, false);
        generateReflection(SECONDARY_REFLECTION, true);
    }

    public int getIndex(int col, int row) {
        return row * gameCols + col;
    }

    public int getCol(int index) {
        return index % gameCols;
    }

    public int getRow(int index) {
        return index / gameCols;
    }

    private void initializeCellLight() {
        int length = gameCols * gameRows;
        for (int index = 0; index < length; ) {
            final int rowEnd = index + gameCols;
            for (; index < rowEnd; ++index) {
                int light = getInitialLight(index);
                if (light > 0)
                    setLight(index, light);
                int sunLight = getInitialSunlight(index);
                if (sunLight > 0)
                    setSunLight(index, sunLight);
            }
        }
    }

    private void generateLightInputs(boolean sunlight) {
        generateLeftLightInputs(sunlight);
        generateRightLightInputs(sunlight);
        generateTopLightInputs(sunlight);
        generateBottomLightInputs(sunlight);
    }

    private void generateReflection(int lightType, boolean isSunlight) {
        generateLeftReflectionInputs(lightType, isSunlight);
        generateRightReflectionInputs(lightType, isSunlight);
        generateTopReflectionInputs(lightType, isSunlight);
        generateBottomReflectionInputs(lightType, isSunlight);
    }

    private void generateLeftLightInputs(boolean isSunlight) {
        int length = gameCols * gameRows;
        for (int index = 0; index < length; ) {
            int lightInput = 0;
            final int rowEnd = index + gameCols;
            for (; index < rowEnd; ++index) {
                if (lightInput > 0) {
                    setLeftLightInput(DIRECT_LIGHT, index, lightInput, isSunlight);
                    lightInput = absorbLight(lightInput, getTransmissionFactor(index));
                }
                lightInput = Math.min(lightInput + getLight(index, isSunlight), (int) LIGHT_INPUT_MAX_VALUE);
            }
        }
    }

    private void generateRightLightInputs(boolean isSunlight) {
        int length = gameCols * gameRows;
        for (int index = length - 1; index > -1; ) {
            int lightInput = 0;
            final int rowStart = index - gameCols;
            for (; index > rowStart; --index) {
                if (lightInput > 0) {
                    setRightLightInput(DIRECT_LIGHT, index, lightInput, isSunlight);
                    lightInput = absorbLight(lightInput, getTransmissionFactor(index));
                }
                lightInput = Math.min(lightInput + getLight(index, isSunlight), (int) LIGHT_INPUT_MAX_VALUE);
            }
        }
    }

    private void generateTopLightInputs(boolean isSunlight) {
        for (int col = 0; col < gameCols; ++col) {
            int lightInput = 0;
            int colEnd = gameRows * gameCols + col;
            for (int index = col; index < colEnd; index += gameCols) {
                if (lightInput > 0) {
                    setTopLightInput(DIRECT_LIGHT, index, lightInput, isSunlight);
                    lightInput = absorbLight(lightInput, getTransmissionFactor(index));
                }
                lightInput = Math.min(lightInput + getLight(index, isSunlight), (int) LIGHT_INPUT_MAX_VALUE);
            }
        }
    }

    private void generateBottomLightInputs(boolean isSunlight) {
        for (int col = 0; col < gameCols; ++col) {
            int lightInput = 0;
            int colEnd = gameRows * gameCols + col;
            for (int index = colEnd - gameCols; index >= col; index -= gameCols) {
                if (lightInput > 0) {
                    setBottomLightInput(DIRECT_LIGHT, index, lightInput, isSunlight);
                    lightInput = absorbLight(lightInput, getTransmissionFactor(index));
                }
                lightInput = Math.min(lightInput + getLight(index, isSunlight), (int) LIGHT_INPUT_MAX_VALUE);
            }
        }
    }

    private void generateLeftReflectionInputs(int lightType, boolean isSunLight) {
        int lastCol = gameCols * gameRows - 1;
        for (int index = 0; index < lastCol; ) {
            int lastRow = index + gameCols - 1;
            while (index < lastRow) {

                float reflectionFactor = getLightReflectionFactor(index);
                float transmissionFactor = getTransmissionFactor(index);

                int topLightInput = absorbLight(getTopLightInput(lightType - 1, index, isSunLight), transmissionFactor);
                int bottomLightInput = absorbLight(getBottomLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int reflectedLight = absorbLight(topLightInput + bottomLightInput, reflectionFactor);
                int forwardedReflection = absorbLight(getLeftLightInput(lightType, index, isSunLight),
                        transmissionFactor);

                ++index;

                int reflectionInput = Math.min(reflectedLight + forwardedReflection, (int) INPUT_MAX_VALUE);
                setLeftLightInput(lightType, index, reflectionInput, isSunLight);
            }
        }
    }

    private void generateRightReflectionInputs(int lightType, boolean isSunLight) {
        int lastCol = gameCols * gameRows - 1;
        for (int index = lastCol; index > 0; ) {
            int fistCol = index - gameCols + 1;
            while (index > fistCol) {

                float reflectionFactor = getLightReflectionFactor(index);
                float transmissionFactor = getTransmissionFactor(index);

                int topLightInput = absorbLight(getTopLightInput(lightType - 1, index, isSunLight), transmissionFactor);
                int bottomLightInput = absorbLight(getBottomLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int reflectedLight = absorbLight(topLightInput + bottomLightInput, reflectionFactor);
                int forwardedReflection = absorbLight(getRightLightInput(lightType, index, isSunLight),
                        transmissionFactor);

                --index;

                int reflectionInput = Math.min(reflectedLight + forwardedReflection, (int) INPUT_MAX_VALUE);
                setRightLightInput(lightType, index, reflectionInput, isSunLight);
            }
        }
    }

    private void generateTopReflectionInputs(int lightType, boolean isSunLight) {
        for (int col = 0; col < gameCols; ++col) {
            int lastRow = (gameRows - 1) * gameCols + col;
            for (int index = col; index < lastRow; ) {

                float reflectionFactor = getLightReflectionFactor(index);
                float transmissionFactor = getTransmissionFactor(index);

                int leftLightInput = absorbLight(getLeftLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int rightLightInput = absorbLight(getRightLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int reflectedLight = absorbLight(leftLightInput + rightLightInput, reflectionFactor);
                int forwardedReflection = absorbLight(getTopLightInput(lightType, index, isSunLight),
                        transmissionFactor);

                index += gameCols;

                int reflectionInput = Math.min(reflectedLight + forwardedReflection, (int) INPUT_MAX_VALUE);
                setTopLightInput(lightType, index, reflectionInput, isSunLight);
            }
        }
    }

    private void generateBottomReflectionInputs(int lightType, boolean isSunLight) {
        for (int col = 0; col < gameCols; ++col) {
            int lastRow = (gameRows - 1) * gameCols + col;
            for (int index = lastRow; index >= col + gameCols; ) {

                float reflectionFactor = getLightReflectionFactor(index);
                float transmissionFactor = getTransmissionFactor(index);

                int leftLightInput = absorbLight(getLeftLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int rightLightInput = absorbLight(getRightLightInput(lightType - 1, index, isSunLight),
                        transmissionFactor);
                int reflectedLight = absorbLight(leftLightInput + rightLightInput, reflectionFactor);
                int forwardedReflection = absorbLight(getBottomLightInput(lightType, index, isSunLight),
                        transmissionFactor);

                index -= gameCols;

                int reflectionInput = Math.min(reflectedLight + forwardedReflection, (int) INPUT_MAX_VALUE);
                setBottomLightInput(lightType, index, reflectionInput, isSunLight);
            }
        }
    }

    public void updateCell(int col, int row) {
        int index = getIndex(col, row);
        updateTile(index, col, row, getLight(index), false);
        updateTile(index, col, row, getSunLight(index), true);
    }

    public void updateCell(int col, int row, int light, boolean isSunlight) {
        updateTile(getIndex(col, row), col, row, light, isSunlight);
    }

    private void updateTile(int index, int col, int row, int light, boolean isSunlight) {

        float transmissionFactor = getTransmissionFactor(index);

        if (isSunlight)
            setSunLight(index, light);
        else
            setLight(index, light);

        // Sunlight output of tile
        int leftSunLightOutput = absorbLight(getRightLightInput(DIRECT_LIGHT, index, isSunlight), transmissionFactor)
                + light;
        int topSunLightOutput = absorbLight(getBottomLightInput(DIRECT_LIGHT, index, isSunlight), transmissionFactor)
                + light;
        int rightSunLightOutput = absorbLight(getLeftLightInput(DIRECT_LIGHT, index, isSunlight), transmissionFactor)
                + light;
        int bottomSunLightOutput = absorbLight(getTopLightInput(DIRECT_LIGHT, index, isSunlight), transmissionFactor)
                + light;

        // Update light output
        updateLeftLightOutput(index, col, row, leftSunLightOutput, isSunlight);
        updateTopLightOutput(index, col, row, topSunLightOutput, isSunlight);
        updateRightLightOutput(index, col, row, rightSunLightOutput, isSunlight);
        updateBottomLightOutput(index, col, row, bottomSunLightOutput, isSunlight);

        // Update primary reflection output
        updateLeftReflectionOutput(PRIMARY_REFLECTION, index, col, row, isSunlight);
        updateTopReflectionOutput(PRIMARY_REFLECTION, index, col, row, isSunlight);
        updateRightReflectionOutput(PRIMARY_REFLECTION, index, col, row, isSunlight);
        updateBottomReflectionOutput(PRIMARY_REFLECTION, index, col, row, isSunlight);

        // Update secondary reflection output
        updateLeftReflectionOutput(SECONDARY_REFLECTION, index, col, row, isSunlight);
        updateTopReflectionOutput(SECONDARY_REFLECTION, index, col, row, isSunlight);
        updateRightReflectionOutput(SECONDARY_REFLECTION, index, col, row, isSunlight);
        updateBottomReflectionOutput(SECONDARY_REFLECTION, index, col, row, isSunlight);
    }

    private int absorbLight(int light, float transmissionFactor) {
        int newValue = Math.round(light * transmissionFactor);
        return Math.max(0, newValue == light ? newValue - 1 : newValue);
    }

    private void updateLeftLightOutput(int index, int currentCol, int currentRow, int lightOutput, boolean isSunLight) {

        while (currentCol > 0) {

            --index;
            --currentCol;

            // Abort if light input is unchanged
            lightOutput = Math.min(lightOutput, (int) LIGHT_INPUT_MAX_VALUE);
            if (lightOutput == getRightLightInput(DIRECT_LIGHT, index, isSunLight))
                return;

            // Update light input
            setRightLightInput(DIRECT_LIGHT, index, lightOutput, isSunLight);

            // Update vertical reflection
            updateTopReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            updateBottomReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);

            // Apply tile absorption and light
            float transmissionFactor = getTransmissionFactor(index);
            lightOutput = absorbLight(lightOutput, transmissionFactor);
            lightOutput += getLight(index, isSunLight);
        }
    }

    private void updateTopLightOutput(int index, int currentCol, int currentRow, int lightOutput, boolean isSunLight) {

        while (currentRow > 0) {

            index -= gameCols;
            --currentRow;

            // Abort if light input is unchanged
            lightOutput = Math.min(lightOutput, (int) LIGHT_INPUT_MAX_VALUE);
            if (lightOutput == getBottomLightInput(DIRECT_LIGHT, index, isSunLight))
                return;

            // Update light input
            setBottomLightInput(DIRECT_LIGHT, index, lightOutput, isSunLight);

            // Update horizontal reflection
            updateLeftReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            updateRightReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);

            // Apply tile absorption and light
            float transmissionFactor = getTransmissionFactor(index);
            lightOutput = absorbLight(lightOutput, transmissionFactor);
            lightOutput += getLight(index, isSunLight);
        }
    }

    private void updateRightLightOutput(int index, int currentCol, int currentRow, int lightOutput,
                                        boolean isSunLight) {

        while (currentCol < gameCols - 1) {

            ++index;
            ++currentCol;

            // Abort if light input is unchanged
            lightOutput = Math.min(lightOutput, (int) LIGHT_INPUT_MAX_VALUE);
            if (lightOutput == getLeftLightInput(DIRECT_LIGHT, index, isSunLight))
                return;

            // Update light input
            setLeftLightInput(DIRECT_LIGHT, index, lightOutput, isSunLight);

            // Update vertical reflection
            updateTopReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            updateBottomReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);

            // Apply tile absorption and light
            float transmissionFactor = getTransmissionFactor(index);
            lightOutput = absorbLight(lightOutput, transmissionFactor);
            lightOutput += getLight(index, isSunLight);
        }
    }

    private void updateBottomLightOutput(int index, int currentCol, int currentRow, int lightOutput,
                                         boolean isSunLight) {

        while (currentRow < gameRows - 1) {

            index += gameCols;
            ++currentRow;

            // Abort if light input is unchanged
            lightOutput = Math.min(lightOutput, (int) LIGHT_INPUT_MAX_VALUE);
            if (lightOutput == getTopLightInput(DIRECT_LIGHT, index, isSunLight))
                return;

            // Update light input
            setTopLightInput(DIRECT_LIGHT, index, lightOutput, isSunLight);

            // Update horizontal reflection
            updateLeftReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            updateRightReflectionOutput(PRIMARY_REFLECTION, index, currentCol, currentRow, isSunLight);

            // Apply tile absorption and light
            float transmissionFactor = getTransmissionFactor(index);
            lightOutput = absorbLight(lightOutput, transmissionFactor);
            lightOutput += getLight(index, isSunLight);
        }
    }

    private void updateLeftReflectionOutput(int lightType, int index, int currentCol, int currentRow,
                                            boolean isSunLight) {

        while (currentCol > 0) {

            float reflectionFactor = getLightReflectionFactor(index);
            float transmissionFactor = getTransmissionFactor(index);

            int topLightInput = absorbLight(getTopLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int bottomLightInput = absorbLight(getBottomLightInput(lightType - 1, index, isSunLight),
                    transmissionFactor);
            int reflectedInput = absorbLight(topLightInput + bottomLightInput, reflectionFactor);
            int forwardedReflection = absorbLight(getRightLightInput(lightType, index, isSunLight), transmissionFactor);

            --index;
            --currentCol;

            // Abort if reflection is unchanged
            int newRightReflection = Math.min(reflectedInput + forwardedReflection, (int) INPUT_MAX_VALUE);
            if (newRightReflection == getRightLightInput(lightType, index, isSunLight))
                return;

            // Update reflection
            setRightLightInput(lightType, index, newRightReflection, isSunLight);

            if (lightType == PRIMARY_REFLECTION) {
                // Update vertical reflection
                updateTopReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
                updateBottomReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            }
        }
    }

    private void updateTopReflectionOutput(int lightType, int index, int currentCol, int currentRow,
                                           boolean isSunLight) {

        while (currentRow > 0) {

            float reflectionFactor = getLightReflectionFactor(index);
            float transmissionFactor = getTransmissionFactor(index);

            int leftLightInput = absorbLight(getLeftLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int rightLightInput = absorbLight(getRightLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int reflectedInput = absorbLight(leftLightInput + rightLightInput, reflectionFactor);
            int forwardedReflection = absorbLight(getBottomLightInput(lightType, index, isSunLight),
                    transmissionFactor);

            index -= gameCols;
            --currentRow;

            // Abort if reflection is unchanged
            int newBottomReflection = Math.min(reflectedInput + forwardedReflection, (int) INPUT_MAX_VALUE);
            if (newBottomReflection == getBottomLightInput(lightType, index, isSunLight))
                return;

            // Update reflection
            setBottomLightInput(lightType, index, newBottomReflection, isSunLight);

            if (lightType == PRIMARY_REFLECTION) {
                // Update horizontal reflection
                updateLeftReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
                updateRightReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            }
        }
    }

    private void updateRightReflectionOutput(int lightType, int index, int currentCol, int currentRow,
                                             boolean isSunLight) {

        while (currentCol < gameCols - 1) {

            float reflectionFactor = getLightReflectionFactor(index);
            float transmissionFactor = getTransmissionFactor(index);

            int topLightInput = absorbLight(getTopLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int bottomLightInput = absorbLight(getBottomLightInput(lightType - 1, index, isSunLight),
                    transmissionFactor);
            int reflectedInput = absorbLight(topLightInput + bottomLightInput, reflectionFactor);
            int forwardedReflection = absorbLight(getLeftLightInput(lightType, index, isSunLight), transmissionFactor);

            ++index;
            ++currentCol;

            // Abort if reflection is unchanged
            int newLeftReflection = Math.min(reflectedInput + forwardedReflection, (int) INPUT_MAX_VALUE);
            if (newLeftReflection == getLeftLightInput(lightType, index, isSunLight))
                return;

            // Update reflection
            setLeftLightInput(lightType, index, newLeftReflection, isSunLight);

            if (lightType == PRIMARY_REFLECTION) {
                // Update vertical reflection
                updateTopReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
                updateBottomReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            }
        }
    }

    private void updateBottomReflectionOutput(int lightType, int index, int currentCol, int currentRow,
                                              boolean isSunLight) {

        while (currentRow < gameRows - 1) {

            float reflectionFactor = getLightReflectionFactor(index);
            float transmissionFactor = getTransmissionFactor(index);

            int leftLightInput = absorbLight(getLeftLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int rightLightInput = absorbLight(getRightLightInput(lightType - 1, index, isSunLight), transmissionFactor);
            int reflectedInput = absorbLight(leftLightInput + rightLightInput, reflectionFactor);
            int forwardedReflection = absorbLight(getTopLightInput(lightType, index, isSunLight), transmissionFactor);

            index += gameCols;
            ++currentRow;

            // Abort if reflection is unchanged
            int newTopReflection = Math.min(reflectedInput + forwardedReflection, (int) INPUT_MAX_VALUE);
            if (newTopReflection == getTopLightInput(lightType, index, isSunLight))
                return;

            // Update reflection
            setTopLightInput(lightType, index, newTopReflection, isSunLight);

            if (lightType == PRIMARY_REFLECTION) {
                // Update horizontal reflection
                updateLeftReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
                updateRightReflectionOutput(SECONDARY_REFLECTION, index, currentCol, currentRow, isSunLight);
            }
        }
    }

    /**
     * Gets the accumulated light of a cell by looking at all light inputs as well
     * as the light sources of the cell itself. Sunlight will be capped at the
     * specified value and the sunlight factor will be applied to control the
     * sunlight strength (e.g. make it weaker at night).
     *
     * @param index          The light grid index.
     * @param sunlightCap    The sunlight cap.
     * @param sunlightFactor The sunlight factor.
     * @return The accumulated light.
     */
    private int getAccumulatedLight(int index, int sunlightCap, float sunlightFactor) {

        int lightSource = getLight(index);
        int sunlightSource = getSunLight(index);

        float transmissionFactor = getTransmissionFactor(index);

        int directLightInput = absorbLight(getLightInput(DIRECT_LIGHT, index), transmissionFactor);
        int directSunlightInput = absorbLight(getSunlightInput(DIRECT_LIGHT, index), transmissionFactor);

        int primaryLightReflectionInput = absorbLight(getLightInput(PRIMARY_REFLECTION, index), transmissionFactor);

        int primarySunlightReflectionInput = absorbLight(getSunlightInput(PRIMARY_REFLECTION, index),
                transmissionFactor);

        int secondaryLightReflectionInput = absorbLight(getLightInput(SECONDARY_REFLECTION, index), transmissionFactor);

        int secondarySunlightReflectionInput = absorbLight(getSunlightInput(SECONDARY_REFLECTION, index),
                transmissionFactor);

        int light = lightSource + directLightInput + primaryLightReflectionInput + secondaryLightReflectionInput;

        int sunlight = sunlightSource + directSunlightInput + primarySunlightReflectionInput
                + secondarySunlightReflectionInput;

        return light + absorbLight(Math.min(sunlight, sunlightCap), sunlightFactor);
    }

    private int getLightInput(int lightType, int index) {
        int left = getLeftLightInput(lightType, index);
        int top = getTopLightLightInput(lightType, index);
        int right = getRightLightInput(lightType, index);
        int bottom = getBottomLightInput(lightType, index);
        return left + top + right + bottom;
    }

    private int getSunlightInput(int lightType, int index) {
        int left = getLeftSunLightInput(lightType, index);
        int top = getTopSunLightInput(lightType, index);
        int right = getRightSunLightInput(lightType, index);
        int bottom = getBottomSunLightInput(lightType, index);
        return left + top + right + bottom;
    }

    public int getLight(int index, boolean isSunLight) {
        return isSunLight ? getSunLight(index) : getLight(index);
    }

    public int getSunLight(int index) {
        return (int) getLight(index, SUN_LIGHT_BITMASK, SUN_LIGHT_BIT_OFFSET);
    }

    public int getLight(int index) {
        return (int) getLight(index, LIGHT_BITMASK, LIGHT_BIT_OFFSET);
    }

    private void setSunLight(int index, int value) {
        setLight(index, value, SUN_LIGHT_INVERTED_BITMASK, SUN_LIGHT_BIT_OFFSET, SUN_LIGHT_MAX_VALUE);
    }

    private void setLight(int index, int value) {
        setLight(index, value, LIGHT_INVERTED_BITMASK, LIGHT_BIT_OFFSET, LIGHT_MAX_VALUE);
    }

    private int getLeftLightInput(int lightType, int index, boolean isSunLight) {
        return isSunLight ? getLeftSunLightInput(lightType, index) : getLeftLightInput(lightType, index);
    }

    private int getLeftLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, LEFT_INPUT_BITMASK, LEFT_INPUT_BIT_OFFSET);
    }

    private int getLeftSunLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, LEFT_SUN_LIGHT_INPUT_BITMASK, LEFT_SUN_LIGHT_INPUT_BIT_OFFSET);
    }

    private void setLeftLightInput(int lightType, int index, int value, boolean isSunLight) {
        if (isSunLight)
            setLeftSunLightInput(lightType, index, value);
        else
            setLeftLightInput(lightType, index, value);
    }

    private void setTopLightInput(int lightType, int index, int value, boolean isSunLight) {
        if (isSunLight)
            setTopSunLightInput(lightType, index, value);
        else
            setTopLightInput(lightType, index, value);
    }

    private void setRightLightInput(int lightType, int index, int value, boolean isSunLight) {
        if (isSunLight)
            setRightSunLightInput(lightType, index, value);
        else
            setRightLightInput(lightType, index, value);
    }

    private void setBottomLightInput(int lightType, int index, int value, boolean isSunLight) {
        if (isSunLight)
            setBottomSunLightInput(lightType, index, value);
        else
            setBottomLightInput(lightType, index, value);
    }

    private void setLeftSunLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, LEFT_SUN_LIGHT_INPUT_INVERTED_BITMASK, LEFT_SUN_LIGHT_INPUT_BIT_OFFSET,
                INPUT_MAX_VALUE);
    }

    private void setTopSunLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, TOP_SUN_LIGHT_INPUT_INVERTED_BITMASK, TOP_SUN_LIGHT_INPUT_BIT_OFFSET,
                INPUT_MAX_VALUE);
    }

    private void setRightSunLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, RIGHT_SUN_LIGHT_INPUT_INVERTED_BITMASK, RIGHT_SUN_LIGHT_INPUT_BIT_OFFSET,
                INPUT_MAX_VALUE);
    }

    private void setBottomSunLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, BOTTOM_SUN_LIGHT_INPUT_INVERTED_BITMASK,
                BOTTOM_SUN_LIGHT_INPUT_BIT_OFFSET, INPUT_MAX_VALUE);
    }

    private void setLeftLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, LEFT_INPUT_INVERTED_BITMASK, LEFT_INPUT_BIT_OFFSET, INPUT_MAX_VALUE);
    }

    private void setTopLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, TOP_INPUT_INVERTED_BITMASK, TOP_INPUT_BIT_OFFSET, INPUT_MAX_VALUE);
    }

    private void setRightLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, RIGHT_INPUT_INVERTED_BITMASK, RIGHT_INPUT_BIT_OFFSET, INPUT_MAX_VALUE);
    }

    private void setBottomLightInput(int lightType, int index, int value) {
        setLightInput(lightType, index, value, BOTTOM_INPUT_INVERTED_BITMASK, BOTTOM_INPUT_BIT_OFFSET, INPUT_MAX_VALUE);
    }

    private void setLightInput(int lightType, int index, long value, long bitMask, long bitOffset, long maxValue) {
        if (value < 0) {
            value = 0;
        } else if (value > maxValue) {
            value = maxValue;
        }
        lightReflectionBuffer[lightType][index] = (lightReflectionBuffer[lightType][index] & bitMask)
                | (value << bitOffset);
    }

    private int getTopLightInput(int lightType, int index, boolean isSunLight) {
        return isSunLight ? getTopSunLightInput(lightType, index) : getTopLightLightInput(lightType, index);
    }

    private int getTopSunLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, TOP_SUN_LIGHT_INPUT_BITMASK, TOP_SUN_LIGHT_INPUT_BIT_OFFSET);
    }

    private int getTopLightLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, TOP_INPUT_BITMASK, TOP_INPUT_BIT_OFFSET);
    }

    private int getRightLightInput(int lightType, int index, boolean isSunLight) {
        return isSunLight ? getRightSunLightInput(lightType, index) : getRightLightInput(lightType, index);
    }

    private int getRightSunLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, RIGHT_SUN_LIGHT_INPUT_BITMASK, RIGHT_SUN_LIGHT_INPUT_BIT_OFFSET);
    }

    private int getRightLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, RIGHT_INPUT_BITMASK, RIGHT_INPUT_BIT_OFFSET);
    }

    private int getBottomLightInput(int lightType, int index, boolean isSunLight) {
        return isSunLight ? getBottomSunLightInput(lightType, index) : getBottomLightInput(lightType, index);
    }

    private int getBottomSunLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, BOTTOM_SUN_LIGHT_INPUT_BITMASK, BOTTOM_SUN_LIGHT_INPUT_BIT_OFFSET);
    }

    private int getBottomLightInput(int lightType, int index) {
        return (int) getLightInput(lightType, index, BOTTOM_INPUT_BITMASK, BOTTOM_INPUT_BIT_OFFSET);
    }

    private void setLight(int index, int value, int bitMask, int bitOffset, int maxValue) {
        if (value < 0) {
            value = 0;
        } else if (value > maxValue) {
            value = maxValue;
        }
        lightSourceBuffer[index] = (lightSourceBuffer[index] & bitMask) | (value << bitOffset);
    }

    private long getLight(int index, int invertedBitMask, int bitOffset) {
        return (lightSourceBuffer[index] & invertedBitMask) >>> bitOffset;
    }

    private long getLightInput(int lightType, int index, long invertedBitMask, long bitOffset) {
        return (lightReflectionBuffer[lightType][index] & invertedBitMask) >>> bitOffset;
    }

    public float getSunlightFactor() {
        return sunlightFactor;
    }

    public void setSunlightFactor(float sunlightFactor) {
        this.sunlightFactor = sunlightFactor;
    }

    @Override
    public void prepare(int alphaMapCols, int alphaMapRows, int gameColOffset, int gameColSpan, int gameRowOffset, int gameRowSpan) {
        int colSpan = alphaMapCols / 2 + 2;
        int rowSpan = alphaMapRows / 2 + 2;
        if (colSpan != this.colSpan || rowSpan != this.rowSpan) {
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
            gameGridLightMap = new int[colSpan * rowSpan];
            lightMapDelta = new float[gameGridLightMap.length * 4];
        }

        int endCol = gameColOffset + gameColSpan + 1;
        int endRow = gameRowOffset + gameRowSpan + 1;

        --gameColOffset;
        --gameRowOffset;

        final int start = gameRowOffset * gameCols + gameColOffset;
        final int end = (endRow - 1) * gameCols + endCol;
        final int columns = endCol - gameColOffset;
        final int gap = gameCols - columns;

        int row = gameRowOffset;
        int lightMappingIndex = 0;
        for (int index = start; index < end; index += gap) {

            int col = gameColOffset;
            int columnEnd = index + columns;
            for (; index < columnEnd; ++index) {
                gameGridLightMap[lightMappingIndex] = col < 0 || col >= gameCols || row < 0 || row >= gameRows ? -1
                        : getAccumulatedLight(index, 255, sunlightFactor);
                ++col;
                ++lightMappingIndex;
            }
            ++row;
        }
    }

    public void apply(LightRenderer renderer) {
        int alphaMapCols = renderer.getCols();

        int previousAlpha = 0;
        int lightMapIndex = 0;
        int tmpIndex = 0;

        for (int y = 0; y < rowSpan - 1; ++y) {
            for (int x = 0; x < colSpan - 1; ++x) {
                final int current = gameGridLightMap[lightMapIndex];
                final int right = gameGridLightMap[lightMapIndex + 1];
                final int lower = gameGridLightMap[lightMapIndex + colSpan];
                final int lowerRight = gameGridLightMap[lightMapIndex + colSpan + 1];
                final int alpha = max(current, right, lower, lowerRight);

                if (x > 0) {
                    lightMapDelta[tmpIndex] = ((previousAlpha + alpha) / 2f);
                    ++tmpIndex;
                }

                if (y > 0) {
                    float upperAlpha = lightMapDelta[tmpIndex - alphaMapCols * 2];
                    lightMapDelta[tmpIndex - alphaMapCols] = (upperAlpha + alpha) / 2;

                    if (x > 0) {
                        float upperLeftAlpha = lightMapDelta[tmpIndex - alphaMapCols * 2 - 2];
                        float leftAlpha = lightMapDelta[tmpIndex - 2];
                        float average = (upperAlpha + upperLeftAlpha + leftAlpha + alpha) / 4;
                        lightMapDelta[tmpIndex - alphaMapCols - 1] = average;
                    }
                }

                lightMapDelta[tmpIndex] = alpha;
                ++tmpIndex;
                ++lightMapIndex;
                previousAlpha = alpha;
            }

            // Skip alpha map row
            tmpIndex += alphaMapCols;

            // Skip last light map column
            ++lightMapIndex;
        }

        for (int i = 0; i < lightMapDelta.length; ++i)
            renderer.addLight(i, lightMapDelta[i]);
    }

    private int max(int v1, int v2, int v3, int v4) {
        int value = v1;
        if (v2 > value)
            value = v2;
        if (v3 > value)
            value = v3;
        if (v4 > value)
            value = v4;
        return value;
    }

    protected abstract int getInitialLight(int index);

    protected abstract int getInitialSunlight(int index);

    protected abstract float getTransmissionFactor(int index);

    protected abstract float getLightReflectionFactor(int index);
}