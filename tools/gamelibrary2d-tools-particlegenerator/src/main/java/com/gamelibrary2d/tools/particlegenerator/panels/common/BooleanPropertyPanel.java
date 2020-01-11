package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;

public class BooleanPropertyPanel extends ButtonPropertyPanel<Boolean> {

    public BooleanPropertyPanel(String propertyName, PropertyParameters<Boolean> params) {
        super(propertyName, params);
    }

    @Override
    protected String toString(Boolean value) {
        return value == null || !value ? "False" : "True";
    }

    @Override
    protected Boolean fromString(String string) {
        return string.toLowerCase().equals("true");
    }

    @Override
    public void onTextChanged(GameObject obj, String before, String after) {

        Button buttonObj = (Button) obj;

        if (fromString(buttonObj.getText())) {
            buttonObj.setFontColor(Color.GREEN);
        } else {
            buttonObj.setFontColor(Color.WHITE);
        }

        buttonObj.setBounds(buttonObj.getTextRenderer().getFont().textSize(
                buttonObj.getText(),
                buttonObj.getHorizontalAlignment(),
                buttonObj.getVerticalAlignment()));

        recalculateBounds();
    }

    @Override
    public void onMouseRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {

        Button buttonObj = (Button) obj;

        if (fromString(buttonObj.getText())) {
            buttonObj.setText(toString(false));
        } else {
            buttonObj.setText(toString(true));
        }
    }
}