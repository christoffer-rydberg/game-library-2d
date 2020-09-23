package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.util.BlendMode;

public class OpenGLUtils {

    private static BlendMode currentBlendMode;

    public static void applyBlendMode(BlendMode blendMode) {

        if (blendMode == currentBlendMode) {
            return;
        }

        boolean blendEnabled = currentBlendMode != null && currentBlendMode != BlendMode.NONE;

        currentBlendMode = blendMode;

        if (blendMode == null)
            return;

        OpenGL openGL = OpenGL.instance();

        switch (blendMode) {

            case NONE:
                openGL.glDisable(OpenGL.GL_BLEND);
                break;

            case TRANSPARENT:
                if (!blendEnabled)
                    openGL.glEnable(OpenGL.GL_BLEND);
                openGL.glBlendFunc(OpenGL.GL_SRC_ALPHA, OpenGL.GL_ONE_MINUS_SRC_ALPHA);
                break;

            case ADDITIVE:
                if (!blendEnabled)
                    openGL.glEnable(OpenGL.GL_BLEND);
                openGL.glBlendFunc(OpenGL.GL_SRC_ALPHA, OpenGL.GL_ONE);
                break;

            case MASKED:
                if (!blendEnabled)
                    openGL.glEnable(OpenGL.GL_BLEND);
                openGL.glBlendFunc(OpenGL.GL_ZERO, OpenGL.GL_ONE_MINUS_SRC_ALPHA);
                break;
        }
    }
}