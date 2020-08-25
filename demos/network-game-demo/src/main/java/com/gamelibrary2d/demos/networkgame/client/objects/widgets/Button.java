package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.RenderSettings;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

public class Button extends AbstractWidget<Label> {

    private final Action onClick;

    public Button(String text, Action onClick) {
        this.onClick = onClick;

        var background = new SurfaceRenderer(
                Surfaces.button(),
                Textures.button());

        var color = Settings.BUTTON_COLOR;
        background.updateSettings(RenderSettings.COLOR_R, color.getR(), color.getG(), color.getB(), color.getA());

        var content = new Label(text, new TextRenderer(Fonts.button()));
        content.setBackground(background);
        setContent(content);
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, projectedX, projectedY);
        onClick.invoke();
    }

}