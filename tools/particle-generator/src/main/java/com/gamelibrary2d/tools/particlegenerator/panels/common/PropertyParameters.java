package com.gamelibrary2d.tools.particlegenerator.panels.common;

import java.util.ArrayList;
import java.util.List;

public abstract class PropertyParameters<T> {

    private final List<T> parameters;

    public PropertyParameters(int count) {
        parameters = new ArrayList<>(count);
        for (int i = 0; i < count; ++i)
            parameters.add(null);
    }

    int getCount() {
        return parameters.size();
    }

    protected boolean setParameter(int index, T value) {

        T oldValue = parameters.get(index);

        if (oldValue == value) {
            // Same value
            return false;
        }

        if (value != null && value.equals(oldValue)) {
            // Equal value
            return false;
        }

        parameters.set(index, value);

        return true;
    }

    protected T getParameter(int index) {
        return parameters.get(index);
    }

    /**
     * Updates setting from the parameters.
     */
    public abstract void updateSetting();

    /**
     * Checks if setting has changed and updates the parameters.
     *
     * @return - True if a change was detected. False otherwise.
     */
    public abstract boolean updateIfChanged();

}