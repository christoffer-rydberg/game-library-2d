package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

public class Button extends AbstractWidget<Label> {

    private final Action onClick;

    public Button(String text, Action onClick) {
        this.onClick = onClick;

        var background = new SurfaceRenderer(
                Surfaces.button(),
                Textures.button());

        background.getParameters().setRgba(Settings.BUTTON_COLOR);

        var content = new Label(text, new TextRenderer(Fonts.button()));
        content.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        content.setBackground(background);
        setContent(content);
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        onClick.invoke();
    }

}