package com.gamelibrary2d.tools.particlegenerator.panels.common;

public class IntegerPropertyPanel extends TextBoxPropertyPanel<Integer> {

    public IntegerPropertyPanel(String propertyName, PropertyParameters<Integer> params) {
        super(propertyName, params);
    }

    @Override
    protected String toString(Integer value) {
        return value == null ? "" : value.toString();
    }

    @Override
    protected Integer fromString(String string) {
        return Integer.parseInt(string);
    }
}