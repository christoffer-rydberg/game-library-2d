package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.Rectangle;
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

    /**
     * Creates a small as possible quad that covers the specific area while respecting the aspect ratio.
     */
    public static Quad coverArea(Rectangle area, float ratioWidth, float ratioHeight, Disposer disposer) {
        var ratio = ratioWidth / ratioHeight;

        var boundsWidth = area.width();
        var boundsHeight = area.height();

        var actualWidth = Math.max(area.width(), boundsHeight * ratio);
        var actualHeight = Math.max(area.height(), boundsWidth / ratio);

        var paddedBackgroundBounds = area.pad(
                (actualWidth - boundsWidth) / 2f,
                (actualHeight - boundsHeight) / 2f);

        return Quad.create(paddedBackgroundBounds, disposer);
    }
}
