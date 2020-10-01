package com.gamelibrary2d.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.events.TextChanged;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Label implements Bounded, Renderable {
    private final List<TextChanged> textChangedListeners = new CopyOnWriteArrayList<>();
    private final Point textPositionOffset = new Point();
    private float rowWidth, rowHeight;
    private String text = "";
    private TextRenderer textRenderer;

    private HorizontalTextAlignment horizontalAlignment = HorizontalTextAlignment.LEFT;
    private VerticalTextAlignment verticalAlignment = VerticalTextAlignment.BASE_LINE;

    private Color fontColor;

    private String breakString;
    private int textOffset;
    private int rowCount = -1;
    private int charactersRendered;
    private Renderable background;

    public Label() {

    }

    public Label(TextRenderer textRenderer) {
        setTextRenderer(textRenderer);
    }

    public Label(String text, TextRenderer textRenderer) {
        setText(text);
        setTextRenderer(textRenderer);
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

    public void addTextChangedListener(TextChanged listener) {
        textChangedListeners.add(listener);
    }

    public void removeTextChangedListener(TextChanged listener) {
        textChangedListeners.remove(listener);
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public Point getTextPositionOffset() {
        return textPositionOffset;
    }

    public void setAlignment(HorizontalTextAlignment horizontal, VerticalTextAlignment vertical) {
        setHorizontalAlignment(horizontal);
        setVerticalAlignment(vertical);
    }

    public HorizontalTextAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalTextAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public Renderable getBackground() {
        return background;
    }

    public void setBackground(Renderable background) {
        this.background = background;
    }

    public VerticalTextAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalTextAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public float getRowWidth() {
        return rowWidth;
    }

    public void setRowWidth(float rowWidth) {
        this.rowWidth = rowWidth;
    }

    public float getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(float rowMargin) {
        this.rowHeight = rowMargin;
    }

    public int getCharactersRenderers() {
        return charactersRendered;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getTextOffset() {
        return textOffset;
    }

    public void setTextOffset(int textOffset) {
        this.textOffset = textOffset;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldValue = this.text;
        this.text = text;
        for (TextChanged listener : textChangedListeners) {
            listener.onTextChanged(oldValue, text);
        }
    }

    /**
     * Rendering will break on rows matching the specified break string. This is
     * useful when creating dialogs. Rendering the next part of the dialog is done
     * by setting the {@link #setTextOffset(int) text offset} to the first index
     * after the break string.
     *
     * @return The break string. Null means that this functionality is disabled.
     */
    public String getBreakString() {
        return breakString;
    }

    /**
     * Sets the specified {@link #getBreakString() break string}.
     *
     * @param breakString The break string, or null to disable this functionality.
     */
    public void setBreakString(String breakString) {
        this.breakString = breakString;
    }

    private boolean isBreakString(String text, int offset, int len) {
        if (breakString == null || len != breakString.length())
            return false;
        for (int i = 0; i < len; ++i) {
            if (text.charAt(offset + i) != breakString.charAt(i))
                return false;
        }
        return true;
    }

    public void render(float alpha) {
        if (background != null) {
            background.render(alpha);
        }
        if (textRenderer != null) {
            onRender(alpha);
        }
    }

    private void onRender(float alpha) {
        if (fontColor != null) {
            textRenderer.getParameters().setRgba(fontColor);
        }

        iterateRows(true, alpha, null);
    }

    private Rectangle getTextBounds(Font font, int offset, int len) {
        var textWidth = font.getTextWidth(text, offset, len);
        float offsetX = offsetFromHorizontalAlignment(textWidth);
        float offsetY = offsetFromVerticalAlignment(font);
        return new Rectangle(
                offsetX,
                offsetY - font.getDescent(),
                textWidth + offsetX,
                offsetY + font.getAscent());
    }

    public Rectangle getTextBounds() {
        var output = new TextBoundsOutput();
        iterateRows(false, 1f, output);
        return output.bounds;
    }

    private void iterateRows(boolean render, float alpha, TextBoundsOutput output) {
        final float rowWidth = this.rowWidth > 0 ? this.rowWidth : Integer.MAX_VALUE;
        final float rowHeight = this.rowHeight > 0 ? this.rowHeight : textRenderer.getFont().getHeight();
        final var textRenderer = this.getTextRenderer();
        final Font font = textRenderer.getFont();
        final int rowCount = getRowCount() > 0 ? getRowCount() : Integer.MAX_VALUE;

        int currentRow = 0;
        int rowStart = getTextOffset();
        while (rowStart < text.length() && currentRow < rowCount) {
            int rowEnd = getRowEnd(text, rowStart, rowWidth, true);

            var endOfText = rowEnd == text.length();
            int newLineSize = endOfText ? 0 : getNewLineSize(text, rowEnd);
            boolean newLineSeparatorFound = newLineSize > 0;
            if (!endOfText && !newLineSeparatorFound) {
                // Back up to avoid cutting words
                while (text.charAt(rowEnd) != ' ') {
                    --rowEnd;
                }
            }

            int len = rowEnd - rowStart;

            boolean renderBreak = isBreakString(text, rowStart, len);

            if (!renderBreak) {
                if (output != null) {
                    output.expandBounds(getTextBounds(font, rowStart, len).move(0, -currentRow * rowHeight));
                }

                if (render) {
                    renderRow(textRenderer, alpha, font.getTextWidth(text, rowStart, len), rowHeight, rowStart, len);
                }
            }

            rowStart = newLineSeparatorFound ? rowEnd + newLineSize : consumeSeparators(text, rowEnd);

            if (renderBreak) {
                if (currentRow != 0) {
                    ++currentRow;
                    break;
                }
            } else {
                ++currentRow;
            }
        }

        if (render) {
            charactersRendered = rowStart;
        }
    }

    private int consumeSeparators(String text, int index) {
        while (index < text.length() && text.charAt(index) == ' ')
            ++index;
        return index;
    }

    private void renderRow(TextRenderer textRenderer, float alpha,
                           float rowWidth, float rowHeight, int offset, int len) {
        final float xOffset = offsetFromHorizontalAlignment(rowWidth) + textPositionOffset.getX();
        final float yOffset = offsetFromVerticalAlignment(textRenderer.getFont())
                + textPositionOffset.getY();

        if (xOffset != 0 || yOffset != 0) {
            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().translatef(xOffset, yOffset, 0);
        }

        textRenderer.setText(text, offset, len);
        textRenderer.render(alpha);

        if (xOffset != 0 || yOffset != 0) {
            ModelMatrix.instance().popMatrix();
        }

        // New row
        ModelMatrix.instance().translatef(0, -rowHeight, 0);
    }

    private float offsetFromHorizontalAlignment(float size) {
        switch (horizontalAlignment) {
            case RIGHT:
                return -size;
            case CENTER:
                return -size / 2;
            default:
                return 0;
        }
    }

    private float offsetFromVerticalAlignment(Font font) {
        switch (verticalAlignment) {
            case TOP:
                return -font.getAscent();
            case CENTER:
                return (font.getAscent() + font.getDescent()) / 2 - font.getAscent();
            case BOTTOM:
                return font.getDescent();
            case BASE_LINE:
            default:
                return 0;
        }
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
            Surface quad = getTextRenderer().getFont().getQuads().get(text.charAt(startIndex));
            if (quad != null)
                pixelWidth -= quad.getBounds().width();
            if (pixelWidth > 0)
                ++startIndex;
        }
        return startIndex;
    }

    @Override
    public Rectangle getBounds() {
        return background instanceof Bounded
                ? ((Bounded) background).getBounds()
                : Rectangle.EMPTY;
    }

    private static class TextBoundsOutput {
        private Rectangle bounds;

        void expandBounds(Rectangle bounds) {
            this.bounds = this.bounds == null
                    ? bounds
                    : this.bounds.expand(bounds);
        }
    }
}