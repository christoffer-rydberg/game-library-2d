package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import static com.gamelibrary2d.framework.OpenGL.*;

public class DefaultTexture extends AbstractDisposable implements Texture {
    private int id;
    private int width;
    private int height;

    private DefaultTexture() {
    }

    public static DefaultTexture create(int width, int height, Disposer disposer) {
        var texture = new DefaultTexture();
        texture.loadTexture(null, width, height);
        disposer.registerDisposal(texture);
        return texture;
    }

    public static DefaultTexture create(URL url, Disposer disposer) throws IOException {
        return create(ImageIO.read(url), disposer);
    }

    public static DefaultTexture create(BufferedImage image, Disposer disposer) {
        var texture = new DefaultTexture();
        texture.loadTexture(createFlipped(image));
        disposer.registerDisposal(texture);
        return texture;
    }

    public static DefaultTexture create(Renderable r, float alpha, Rectangle area, Disposer disposer) {
        var tempResourceDisposer = new DefaultDisposer();

        try {
            var texture = DefaultTexture.create((int) area.width(), (int) area.height(), disposer);
            var frameBuffer = FrameBuffer.create(texture, tempResourceDisposer);

            frameBuffer.bind();

            // Fix for incorrect alpha blending. See:
            // https://community.khronos.org/t/alpha-blending-issues-when-drawing-frame-buffer-into-default-buffer/73958/3
            OpenGL.instance().glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().clearMatrix();
            ModelMatrix.instance().translatef(-area.xMin(), -area.yMin(), 0);
            r.render(alpha);
            ModelMatrix.instance().popMatrix();
            frameBuffer.unbind(true);

            return texture;
        } finally {
            tempResourceDisposer.dispose();
        }
    }

    /**
     * The image has the upper-left corner as reference point and the Y-axis is
     * pointing downwards. Default OpenGL uses a lower-left reference point with an
     * up-pointing Y-axis. This method is used to flip the image in order to match
     * the texture coordinates.
     */
    private static BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
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

    public void onDispose() {
        OpenGL.instance().glDeleteTextures(id);
    }

    private void loadTexture(BufferedImage image) {
        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();

        int[] pixels = new int[imageWidth * imageHeight];
        image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, image.getWidth());

        var buffer = BufferUtils.createByteBuffer(imageWidth * imageHeight * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component. Only for RGBA
            }
        }

        buffer.flip();

        loadTexture(buffer, imageWidth, imageHeight);
    }

    private void loadTexture(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;

        // Create a new texture object in memory and bind it
        id = OpenGL.instance().glGenTextures();
        bind();

        // All RGB bytes are aligned to each other and each component is 1 byte
        OpenGL.instance().glPixelStorei(OpenGL.GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data
        OpenGL.instance().glTexImage2D(OpenGL.GL_TEXTURE_2D, 0, OpenGL.GL_RGBA8, width, height, 0, OpenGL.GL_RGBA,
                OpenGL.GL_UNSIGNED_BYTE, buffer);

        // Setup the ST coordinate system
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_S, OpenGL.GL_CLAMP_TO_EDGE);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_T, OpenGL.GL_CLAMP_TO_EDGE);

        // Setup what to do when the texture has to be scaled
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MAG_FILTER, OpenGL.GL_LINEAR);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MIN_FILTER, OpenGL.GL_LINEAR);
    }
}