package com.gamelibrary2d.tools.particlegenerator.panels.renderSettings;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.eventlisteners.MouseReleaseListener;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.TextChangedListener;
import com.gamelibrary2d.objects.TextObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.rendering.HorizontalAlignment;
import com.gamelibrary2d.rendering.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

class ButtonPanel extends AbstractPanel<GameObject> {

    ButtonPanel(String propertyName, String buttonText,
                MouseReleaseListener mouseListener, TextChangedListener textListener) {

        TextObject label = new TextObject();
        label.setVerticalAlignment(VerticalAlignment.TOP);
        label.setHorizontalAlignment(HorizontalAlignment.LEFT);
        label.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        label.setFontColor(Color.WHITE);
        label.setText(propertyName + ":");
        label.setBounds(label.getTextRenderer().getFont().textSize(
                label.getText(),
                label.getHorizontalAlignment(),
                label.getVerticalAlignment()));
        add(label);

        int offset = 150;

        Font font = Fonts.getDefaultFont();

        Button button = new Button();
        button.setVerticalAlignment(VerticalAlignment.TOP);
        button.setHorizontalAlignment(HorizontalAlignment.LEFT);
        button.setTextRenderer(new TextRenderer(font));
        button.setFontColor(Color.WHITE);
        button.setText(buttonText);
        button.getPosition().set(offset, 0);
        button.setBounds(font.textSize(button.getText(),
                button.getHorizontalAlignment(), button.getVerticalAlignment()));
        button.addMouseReleaseListener(mouseListener);
        if (textListener != null) button.addTextChangedListener(textListener);

        add(button);
    }
}