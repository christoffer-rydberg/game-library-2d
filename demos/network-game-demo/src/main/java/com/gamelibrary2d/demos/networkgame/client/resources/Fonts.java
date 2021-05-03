package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.resources.DefaultFont;
import com.gamelibrary2d.resources.Font;

import java.io.IOException;
import java.io.InputStream;

public class Fonts {
    private static Font button;
    private static Font inputField;
    private static Font timer;

    private static Font readFont(InputStream is, Disposer disposer) throws IOException {
        DataBuffer bytes = Read.bytes(is);
        bytes.flip();
        return DefaultFont.load(bytes, disposer);
    }

    public static void create(ResourceManager resourceManager, Disposer disposer) throws IOException {
        button = resourceManager.load("fonts/button.font", stream -> readFont(stream, disposer));
        inputField = resourceManager.load("fonts/inputField.font", stream -> readFont(stream, disposer));
        timer = resourceManager.load("fonts/inputField.font", stream -> readFont(stream, disposer));
    }

    public static Font button() {
        return button;
    }

    public static Font inputField() {
        return inputField;
    }

    public static Font timer() { return timer; }
}
