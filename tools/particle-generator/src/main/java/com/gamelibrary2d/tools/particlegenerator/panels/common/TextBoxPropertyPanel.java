package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.objects.TextBox;
import com.gamelibrary2d.objects.TextObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.rendering.HorizontalAlignment;
import com.gamelibrary2d.rendering.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

import java.util.ArrayList;
import java.util.List;

public abstract class TextBoxPropertyPanel<T> extends StackPanel {

    private final static float MARGIN = 75;
    private final static HorizontalAlignment HORIZONTAL_ALIGNMENT = HorizontalAlignment.LEFT;
    private final static VerticalAlignment VERTICAL_ALIGNMENT = VerticalAlignment.TOP;

    private final Rectangle minBounds;
    private final List<TextBox> textBoxes = new ArrayList<>();
    private final PropertyParameters<T> params;

    TextBoxPropertyPanel(String propertyName, PropertyParameters<T> params) {

        super(Orientation.HORIZONTAL, MARGIN);

        this.params = params;

        Font font = Fonts.getDefaultFont();

        minBounds = font.textSize("N/A", HORIZONTAL_ALIGNMENT, VERTICAL_ALIGNMENT);

        TextObject label = new TextObject();
        label.setHorizontalAlignment(HORIZONTAL_ALIGNMENT);
        label.setVerticalAlignment(VERTICAL_ALIGNMENT);
        label.setTextRenderer(new TextRenderer(font));
        label.setFontColor(Color.WHITE);
        label.setText(propertyName + ":");

        add(label);

        for (int i = 0; i < params.getCount(); ++i) {
            params.setParameter(i, params.getParameter(i));
            TextBox textField = new TextBox();
            textField.setHorizontalAlignment(HORIZONTAL_ALIGNMENT);
            textField.setVerticalAlignment(VERTICAL_ALIGNMENT);
            textField.setTextRenderer(new TextRenderer(font));
            textField.setFontColor(Color.WHITE);
            textField.setText("N/A");
            textField.setFocusedTextRenderer(new TextRenderer(font));
            textField.setFocusedFontColor(new Color(0, 1, 1, 1));
            textField.setBounds(font.textSize(textField.getText(), textField.getHorizontalAlignment(),
                    textField.getVerticalAlignment()));
            textField.addTextChangedListener((sender, x, y) -> update((TextBox) sender));
            textBoxes.add(textField);
            add(textField, i == 0 ? 175 : MARGIN);
        }
    }

    @Override
    public void onUpdate(float deltaTime) {
        if (params.updateIfChanged()) {
            for (int i = 0; i < textBoxes.size(); ++i) {
                textBoxes.get(i).setText(toString(params.getParameter(i)));
            }
        }
        super.onUpdate(deltaTime);
    }

    private void update(TextBox textBox) {
        try {
            params.setParameter(textBoxes.indexOf(textBox), fromString(textBox.getText()));
            params.updateSetting();
            textBox.setFontColor(Color.WHITE);
        } catch (Exception e) {
            textBox.setFontColor(Color.RED);
        }

        Rectangle textBounds = textBox.getTextRenderer().getFont().textSize(textBox.getText(),
                textBox.getHorizontalAlignment(), textBox.getVerticalAlignment());

        // Make sure that the bounds are not too small (e.g. for empty string).
        if (textBounds.getWidth() < minBounds.getWidth()) {
            textBounds = minBounds;
        }

        // Update bounds of TextBox
        textBox.setBounds(textBounds);

        // Recalculate bounds of panel
        recalculateBounds();
    }

    protected abstract String toString(T value);

    protected abstract T fromString(String string);
}