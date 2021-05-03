package com.gamelibrary2d.resources;

import com.gamelibrary2d.framework.OpenGL;

public class TextureUtil {

    private static int currentId = 0;

    public static int getBoundTextureId() {
        return currentId;
    }

    public static void bind(int id) {
        if (currentId != id) {
            OpenGL.instance().glBindTexture(OpenGL.GL_TEXTURE_2D, id);
            currentId = id;
        }
    }

    public static void unbind(int id) {
        if (currentId != id) {
            OpenGL.instance().glBindTexture(OpenGL.GL_TEXTURE_2D, 0);
            currentId = -1;
        }
    }
}
