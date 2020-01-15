package com.gamelibrary2d.tools.particlegenerator.panels.common;

public class FloatPropertyPanel extends TextBoxPropertyPanel<Float> {

    public FloatPropertyPanel(String propertyName, PropertyParameters<Float> params) {
        super(propertyName, params);
    }

    @Override
    protected String toString(Float value) {
        return value == null ? "" : value.toString();
    }

    @Override
    protected Float fromString(String string) {
        return Float.parseFloat(string);
    }
}