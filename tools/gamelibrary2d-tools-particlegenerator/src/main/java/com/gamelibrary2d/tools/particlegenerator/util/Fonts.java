package com.gamelibrary2d.tools.particlegenerator.util;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Font;

public class Fonts {

    private static Font defaultFont;
    private static Font menuFont;

    public static void create(Disposer disposer) {
        defaultFont = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 16), disposer);
        menuFont = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 30), disposer);
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static Font getMenuFont() {
        return menuFont;
    }

}