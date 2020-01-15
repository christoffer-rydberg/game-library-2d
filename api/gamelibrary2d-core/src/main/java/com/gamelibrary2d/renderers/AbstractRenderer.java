package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.rendering.RenderSettings;

public abstract class AbstractRenderer implements Renderer {

    public final static float[] DefaultSettings = createDefaultSettings();
    private float[] settings;
    private int settingsSize = RenderSettings.DEFAULT_SETTINGS_SIZE;
    private int firstSettingIndex;
    private int lastSettingIndex;

    protected AbstractRenderer() {
    }

    private static float[] createDefaultSettings() {
        float[] defaultSettings = new float[RenderSettings.DEFAULT_SETTINGS_SIZE];
        defaultSettings[RenderSettings.ALPHA] = 1;
        defaultSettings[RenderSettings.COLOR_R] = 1;
        defaultSettings[RenderSettings.COLOR_G] = 1;
        defaultSettings[RenderSettings.COLOR_B] = 1;
        defaultSettings[RenderSettings.COLOR_A] = 1;
        return defaultSettings;
    }

    @Override
    public float getSetting(int index) {
        return settings[index];
    }

    @Override
    public void updateSettings(int index, float f) {
        prepareSettingsIndex(index, index);
        settings[index] = f;
    }

    @Override
    public void updateSettings(int index, float f1, float f2) {
        prepareSettingsIndex(index, index + 1);
        settings[index] = f1;
        settings[index + 1] = f2;
    }

    @Override
    public void updateSettings(int index, float f1, float f2, float f3) {
        prepareSettingsIndex(index, index + 2);
        settings[index] = f1;
        settings[index + 1] = f2;
        settings[index + 2] = f3;
    }

    @Override
    public void updateSettings(int index, float f1, float f2, float f3, float f4) {
        prepareSettingsIndex(index, index + 3);
        settings[index] = f1;
        settings[index + 1] = f2;
        settings[index + 2] = f3;
        settings[index + 3] = f4;
    }

    @Override
    public void updateSettings(int index, float[] settings, int offset, int length) {
        prepareSettingsIndex(index, index + length);
        System.arraycopy(settings, offset, this.settings, index, length);
    }

    /**
     * @return The size of the settings array.
     */
    public int getSettingsSize() {
        return settingsSize;
    }

    /**
     * The settings array is allocated the first time a setting is updated, or upon
     * rendering. Use this method to set the size of the array beforehand, in order
     * to avoid unnecessary allocations.
     *
     * @param settingsSize The size of the settings array when it is allocated.
     */
    public void setSettingsSize(int settingsSize) {
        if (settingsSize < RenderSettings.DEFAULT_SETTINGS_SIZE) {
            throw new GameLibrary2DRuntimeException("Minimum settings size is " + RenderSettings.DEFAULT_SETTINGS_SIZE);
        } else if (settingsSize > RenderSettings.MAXIMUM_SETTINGS_SIZE) {
            throw new GameLibrary2DRuntimeException("Maximum settings size is " + RenderSettings.MAXIMUM_SETTINGS_SIZE);
        }
        this.settingsSize = settingsSize;
    }

    /**
     * Sets each setting in the array to the value specified in the DefaultValues
     * array. Settings with indices out of bounds in the DefaultValues array, will
     * be set to 0. How default values are set can be changed by overriding the
     * setDefaultValue method. SettingsSize is set back to default as well.
     */
    public void resetSettings() {
        setDefaultValue(settings, firstSettingIndex, lastSettingIndex + 1);
        firstSettingIndex = 0;
        lastSettingIndex = 0;
        settingsSize = RenderSettings.DEFAULT_SETTINGS_SIZE;
    }

    /**
     * @return The highest changed index in the settings array.
     */
    protected int getLastSettingIndex() {
        return lastSettingIndex;
    }

    /**
     * @return The lowest changed index in the settings array.
     */
    protected int getFirstSettingIndex() {
        return this.firstSettingIndex;
    }

    /**
     * Call this before changing a setting, to update the firstSettingIndex and
     * lastSettingIndex fields and to ensure that the settings array is big enough.
     *
     * @param first The fist index (inclusive)
     * @param last  The last index (inclusive)
     */
    protected void prepareSettingsIndex(int first, int last) {
        if (first < firstSettingIndex)
            firstSettingIndex = first;

        if (last > lastSettingIndex) {
            lastSettingIndex = last;
            ensureSettingsSize(Math.max(settingsSize, last + 1));
        }
    }

    /**
     * Resizes the settings array, if needed, to the specified size.
     *
     * @param settingsSize The minimum needed size of the settings array.
     */
    protected void ensureSettingsSize(int settingsSize) {
        if (settings == null) {
            settings = new float[settingsSize];
            setDefaultValue(settings, 0, settingsSize);
            this.settingsSize = settingsSize;
        } else if (settingsSize > settings.length) {
            float[] tmp = settings;
            settings = new float[settingsSize];
            if (this.settingsSize >= 0)
                System.arraycopy(tmp, 0, settings, 0, this.settingsSize);

            setDefaultValue(settings, this.settingsSize, settingsSize);
        }

        this.settingsSize = settingsSize;
    }

    /**
     * @return The settings array.
     */
    protected float[] getSettings() {
        return settings;
    }

    /**
     * Called whenever the default values of the settings array must be reset.
     *
     * @param settings The settings array.
     * @param begin    The beginning of the array (inclusive).
     * @param end      The end of the array (exclusive).
     */
    protected void setDefaultValue(float[] settings, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            settings[i] = i >= DefaultSettings.length ? 0 : DefaultSettings[i];
        }
    }

    @Override
    public void render(float alpha) {
        ensureSettingsSize(settingsSize);
        onRender(alpha, settings, settingsSize);
    }

    protected abstract void onRender(float alpha, float[] settings, int settingsSize);
}