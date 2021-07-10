package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.settings.Dimensions;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;

public class Surfaces {
    private static Surface button;
    private static Surface inputField;

    public static void create(Dimensions dimensions, Disposer disposer) {
        button = Quad.create(dimensions.getButtonSize(), disposer);
        inputField = Quad.create(dimensions.getInputFieldSize(), disposer);
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
        float ratio = ratioWidth / ratioHeight;

        float boundsWidth = area.getWidth();
        float boundsHeight = area.getHeight();

        float actualWidth = Math.max(area.getWidth(), boundsHeight * ratio);
        float actualHeight = Math.max(area.getHeight(), boundsWidth / ratio);

        Rectangle paddedBackgroundBounds = area.pad(
                (actualWidth - boundsWidth) / 2f,
                (actualHeight - boundsHeight) / 2f);

        return Quad.create(paddedBackgroundBounds, disposer);
    }
}
