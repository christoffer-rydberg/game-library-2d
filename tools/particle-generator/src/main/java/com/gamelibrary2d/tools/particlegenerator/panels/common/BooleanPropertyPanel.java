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
        Button button = (Button) obj;

        var buttonContext = button.getContent();
        if (fromString(buttonContext.getText())) {
            buttonContext.setFontColor(Color.GREEN);
        } else {
            buttonContext.setFontColor(Color.WHITE);
        }

        button.setBounds(buttonContext.getTextRenderer().getFont().textSize(
                buttonContext.getText(),
                buttonContext.getHorizontalAlignment(),
                buttonContext.getVerticalAlignment()));

        recalculateBounds();
    }

    @Override
    public void onMouseButtonReleased(GameObject obj, int button, int mods, float projectedX, float projectedY) {
        Button buttonObj = (Button) obj;

        var buttonContext = buttonObj.getContent();
        if (fromString(buttonContext.getText())) {
            buttonContext.setText(toString(false));
        } else {
            buttonContext.setText(toString(true));
        }
    }
}