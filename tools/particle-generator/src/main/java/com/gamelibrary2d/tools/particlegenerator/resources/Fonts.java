package com.gamelibrary2d.tools.particlegenerator.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Font;

public class Fonts {

    private static Font defaultFont;
    private static Font largeFont;
    private static Font menuFont;

    public static void create(Disposer disposer) {
        defaultFont = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 16), disposer);
        largeFont = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 20), disposer);
        menuFont = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 32), disposer);
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