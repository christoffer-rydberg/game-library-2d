package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.framework.Image;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.imaging.BufferedImageParser;
import com.gamelibrary2d.imaging.DefaultImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultFont implements Font, Serializable {
    private final Texture texture;
    private final Map<Character, Quad> quads;
    private final int ascent;
    private final int descent;
    private final int height;

    public DefaultFont(Texture texture, Map<Character, Quad> quads, int ascent, int descent, int height) {
        this.texture = texture;
        this.quads = Collections.unmodifiableMap(quads);
        this.ascent = ascent;
        this.descent = descent;
        this.height = height;
    }

    public static DefaultFont load(DataBuffer buffer, Disposer parentDisposer) {
        int ascent = buffer.getInt();
        int descent = buffer.getInt();
        int height = buffer.getInt();

        Disposer disposer = new DefaultDisposer(parentDisposer);

        int numberOfQuads = buffer.getInt();
        Map<Character, Quad> quads = new HashMap<>();
        for (int i = 0; i < numberOfQuads; ++i) {
            char character = (char) buffer.getInt();
            Rectangle bounds = deserializeRectangle(buffer);
            Rectangle textureBounds = deserializeRectangle(buffer);
            quads.put(character, Quad.create(bounds, textureBounds, disposer));
        }

        int textureWidth = buffer.getInt();
        int textureHeight = buffer.getInt();
        int textureChannels = buffer.getInt();

        byte[] bytes = new byte[buffer.getInt()];
        buffer.get(bytes, 0, bytes.length);

        Image image = new DefaultImage(bytes, textureWidth, textureHeight, textureChannels);

        Texture texture = DefaultTexture.create(image, disposer);

        return new DefaultFont(texture, quads, ascent, descent, height);
    }

    @Override
    public void serialize(DataBuffer buffer) {
        buffer.putInt(ascent);
        buffer.putInt(descent);
        buffer.putInt(height);

        buffer.putInt(quads.size());
        for (Character character : quads.keySet()) {
            Quad quad = quads.get(character);
            int charInt = (int) character;
            buffer.putInt(charInt);
            serializeRectangle(quad.getBounds(), buffer);
            serializeRectangle(quad.getTextureBounds(), buffer);
        }

        Image textureImage = texture.loadImage();
        buffer.putInt(textureImage.getWidth());
        buffer.putInt(textureImage.getHeight());
        buffer.putInt(textureImage.getChannels());

        byte[] imageData = textureImage.getData();

        buffer.putInt(imageData.length);
        buffer.put(imageData);
    }

    private static Rectangle deserializeRectangle(DataBuffer buffer) {
        return new Rectangle(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    private static void serializeRectangle(Rectangle rectangle, DataBuffer buffer) {
        buffer.putFloat(rectangle.getLowerX());
        buffer.putFloat(rectangle.getLowerY());
        buffer.putFloat(rectangle.getUpperX());
        buffer.putFloat(rectangle.getUpperY());
    }

    public static DefaultFont create(java.awt.Font font, Disposer disposer) {
        FontMetrics fontMetrics = createFontMetrics(font);
        BufferedImage[] charImages = createCharImages(fontMetrics);

        final int padding = 1;
        int imageWidth = 0;
        int imageHeight = fontMetrics.getAscent() + fontMetrics.getDescent();
        for (BufferedImage image : charImages) {
            BufferedImage charImage = image;
            if (charImage == null) {
                charImage = charImages[(int) '_'];
            }
            imageWidth += charImage.getWidth() + padding;
        }

        BufferedImage textureImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = textureImage.createGraphics();

        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int height = ascent + descent;

        int currentTextureWidth = 0;
        Map<Character, Quad> quads = new HashMap<>();
        for (int i = 0; i < charImages.length; ++i) {
            BufferedImage charImage = charImages[i];
            if (charImage == null) {
                charImage = charImages[(int) '_'];
            }

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            graphics.drawImage(charImage, currentTextureWidth, 0, null);

            float textureStart = (float) currentTextureWidth / imageWidth;
            currentTextureWidth += charWidth;
            float textureEnd = (float) currentTextureWidth / imageWidth;
            currentTextureWidth += padding;

            Quad quad = Quad.create(
                    new Rectangle(0, -descent, charWidth, ascent),
                    new Rectangle(textureStart, 0, textureEnd, (float) charHeight / imageHeight),
                    disposer);

            quads.put((char) i, quad);
        }

        Image image = new BufferedImageParser().parse(textureImage);

        Texture texture = DefaultTexture.create(image, disposer);

        return new DefaultFont(texture, quads, ascent, descent, height);
    }

    private static BufferedImage[] createCharImages(FontMetrics fontMetrics) {
        final int size = 256 + 8;
        BufferedImage[] charImages = new BufferedImage[size];

        // Skip control codes (ASCII 0 to 31)
        for (int i = 32; i < size; i++) {
            // ASCII 127 is a DEL control code that we can skip
            if (i != 127) {
                BufferedImage charImage = createCharImage(fontMetrics, (char) i);
                if (charImage != null) {
                    charImages[i] = charImage;
                }
            }
        }

        return charImages;
    }

    private static BufferedImage createCharImage(FontMetrics fontMetrics, char c) {
        int charWidth = fontMetrics.charWidth(c);
        if (charWidth == 0) {
            return null;
        }

        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int charHeight = ascent + descent;

        BufferedImage image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createFontGraphics(image, fontMetrics.getFont());
        g.setPaint(Color.WHITE);
        g.drawString(String.valueOf(c), 0, ascent);
        g.dispose();

        return image;
    }

    private static FontMetrics createFontMetrics(java.awt.Font font) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createFontGraphics(image, font);
        FontMetrics fontMetrics = g.getFontMetrics();
        g.dispose();
        return fontMetrics;
    }

    private static Graphics2D createFontGraphics(BufferedImage image, java.awt.Font font) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        return g;
    }

    @Override
    public void render(ShaderProgram shaderProgram, String text, int start, int end) {
        texture.bind();

        ModelMatrix.instance().pushMatrix();

        for (int i = start; i < end; ++i) {
            Quad quad = quads.get(text.charAt(i));
            if (quad == null)
                continue;

            shaderProgram.updateModelMatrix(ModelMatrix.instance());

            quad.render(shaderProgram);

            float width = quad.getBounds().getWidth();

            ModelMatrix.instance().translatef(width, 0, 0);
        }

        ModelMatrix.instance().popMatrix();
    }

    public Texture getTexture() {
        return texture;
    }

    public Map<Character, Quad> getQuads() {
        return quads;
    }

    @Override
    public int getAscent() {
        return ascent;
    }

    @Override
    public int getDescent() {
        return descent;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public float getTextWidth(String text, int offset, int len) {
        float width = 0;
        int end = Math.min(offset + len, text.length());
        for (int i = offset; i < end; ++i) {
            Surface surface = quads.get(text.charAt(i));
            if (surface != null) {
                Rectangle bounds = surface.getBounds();
                width += bounds.getWidth();
            }
        }

        return width;
    }
}