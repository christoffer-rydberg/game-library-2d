package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.tools.particlegenerator.properties.GenericProperty;
import com.gamelibrary2d.components.widgets.AbstractWidget;
import com.gamelibrary2d.components.widgets.Label;

public class EnumWidget<T extends Enum<T>> extends AbstractWidget<Label> {
    private final T[] values;
    private final GenericProperty<T> property;

    private T cachedValue;
    private int currentIndex;

    public EnumWidget(Class<T> enumType, Label label, GenericProperty<T> property) {
        this.values = enumType.getEnumConstants();
        this.property = property;
        setContent(label);
        updateLabel();
    }

    private void updateLabel() {
        T value = property.get();
        if (cachedValue != value) {
            cachedValue = value;
            currentIndex = -1;
            for (int i = 0; i < values.length; ++i) {
                if (values[i].equals(value)) {
                    currentIndex = i;
                    break;
                }
            }

            getContent().setText(value.toString());
        }
    }

    @Override
    public void onRenderUnprojected(float alpha) {
        updateLabel();
        super.onRenderUnprojected(alpha);
    }

    @Override
    protected void onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerDown(id, button, x, y, projectedX, projectedY);
        currentIndex = (currentIndex + 1) % values.length;
        cachedValue = values[currentIndex];
        property.set(cachedValue);
        getContent().setText(cachedValue.toString());
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }
}
