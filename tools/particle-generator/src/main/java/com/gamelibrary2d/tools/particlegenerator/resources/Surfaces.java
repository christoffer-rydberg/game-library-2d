package com.gamelibrary2d.tools.particlegenerator.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.tools.particlegenerator.widgets.Box;

public class Surfaces {
    private static Box defaultCheckbox;
    private static Box cornerCheckbox;
    private static Surface propertyBaseLine;

    public static void create(Disposer disposer) {
        defaultCheckbox = Box.create(Rectangle.centered(14f, 14f), disposer);
        cornerCheckbox = Box.create(Rectangle.centered(12f, 12f), disposer);
        propertyBaseLine = Quad.create(Bounds.PROPERTY_BASE_LINE, disposer);
    }

    public static Box cornerCheckbox() {
        return cornerCheckbox;
    }

    public static Box defaultCheckbox() {
        return defaultCheckbox;
    }

    public static Surface propertyBaseLine() {
        return propertyBaseLine;
    }
}
