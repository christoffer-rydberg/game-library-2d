package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.renderable.Label;
import com.gamelibrary2d.renderable.TextBox;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.objects.InputObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
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
    private final List<InputObject<TextBox>> textBoxes = new ArrayList<>();
    private final PropertyParameters<T> params;

    TextBoxPropertyPanel(String propertyName, PropertyParameters<T> params) {

        super(Orientation.HORIZONTAL, MARGIN);

        this.params = params;

        Font font = Fonts.getDefaultFont();

        minBounds = font.textSize("N/A", HORIZONTAL_ALIGNMENT, VERTICAL_ALIGNMENT);

        var labelContext = new Label();
        labelContext.setHorizontalAlignment(HORIZONTAL_ALIGNMENT);
        labelContext.setVerticalAlignment(VERTICAL_ALIGNMENT);
        labelContext.setTextRenderer(new TextRenderer(font));
        labelContext.setFontColor(Color.WHITE);
        labelContext.setText(propertyName + ":");

        add(new BasicObject<>(labelContext));

        for (int i = 0; i < params.getCount(); ++i) {
            params.setParameter(i, params.getParameter(i));

            var textRenderer = new TextRenderer(font);

            var textBoxContext = new TextBox();
            textBoxContext.setText("N/A");
            textBoxContext.setHorizontalAlignment(HORIZONTAL_ALIGNMENT);
            textBoxContext.setVerticalAlignment(VERTICAL_ALIGNMENT);
            textBoxContext.setTextRenderer(textRenderer);
            textBoxContext.setFontColor(Color.WHITE);

            var textBox = new InputObject<>(textBoxContext);
            textBox.setFocusedHandler(content -> content.setFontColor(Color.GREEN));
            textBox.setUnfocusedHandler(content -> content.setFontColor(Color.WHITE));
            textBoxContext.addTextChangedListener((sender, x, y) -> update(textBox));
            textBox.setBounds(font.textSize(textBoxContext.getText(), textBoxContext.getHorizontalAlignment(),
                    textBoxContext.getVerticalAlignment()));
            textBoxes.add(textBox);
            add(textBox, i == 0 ? 175 : MARGIN);
        }
    }

    @Override
    public void onUpdate(float deltaTime) {
        if (params.updateIfChanged()) {
            for (int i = 0; i < textBoxes.size(); ++i) {
                textBoxes.get(i).getContent().setText(toString(params.getParameter(i)));
            }
        }
        super.onUpdate(deltaTime);
    }

    private void update(InputObject<TextBox> textBox) {

        var textBoxContext = textBox.getContent();
        try {
            params.setParameter(textBoxes.indexOf(textBox), fromString(textBoxContext.getText()));
            params.updateSetting();
            textBoxContext.setFontColor(Color.WHITE);
        } catch (Exception e) {
            textBoxContext.setFontColor(Color.RED);
        }

        Rectangle textBounds = textBoxContext.getTextRenderer().getFont().textSize(textBoxContext.getText(),
                textBoxContext.getHorizontalAlignment(), textBoxContext.getVerticalAlignment());

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