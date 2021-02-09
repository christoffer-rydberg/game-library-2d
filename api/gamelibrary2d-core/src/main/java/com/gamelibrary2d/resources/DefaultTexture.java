package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.*;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import static com.gamelibrary2d.framework.OpenGL.*;

public class DefaultTexture extends AbstractDisposable implements Texture {
    private final int id;
    private final int width;
    private final int height;

    private DefaultTexture(ByteBuffer buffer, int width, int height, int channels) {
        this.width = width;
        this.height = height;

        // Create a new texture object in memory and bind it
        id = OpenGL.instance().glGenTextures();
        bind();

        // Setup the ST coordinate system
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_S, OpenGL.GL_CLAMP_TO_EDGE);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_T, OpenGL.GL_CLAMP_TO_EDGE);

        // Setup what to do when the texture has to be scaled
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MAG_FILTER, OpenGL.GL_LINEAR);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MIN_FILTER, OpenGL.GL_LINEAR);

        int format;
        int format8;
        if (channels == 3) {
            if ((this.width & 3) != 0) {
                OpenGL.instance().glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (this.width & 1));
            }
            format = GL_RGB;
            format8 = GL_RGB8;
        } else {
            // All RGB bytes are aligned to each other and each component is 1 byte
            OpenGL.instance().glPixelStorei(OpenGL.GL_UNPACK_ALIGNMENT, 1);

            format = GL_RGBA;
            format8 = GL_RGBA8;
        }

        // Upload the texture data
        OpenGL.instance().glTexImage2D(
                OpenGL.GL_TEXTURE_2D,
                0,
                format8,
                width, height,
                0,
                format,
                OpenGL.GL_UNSIGNED_BYTE,
                buffer);
    }

    public static DefaultTexture create(int width, int height, Disposer disposer) {
        DefaultTexture texture = new DefaultTexture(null, width, height, 4);
        disposer.registerDisposal(texture);
        return texture;
    }

    public static DefaultTexture create(URL url, Disposer disposer) throws IOException {
        return create(url, Runtime.getFramework().createImageReader(), disposer);
    }

    public static DefaultTexture create(URL url, ImageReader imageReader, Disposer disposer) throws IOException {
        try (InputStream stream = url.openStream()) {
            Image img = imageReader.read(stream);
            return create(img, disposer);
        }
    }

    public static DefaultTexture create(Image image, Disposer disposer) {
        DefaultTexture texture = new DefaultTexture(image.getData(), image.getWidth(), image.getHeight(), image.getChannels());
        disposer.registerDisposal(texture);
        return texture;
    }

    public static DefaultTexture create(Renderable r, float alpha, Rectangle area, Disposer disposer) {
        Disposer frameBufferDisposer = new DefaultDisposer();

        try {
            DefaultTexture texture = DefaultTexture.create((int) area.getWidth(), (int) area.getHeight(), disposer);
            FrameBuffer frameBuffer = FrameBuffer.create(texture, frameBufferDisposer);

            frameBuffer.bind();

            // Fix for incorrect alpha blending. See:
            // https://community.khronos.org/t/alpha-blending-issues-when-drawing-frame-buffer-into-default-buffer/73958/3
            OpenGL.instance().glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().clearMatrix();
            ModelMatrix.instance().translatef(-area.getLowerX(), -area.getLowerY(), 0);
            r.render(alpha);
            ModelMatrix.instance().popMatrix();
            frameBuffer.unbind(true);

            return texture;
        } finally {
            frameBufferDisposer.dispose();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    protected void onDispose() {
        OpenGL.instance().glDeleteTextures(id);
    }
}