package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class Texture extends AbstractDisposable {

    private static int currentId;

    private int id;
    private float imageWidth;
    private float imageHeight;
    private byte[][] alphaArray;

    private Texture() {
    }

    public static Texture create(int width, int height, Disposer disposer) {
        Texture texture = new Texture();
        texture.loadTexture(null, width, height);
        disposer.register(texture);
        return texture;
    }

    public static Texture create(URL url, Disposer disposer) throws IOException {
        return create(load(url), disposer);
    }

    public static Texture create(BufferedImage image, Disposer disposer) {
        Texture texture = new Texture();
        texture.loadTexture(createFlipped(image));
        disposer.register(texture);
        return texture;
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

    public static BufferedImage load(URL url) throws IOException {
        return ImageIO.read(url);
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

    public int getId() {
        return id;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public float getImageHeight() {
        return imageHeight;
    }

    public boolean isVisible(int pixelX, int pixelY) {
        return getAlpha(pixelX, pixelY) != 0;
    }

    public boolean hasAlpha() {
        return alphaArray != null;
    }

    public byte getAlpha(int pixelX, int pixelY) {
        if (!hasAlpha())
            return Byte.MAX_VALUE;

        if (0 <= pixelX && pixelX < imageWidth && 0 <= pixelY && pixelY < imageHeight) {
            return alphaArray[pixelX][pixelY];
        }

        return 0;
    }

    public void onDispose() {
        OpenGL.instance().glDeleteTextures(id);
    }

    private byte[][] getAlpha(BufferedImage img) {
        Raster raster = img.getAlphaRaster();
        if (raster == null)
            return null;
        byte[][] alphaArray = new byte[raster.getWidth()][raster.getHeight()];

        int[] alphaPixel = new int[raster.getNumBands()];
        for (int x = 0; x < raster.getWidth(); x++) {
            for (int y = 0; y < raster.getHeight(); y++) {
                raster.getPixel(x, y, alphaPixel);
                alphaArray[x][y] = (byte) alphaPixel[0];
            }
        }

        return alphaArray;
    }

    private void loadTexture(BufferedImage image) {
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        alphaArray = getAlpha(image);

        int[] pixels = new int[image.getWidth() * image.getHeight()];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); // 4 for RGBA, 3 for
        // RGB

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

        loadTexture(buffer, image.getWidth(), image.getHeight());
    }

    private void loadTexture(ByteBuffer buffer, int width, int height) {
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

    public void bind() {
        bind(id);
    }

    public void unbind() {
        unbind(id);
    }
}