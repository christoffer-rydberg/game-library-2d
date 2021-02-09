package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.widgets.TextField;

public class FloatPropertyTextField extends TextField {
    private static final Color VALID = Color.WHITE;
    private static final Color INVALID = Color.RED;

    private final FloatProperty property;

    private float cachedValue;

    public FloatPropertyTextField(TextRenderer textRenderer, FloatProperty property) {
        super(textRenderer);
        this.property = property;
        cachedValue = property.get();
        textRenderer.getParameters().setColor(VALID);
        setText(toString(cachedValue));
        addTextChangedListener(this::onTextChanged);
    }

    public static String toString(float value) {
        return value == (int) value
                ? Integer.toString((int) value)
                : Float.toString(value);
    }

    private void updateLabel() {
        float value = property.get();
        if (cachedValue != value) {
            cachedValue = value;
            setText(toString(value));
        }
    }

    private void onTextChanged(String before, String after) {
        try {
            property.set(Float.parseFloat(after));
            getTextRenderer().getParameters().setColor(VALID);
        } catch (Exception e) {
            getTextRenderer().getParameters().setColor(INVALID);
        }
    }

    @Override
    public void render(float alpha) {
        updateLabel();
        super.render(alpha);
    }
}