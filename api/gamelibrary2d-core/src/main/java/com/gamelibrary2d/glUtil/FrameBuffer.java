package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.resources.Texture;

public class FrameBuffer implements Disposable {
    private static int bound;

    private final int fbo;
    private final Texture texture;

    private int prevBound = 0;

    private FrameBuffer(int fbo, Texture texture, Disposer disposer) {
        this.fbo = fbo;
        this.texture = texture;
        disposer.registerDisposal(this);
    }

    public static FrameBuffer create(Texture texture, Disposer disposer) {
        var openGl = OpenGL.instance();

        // Create a frame buffer
        int fbo = openGl.glGenFramebuffers();
        openGl.glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, fbo);

        // Attach a texture to the frame buffer
        openGl.glFramebufferTexture2D(OpenGL.GL_FRAMEBUFFER, OpenGL.GL_COLOR_ATTACHMENT0, OpenGL.GL_TEXTURE_2D, texture.getId(), 0);

        // Unbind
        openGl.glBindRenderbuffer(OpenGL.GL_RENDERBUFFER, 0);
        openGl.glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, 0);

        return new FrameBuffer(fbo, texture, disposer);
    }

    public Texture getTexture() {
        return texture;
    }

    public void bind() {
        if (bound != fbo) {
            OpenGL.instance().glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, fbo);
            prevBound = bound;
            bound = fbo;
        }
    }

    public void unbind(boolean restorePrevious) {
        bound = restorePrevious ? prevBound : 0;
        OpenGL.instance().glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, bound);
    }

    public void clear() {
        OpenGL.instance().glClear(OpenGL.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        OpenGL.instance().glDeleteFramebuffers(fbo);
    }
}