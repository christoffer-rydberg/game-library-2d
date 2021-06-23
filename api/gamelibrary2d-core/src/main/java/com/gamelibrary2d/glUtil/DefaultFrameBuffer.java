package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.resources.Texture;

import java.nio.ByteBuffer;

public class DefaultFrameBuffer implements FrameBuffer {
    private final int id;
    private final Texture texture;
    private final ByteBuffer pixelReadBuffer = ByteBuffer.allocateDirect(4);

    private DefaultFrameBuffer(int id, Texture texture) {
        this.id = id;
        this.texture = texture;
    }

    public static DefaultFrameBuffer create(Texture texture, Disposer disposer) {
        OpenGL openGl = OpenGL.instance();

        // Create a frame buffer
        int fbo = openGl.glGenFramebuffers();
        openGl.glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, fbo);

        // Attach a texture to the frame buffer
        openGl.glFramebufferTexture2D(OpenGL.GL_FRAMEBUFFER, OpenGL.GL_COLOR_ATTACHMENT0, OpenGL.GL_TEXTURE_2D, texture.getId(), 0);

        // Unbind
        openGl.glBindRenderbuffer(OpenGL.GL_RENDERBUFFER, 0);
        openGl.glBindFramebuffer(OpenGL.GL_FRAMEBUFFER, 0);

        DefaultFrameBuffer frameBuffer = new DefaultFrameBuffer(fbo, texture);

        disposer.registerDisposal(frameBuffer);

        return frameBuffer;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int readPixel(int x, int y) {
        pixelReadBuffer.clear();

        OpenGL.instance().glReadPixels(
                x,
                y,
                1,
                1,
                OpenGL.GL_RGBA,
                OpenGL.GL_UNSIGNED_BYTE,
                pixelReadBuffer);

        return pixelReadBuffer.getInt(0);
    }

    @Override
    public boolean isVisible(int x, int y) {
        int pixel = readPixel(x, y);
        return (pixel & 0x000000FF) > 0;
    }

    @Override
    public void clear() {
        OpenGL.instance().glClear(OpenGL.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        OpenGL.instance().glDeleteFramebuffers(id);
    }
}