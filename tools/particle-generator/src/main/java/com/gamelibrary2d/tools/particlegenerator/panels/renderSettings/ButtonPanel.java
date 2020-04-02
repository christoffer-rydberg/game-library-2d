package com.gamelibrary2d.tools.particlegenerator.panels.renderSettings;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.eventlisteners.MouseButtonReleaseListener;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderable.Label;
import com.gamelibrary2d.eventlisteners.TextChangedListener;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

class ButtonPanel extends AbstractPanel<GameObject> {

    ButtonPanel(String propertyName, String buttonText,
                MouseButtonReleaseListener mouseListener, TextChangedListener textListener) {

        var labelContext = new Label();
        labelContext.setVerticalAlignment(VerticalAlignment.TOP);
        labelContext.setHorizontalAlignment(HorizontalAlignment.LEFT);
        labelContext.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        labelContext.setFontColor(Color.WHITE);
        labelContext.setText(propertyName + ":");

        var label = new BasicObject<>(labelContext);
        label.setBounds(labelContext.getTextRenderer().getFont().textSize(
                labelContext.getText(),
                labelContext.getHorizontalAlignment(),
                labelContext.getVerticalAlignment()));
        add(label);

        int offset = 150;

        Font font = Fonts.getDefaultFont();

        Button button = new Button();

        var buttonContext = button.getContent();
        buttonContext.setVerticalAlignment(VerticalAlignment.TOP);
        buttonContext.setHorizontalAlignment(HorizontalAlignment.LEFT);
        buttonContext.setTextRenderer(new TextRenderer(font));
        buttonContext.setFontColor(Color.WHITE);
        buttonContext.setText(buttonText);
        button.setPosition(offset, 0);
        button.setBounds(font.textSize(buttonContext.getText(),
                buttonContext.getHorizontalAlignment(), buttonContext.getVerticalAlignment()));
        button.addMouseButtonReleaseListener(mouseListener);
        if (textListener != null) buttonContext.addTextChangedListener(textListener);

        add(button);
    }
}