package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.*;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.imaging.DefaultImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import static com.gamelibrary2d.framework.OpenGL.*;

public class DefaultTexture extends AbstractDisposable implements Texture {
    private final int id;
    private final int width;
    private final int height;
    private final int channels;

    private DefaultTexture(byte[] data, int width, int height, int channels) {
        this.width = width;
        this.height = height;
        this.channels = channels;

        // Create a new texture object in memory and bind it
        id = OpenGL.instance().glGenTextures();
        bind();

        // Setup the ST coordinate system
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_S, OpenGL.GL_CLAMP_TO_EDGE);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_T, OpenGL.GL_CLAMP_TO_EDGE);

        // Setup what to do when the texture has to be scaled
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MAG_FILTER, OpenGL.GL_LINEAR);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MIN_FILTER, OpenGL.GL_LINEAR);

        OpenGL.instance().glPixelStorei(GL_UNPACK_ALIGNMENT, getAlignment(channels));

        int format = getFormat(channels);
        int internalFormat = getInternalFormat(format);

        ByteBuffer buffer = null;
        if (data != null) {
            buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();
        }

        // Upload the texture data
        OpenGL.instance().glTexImage2D(
                OpenGL.GL_TEXTURE_2D,
                0,
                internalFormat,
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
        return create(url, Runtime.getFramework().createDefaultImageReader(), disposer);
    }

    public static DefaultTexture create(URL url, ImageReader imageReader, Disposer disposer) throws IOException {
        try (InputStream stream = url.openStream()) {
            return create(stream, imageReader, disposer);
        }
    }

    public static DefaultTexture create(InputStream stream, Disposer disposer) throws IOException {
        return create(stream, Runtime.getFramework().createDefaultImageReader(), disposer);
    }

    public static DefaultTexture create(InputStream stream, ImageReader imageReader, Disposer disposer) throws IOException {
        Image img = imageReader.read(stream);
        return create(img, disposer);
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

    private int getInternalFormat(int format) {
        switch (format) {
            case GL_RGB:
                return GL_RGB8;
            case GL_RGBA:
                return GL_RGBA8;
            default:
                throw new RuntimeException(String.format("Unsupported format: %d", format));
        }
    }

    private int getFormat(int channels) {
        switch (channels) {
            case 3:
                return GL_RGB;
            case 4:
                return GL_RGBA;
            default:
                throw new RuntimeException(String.format("Unsupported channels: %d", channels));
        }
    }

    private int getAlignment(int channels) {
        switch (channels) {
            case 3:
                return (this.width & 3) != 0
                        ? 2 - (this.width & 1)
                        : 1;
            case 4:
                return 4;
            default:
                throw new RuntimeException(String.format("Unsupported channels: %d", channels));
        }
    }

    @Override
    public Image loadImage() {
        int bound = TextureUtil.getBoundTextureId();
        try {
            bind();

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * channels);

            OpenGL.instance().glPixelStorei(GL_PACK_ALIGNMENT, getAlignment(channels));
            OpenGL.instance().glGetTexImage(
                    GL_TEXTURE_2D,
                    0,
                    getFormat(channels),
                    GL_UNSIGNED_BYTE,
                    buffer);

            byte[] pixels = new byte[buffer.remaining()];
            buffer.get(pixels);
            return new DefaultImage(pixels, width, height, channels);
        } finally {
            TextureUtil.bind(bound);
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