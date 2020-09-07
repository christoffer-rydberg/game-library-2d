package com.gamelibrary2d.tools.particlegenerator.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;

public class Surfaces {
    private static Surface propertyTextBox;

    public static void create(Disposer disposer) {
        propertyTextBox = Quad.create(Bounds.PROPERTY_TEXT_BOX, disposer);
    }

    public static Surface propertyTextBox() {
        return propertyTextBox;
    }
}
