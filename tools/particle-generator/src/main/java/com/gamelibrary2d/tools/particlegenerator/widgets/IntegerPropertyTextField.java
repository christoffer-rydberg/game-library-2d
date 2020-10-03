package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.tools.particlegenerator.properties.IntegerProperty;
import com.gamelibrary2d.widgets.TextField;

public class IntegerPropertyTextField extends TextField {
    private static final Color VALID = Color.WHITE;
    private static final Color INVALID = Color.RED;

    private final IntegerProperty property;

    private float cachedValue;

    public IntegerPropertyTextField(TextRenderer textRenderer, IntegerProperty property) {
        super(textRenderer);
        this.property = property;
        cachedValue = property.get();
        textRenderer.getParameters().setRgba(VALID);
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
            getTextRenderer().getParameters().setRgba(VALID);
        } catch (Exception e) {
            getTextRenderer().getParameters().setRgba(INVALID);
        }
    }

    @Override
    public void render(float alpha) {
        updateLabel();
        super.render(alpha);
    }
}