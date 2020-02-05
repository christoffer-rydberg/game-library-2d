package com.gamelibrary2d.util;

/**
 * This class contains indices for configurable settings in a renderer, typically used by the shaders.
 */
public class RenderSettings {

    /**
     * The number of indices used by the default shaders.
     * This is also the first free index for custom settings if you
     * implement your own shaders.
     */
    public final static int DEFAULT_SETTINGS_SIZE = 7;

    /**
     * The maximum number of float settings, exceeding this value is not permitted.
     */
    public final static int MAXIMUM_SETTINGS_SIZE = 100;

    /**
     * The alpha setting.
     * This is automatically updated every render call and does not need to be explicitly set.
     * Value range: [0, 255]
     */
    public static final int ALPHA = 0;

    /**
     * Time variable that can be used to change visualization over time.
     */
    public static final int TIME = 1;

    /**
     * Color settings for the first channel (R). The subsequent indices represent G, B, and A.
     * Value range: [0, 255]
     */
    public static final int COLOR_R = 2;

    /**
     * Color settings for the second channel (G). The subsequent indices represent B, and A.
     * Value range: [0, 255]
     */
    public static final int COLOR_G = 3;

    /**
     * Color settings for the third channel (B). The subsequent index represents A.
     * Value range: [0, 255]
     */
    public static final int COLOR_B = 4;

    /**
     * Color settings for the fourth channel (A).
     * Value range: [0, 255]
     */
    public static final int COLOR_A = 5;

    /**
     * Determines if a texture should be applied.
     * This is typically set automatically by the renderer and should not be altered.
     * Altering this setting can put the renderer in an uneven state.
     * Possible values: false (0), true (1).
     */
    public static final int TEXTURED = 6;
}
