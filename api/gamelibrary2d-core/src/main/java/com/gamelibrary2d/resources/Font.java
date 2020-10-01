package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.ShaderProgram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Font {
    private final Texture texture;
    private final Map<Character, Surface> quads;
    private final int ascent;
    private final int descent;
    private final int height;

    public Font(Texture texture, Map<Character, Surface> quads, int ascent, int descent, int height) {
        this.texture = texture;
        this.quads = Collections.unmodifiableMap(quads);
        this.ascent = ascent;
        this.descent = descent;
        this.height = height;
    }

    public static Font create(java.awt.Font font, Disposer disposer) {
        var fontMetrics = createFontMetrics(font);
        var charImages = createCharImages(fontMetrics);

        final int padding = 1;
        int imageWidth = 0;
        int imageHeight = fontMetrics.getAscent() + fontMetrics.getDescent();
        for (BufferedImage image : charImages) {
            var charImage = image;
            if (charImage == null) {
                charImage = charImages[(int) '_'];
            }
            imageWidth += charImage.getWidth() + padding;
        }

        var textureImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        var graphics = textureImage.createGraphics();

        var ascent = fontMetrics.getAscent();
        var descent = fontMetrics.getDescent();
        var height = fontMetrics.getHeight();

        int currentTextureWidth = 0;
        Map<Character, Surface> quads = new HashMap<>();
        for (int i = 0; i < charImages.length; ++i) {
            var charImage = charImages[i];
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

            var quad = Quad.create(
                    new Rectangle(0, -descent, charWidth, ascent),
                    new Rectangle(textureStart, 0, textureEnd, (float) charHeight / imageHeight),
                    disposer);

            quads.put((char) i, quad);
        }

        return new Font(Texture.create(textureImage, disposer), quads, ascent, descent, height);
    }

    private static BufferedImage[] createCharImages(FontMetrics fontMetrics) {
        final int size = 256 + 8;
        var charImages = new BufferedImage[size];

        // Skip control codes (ASCII 0 to 31)
        for (int i = 32; i < size; i++) {
            // ASCII 127 is a DEL control code that we can skip
            if (i != 127) {
                var charImage = createCharImage(fontMetrics, (char) i);
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

    public void render(ShaderProgram shaderProgram, String text, int start, int end) {
        texture.bind();

        ModelMatrix.instance().pushMatrix();

        for (int i = start; i < end; ++i) {
            Surface quad = quads.get(text.charAt(i));
            if (quad == null)
                continue;

            shaderProgram.updateModelMatrix(ModelMatrix.instance());

            quad.render(shaderProgram);

            float width = quad.getBounds().width();

            ModelMatrix.instance().translatef(width, 0, 0);
        }

        ModelMatrix.instance().popMatrix();
    }

    public Texture getTexture() {
        return texture;
    }

    public Map<Character, Surface> getQuads() {
        return quads;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getHeight() {
        return height;
    }

    public float getTextWidth(String text, int offset, int len) {
        float width = 0;
        int end = Math.min(offset + len, text.length());
        for (int i = offset; i < end; ++i) {
            var quad = quads.get(text.charAt(i));
            if (quad != null) {
                var bounds = quad.getBounds();
                width += bounds.width();
            }
        }

        return width;
    }
}