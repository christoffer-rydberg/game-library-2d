package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.tools.particlegenerator.properties.IntegerProperty;
import com.gamelibrary2d.widgets.TextBox;

public class IntegerPropertyTextBox extends TextBox {
    private static final Color VALID = Color.WHITE;
    private static final Color INVALID = Color.RED;

    private final IntegerProperty property;

    private float cachedValue;

    public IntegerPropertyTextBox(TextRenderer textRenderer, IntegerProperty property) {
        super(textRenderer);
        this.property = property;
        cachedValue = property.get();
        setFontColor(VALID);
        setText(toString(cachedValue));
        addTextChangedListener(this::onTextChanged);
    }

    public static String toString(float value) {
        return value == (int) value
                ? Integer.toString((int) value)
                : Float.toString(value);
    }

    private void updateLabel() {
        var value = property.get();
        if (cachedValue != value) {
            cachedValue = value;
            setText(toString(value));
        }
    }

    private void onTextChanged(String before, String after) {
        try {
            property.set(Integer.parseInt(after));
            setFontColor(VALID);
        } catch (Exception e) {
            setFontColor(INVALID);
        }
    }

    @Override
    public void render(float alpha) {
        updateLabel();
        super.render(alpha);
    }
}