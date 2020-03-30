package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;

public class Surfaces {
    private static Surface button;
    private static Surface inputField;

    public static void create(Disposer disposer) {
        button = Quad.create(Settings.BUTTON_SIZE, disposer);
        inputField = Quad.create(Settings.INPUT_FIELD_SIZE, disposer);
    }

    public static Surface button() {
        return button;
    }

    public static Surface inputField() {
        return inputField;
    }
}
