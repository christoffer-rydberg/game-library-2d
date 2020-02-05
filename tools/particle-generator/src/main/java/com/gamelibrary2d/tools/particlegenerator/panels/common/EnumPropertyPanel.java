package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;

public class EnumPropertyPanel<T extends Enum<T>> extends ButtonPropertyPanel<T> {

    private final Class<T> enumType;

    public EnumPropertyPanel(String propertyName, Class<T> enumType, PropertyParameters<T> params) {
        super(propertyName, params);
        this.enumType = enumType;
    }

    @Override
    protected String toString(T value) {
        return value == null ? "" : value.toString();
    }

    @Override
    protected T fromString(String string) {
        return T.valueOf(enumType, string);
    }

    @Override
    public void onTextChanged(GameObject obj, String before, String after) {
        Button button = (Button) obj;

        var buttonContext = button.getContent();
        button.setBounds(buttonContext.getTextRenderer().getFont().textSize(buttonContext.getText(),
                buttonContext.getHorizontalAlignment(), buttonContext.getVerticalAlignment()));

        recalculateBounds();
    }

    @Override
    public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
        int increment;
        if (button == Mouse.instance().mouseButton1())
            increment = 1;
        else if (button == Mouse.instance().mouseButton2())
            increment = -1;
        else
            return;

        Button buttonObj = (Button) obj;

        T currentValue = fromString(buttonObj.getContent().getText());

        T[] values = enumType.getEnumConstants();

        for (int i = 0; i < values.length; ++i) {
            if (currentValue.equals(values[i])) {
                buttonObj.getContent().setText(toString(values[Math.floorMod(i + increment, values.length)]));
                return;
            }
        }
    }
}