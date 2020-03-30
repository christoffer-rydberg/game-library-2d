package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Font;

public class Fonts {
    private static Font button;
    private static Font inputField;

    public static void create(Disposer disposer) {
        button = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 32), disposer);
        inputField = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 24), disposer);
    }

    public static Font button() {
        return button;
    }

    public static Font inputField() {
        return inputField;
    }
}
