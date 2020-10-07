package com.gamelibrary2d.tools.particlegenerator.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.DefaultFont;
import com.gamelibrary2d.resources.Font;

public class Fonts {
    private static Font smallFont;
    private static Font defaultFont;
    private static Font largeFont;
    private static Font menuFont;

    public static void create(Disposer disposer) {
        smallFont = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 12), disposer);
        defaultFont = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 16), disposer);
        largeFont = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 20), disposer);
        menuFont = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 32), disposer);
    }

    public static Font getSmallFont() {
        return smallFont;
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static Font getLargeFont() {
        return largeFont;
    }

    public static Font getMenuFont() {
        return menuFont;
    }

}