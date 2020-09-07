package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.TextBox;

public class InputField extends AbstractWidget<TextBox> {

    public InputField(String text) {
        var background = new SurfaceRenderer(
                Surfaces.inputField(),
                Textures.inputField());

        background.getParameters().setRgba(Settings.INPUT_FIELD_COLOR);

        var content = new TextBox(text, new TextRenderer(Fonts.inputField()));
        content.setBackground(background);
        setContent(content);
    }

    public int getIntValue() {
        return Integer.parseInt(getContent().getText());
    }

    public String getStringValue() {
        return getContent().getText();
    }

}
