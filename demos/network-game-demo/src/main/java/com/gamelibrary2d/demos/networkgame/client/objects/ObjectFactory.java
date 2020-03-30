package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.InputObject;
import com.gamelibrary2d.objects.ObservableObject;
import com.gamelibrary2d.renderable.Label;
import com.gamelibrary2d.renderable.TextBox;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.RenderSettings;

// TODO: Replace with object classes?

public class ObjectFactory {

    public static ComposableObject<TextBox> createInputField(String text) {
        var background = new SurfaceRenderer(
                Surfaces.inputField(),
                Textures.inputField());

        var color = Settings.INPUT_FIELD_COLOR;
        background.updateSettings(RenderSettings.COLOR_R, color.getR(), color.getG(), color.getB(), color.getA());

        var inputField = new InputObject<>(new TextBox(text, new TextRenderer(Fonts.inputField())));
        inputField.getContent().setBackground(background);

        return inputField;
    }

    public static GameObject createButton(String text, Action clickAction) {
        var background = new SurfaceRenderer(
                Surfaces.button(),
                Textures.button());

        var color = Settings.BUTTON_COLOR;
        background.updateSettings(RenderSettings.COLOR_R, color.getR(), color.getG(), color.getB(), color.getA());

        var button = new ObservableObject<>(new Label(text, new TextRenderer(Fonts.button())));
        button.getContent().setBackground(background);
        button.addMouseButtonDownListener((o, b, m, x, y) -> clickAction.invoke());

        return button;
    }
}