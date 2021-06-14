package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.AbstractWidget;
import com.gamelibrary2d.components.widgets.TextField;

public class InputField extends AbstractWidget<TextField> {
    private final Renderer background;

    public InputField(String text) {
        background = new SurfaceRenderer<>(
                Surfaces.inputField(),
                Textures.inputField());

        background.getParameters().setColor(Settings.INPUT_FIELD_COLOR);

        TextField textField = new TextField(text, new TextRenderer(Fonts.inputField()));
        textField.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        setContent(textField);

        setBounds(background.getBounds());
    }

    public int getIntValue() {
        return Integer.parseInt(getContent().getText());
    }

    public String getStringValue() {
        return getContent().getText();
    }

    @Override
    protected void onRender(float alpha) {
        background.render(alpha);
        super.onRender(alpha);
    }

}
