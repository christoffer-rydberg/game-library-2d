package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.rendering.HorizontalAlignment;
import com.gamelibrary2d.rendering.VerticalAlignment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Font {

    private final Texture texture;
    private final Map<Character, Surface> quads;
    private final Point textSizeOutput = new Point();
    private float textHeight;

    public Font(Texture texture, Map<Character, Surface> quads) {
        this.texture = texture;
        this.quads = quads;
        textHeight = quads.values().iterator().next().getBounds().getHeight();
    }

    public static Font create(java.awt.Font font, Disposer disposer) {
        // Create an image for each character
        FontMetrics fontMetrics = createFontMetrics(font);

        BufferedImage[] charImages = createCharImages(fontMetrics, Color.WHITE);

        // Compute the width and height of the font texture
        final int padding = 1;
        int imageWidth = 0;
        int imageHeight = 0;
        for (int i = 0; i < charImages.length; ++i) {
            BufferedImage charImage = charImages[i];
            if (charImage == null)
                charImage = charImages[(int) '_'];
            imageWidth += charImage.getWidth() + padding;
            imageHeight = Math.max(imageHeight, charImage.getHeight());
        }

        BufferedImage textureImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = textureImage.createGraphics();

        int currentTextureWidth = 0;
        Map<Character, Surface> quads = new HashMap<>();
        for (int i = 0; i < charImages.length; ++i) {

            BufferedImage charImage = charImages[i];
            if (charImage == null)
                charImage = charImages[(int) '_'];

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            graphics.drawImage(charImage, currentTextureWidth, 0, null);

            float textureStart = (float) currentTextureWidth / imageWidth;
            currentTextureWidth += charWidth;
            float textureEnd = (float) currentTextureWidth / imageWidth;
            currentTextureWidth += padding;

            Surface quad = Quad.create(new Rectangle(0, 0, charWidth, charHeight),
                    new Rectangle(textureStart, 0, textureEnd, (float) charHeight / imageHeight), disposer);

            quads.put((char) i, quad);
        }

        return new Font(Texture.create(textureImage, disposer), quads);
    }

    private static BufferedImage[] createCharImages(FontMetrics fontMetrics, Color color) {
        final int size = 256 + 8;

        BufferedImage[] charImages = new BufferedImage[size];

        // Skip control codes (ASCII 0 to 31)
        for (int i = 32; i < size; i++) {
            if (i == 127)
                continue; // ASCII 127 is a DEL control code that we can skip
            BufferedImage charImage = createCharImage(fontMetrics, color, (char) i);
            if (charImage != null)
                charImages[i] = charImage;
        }

        return charImages;
    }

    private static BufferedImage createCharImage(FontMetrics fontMetrics, Color color, char c) {
        int charWidth = fontMetrics.charWidth(c);
        if (charWidth == 0) {
            return null;
        }

        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int charHeight = ascent + descent;

        BufferedImage image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createFontGraphics(image, fontMetrics.getFont());
        g.setPaint(color);
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

    /**
     * Determines if the specified index of the specified text holds a 'new line'
     * separator.
     *
     * @param text  The text.
     * @param index The index.
     * @return True if a 'new line' separator is found, false otherwise.
     */
    public static boolean isNewLine(String text, int index) {
        return getNewLineSize(text, index) > 0;
    }

    /**
     * Gets the character size of the 'new line' separator in the specified text at
     * the specified index.
     *
     * @param text  The text.
     * @param index The index.
     * @return The size, in number of characters, of the 'new line' separator or 0
     * if the specified index is not a 'new line' separator.
     */
    public static int getNewLineSize(String text, int index) {
        return text.charAt(index) == '\n' ? 1 : 0;
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

            float width = quad.getBounds().getWidth();

            ModelMatrix.instance().translatef(width, 0, 0);
        }

        ModelMatrix.instance().popMatrix();
    }

    public float textHeight() {
        return textHeight;
    }

    public Rectangle textSize(String text) {
        textSize(text, textSizeOutput);
        return new Rectangle(0, 0, textSizeOutput.getX(), textSizeOutput.getY());
    }

    public Rectangle textSize(String text, HorizontalAlignment horizontal, VerticalAlignment vertical) {
        textSize(text, textSizeOutput);
        float offsetX = offsetFromHorizontalAlignment(horizontal, textSizeOutput.getX());
        float offsetY = offsetFromVerticalAlignment(vertical, textSizeOutput.getY());
        return new Rectangle(offsetX, offsetY, textSizeOutput.getX() + offsetX, textSizeOutput.getY() + offsetY);
    }

    public void textSize(String text, Point diameterOutput) {
        float width = 0, height = 0;
        for (int i = 0; i < text.length(); ++i) {
            Surface quad = quads.get(text.charAt(i));
            if (quad == null)
                continue;
            Rectangle bounds = quad.getBounds();
            width += bounds.getWidth();
            height = Math.max(height, bounds.getHeight());
        }
        diameterOutput.set(width, height);
    }

    public void textSize(String text, int offset, int len, Point diameterOutput) {
        int end = Math.min(offset + len, text.length());
        float width = 0, height = 0;
        for (int i = offset; i < end; ++i) {
            Surface quad = quads.get(text.charAt(i));
            if (quad == null)
                continue;
            Rectangle bounds = quad.getBounds();
            width += bounds.getWidth();
            height = Math.max(height, bounds.getHeight());
        }
        diameterOutput.set(width, height);
    }

    public float textWidth(String text) {
        float width = 0;
        for (int i = 0; i < text.length(); ++i) {
            Surface quad = quads.get(text.charAt(i));
            if (quad == null)
                continue;
            Rectangle bounds = quad.getBounds();
            width += bounds.getWidth();
        }
        return width;
    }

    /**
     * Gets the end index of a row from the specified text, given the specified
     * start index and row pixel width.
     *
     * @param text           The text.
     * @param startIndex     The (inclusive) start index of the row.
     * @param pixelWidth     The pixel width of the row.
     * @param breakOnNewLine Determines if a 'new line'-separator should end the
     *                       row.
     * @return The (exclusive) end index of the row.
     */
    public int getRowEnd(String text, int startIndex, float pixelWidth, boolean breakOnNewLine) {
        final int length = text.length();
        while (startIndex < length && pixelWidth > 0) {
            if (breakOnNewLine && isNewLine(text, startIndex))
                break;
            Surface quad = quads.get(text.charAt(startIndex));
            if (quad != null)
                pixelWidth -= quad.getBounds().getWidth();
            if (pixelWidth > 0)
                ++startIndex;
        }
        return startIndex;
    }

    public float textWidth(String text, int offset, int len) {
        int end = Math.min(offset + len, text.length());
        float width = 0;
        for (int i = offset; i < end; ++i) {
            char c = text.charAt(i);
            Surface quad = quads.get(c);
            if (quad == null)
                continue;
            Rectangle bounds = quad.getBounds();
            width += bounds.getWidth();
        }
        return width;
    }

    private float offsetFromHorizontalAlignment(HorizontalAlignment horizontal, float textSize) {
        switch (horizontal) {
            case RIGHT:
                return -textSize;
            case CENTER:
                return -textSize / 2;
            default:
                return 0;
        }
    }

    private float offsetFromVerticalAlignment(VerticalAlignment vertical, float textSize) {
        switch (vertical) {
            case TOP:
                return -textSize;
            case CENTER:
                return -textSize / 2;
            default:
                return 0;
        }
    }
}