package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.framework.OpenGL;

public class OpenGLState {
    private static int boundTexture = 0;
    private static int boundFrameBuffer = 0;

    public static int bindTexture(int id) {
        int previous = boundTexture;

        if (previous != id) {
            OpenGL.instance().glBindTexture(OpenGL.GL_TEXTURE_2D, id);
            boundTexture = id;
        }

        return previous;
    }

    public static void unbindTexture(int id) {
        if (boundTexture != id) {
            OpenGL.instance().glBindTexture(OpenGL.GL_TEXTURE_2D, 0);
            boundTexture = 0;
        }
    }

    public static int bindFrameBuffer(int id) {
        int previous = boundFrameBuffer;

        if (previous != id) {
            OpenGL.instance().glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, id);
            boundFrameBuffer = id;
        }

        return previous;
    }

    public static void unbindFrameBuffer(int id) {
        if (boundFrameBuffer != id) {
            OpenGL.instance().glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, 0);
            boundFrameBuffer = 0;
        }
    }
}
