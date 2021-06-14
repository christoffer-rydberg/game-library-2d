package com.gamelibrary2d.components.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.renderers.ShaderParameters;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.listeners.TextChangedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Label implements Renderable {
    private final List<TextChangedListener> textChangedListeners = new CopyOnWriteArrayList<>();

    private String text = "";
    private TextRenderer textRenderer;
    private Color color;

    private HorizontalTextAlignment horizontalAlignment = HorizontalTextAlignment.CENTER;
    private VerticalTextAlignment verticalAlignment = VerticalTextAlignment.CENTER;

    private float rowHeight;
    private float width = Float.MAX_VALUE;
    private float height = Float.MAX_VALUE;

    public Label() {

    }

    public Label(TextRenderer textRenderer) {
        setTextRenderer(textRenderer);
    }

    public Label(String text, TextRenderer textRenderer) {
        setText(text);
        setTextRenderer(textRenderer);
    }

    public Label(TextRenderer textRenderer, Color color) {
        setTextRenderer(textRenderer);
        setColor(color);
    }

    public Label(String text, TextRenderer textRenderer, Color color) {
        setText(text);
        setTextRenderer(textRenderer);
        setColor(color);
    }

    private static boolean isNewLine(String text, int index) {
        return getNewLineSize(text, index) > 0;
    }

    private static int getNewLineSize(String text, int index) {
        if (text.charAt(index) == '\n') {
            return 1;
        } else if (text.charAt(index) == '\r') {
            int next = index + 1;
            return next < text.length() && text.charAt(next) == '\n' ? 2 : 1;
        } else {
            return 0;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addTextChangedListener(TextChangedListener listener) {
        textChangedListeners.add(listener);
    }

    public void removeTextChangedListener(TextChangedListener listener) {
        textChangedListeners.remove(listener);
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
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

    public VerticalTextAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalTextAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(float rowHeight) {
        this.rowHeight = rowHeight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        for (TextChangedListener listener : textChangedListeners) {
            listener.onTextChanged(oldText, text);
        }
    }

    public void render(float alpha) {
        render(alpha, 0, text.length());
    }

    /**
     * Renders the label with the specified text offset and length.
     *
     * @param alpha  The alpha.
     * @param offset The text offset.
     * @param len    The text length.
     * @return Text offset where rendering stopped, i.e. the first index that wasn't rendered.
     */
    public int render(float alpha, int offset, int len) {
        if (textRenderer == null) {
            return offset;
        } else if (color == null) {
            return onRender(alpha, offset, len);
        }

        ShaderParameters params = textRenderer.getParameters();
        float r = params.get(ShaderParameters.COLOR_R);
        float g = params.get(ShaderParameters.COLOR_G);
        float b = params.get(ShaderParameters.COLOR_B);
        float a = params.get(ShaderParameters.ALPHA);

        params.setColor(color);

        int index = onRender(alpha, offset, len);

        params.set(ShaderParameters.COLOR_R, r);
        params.set(ShaderParameters.COLOR_G, g);
        params.set(ShaderParameters.COLOR_B, b);
        params.set(ShaderParameters.ALPHA, a);

        return index;
    }

    public Rectangle calculateBounds() {
        TextBoundsOutput output = new TextBoundsOutput();
        iterateRows(0, text.length(), false, 1f, output);
        return output.bounds;
    }

    private int onRender(float alpha, int offset, int len) {
        ModelMatrix.instance().pushMatrix();
        int index = iterateRows(offset, len, true, alpha, null);
        ModelMatrix.instance().popMatrix();
        return index;
    }

    private Rectangle getRowBounds(Font font, int offset, int len) {
        float textWidth = font.getTextWidth(text, offset, len);
        float offsetX = offsetFromHorizontalAlignment(textWidth);
        float offsetY = offsetFromVerticalAlignment(font);
        return new Rectangle(
                offsetX,
                offsetY - font.getDescent(),
                textWidth + offsetX,
                offsetY + font.getAscent());
    }

    private int iterateRows(int offset, int len, boolean render, float alpha, TextBoundsOutput output) {
        final float rowHeight = this.rowHeight > 0 ? this.rowHeight : textRenderer.getFont().getHeight();
        final TextRenderer textRenderer = this.getTextRenderer();
        final Font font = textRenderer.getFont();
        final int rowCount = (int) (height / font.getHeight());

        int currentRow = 0;
        int rowStart = offset;
        int rowEnd = offset;
        int end = len + offset;
        while (rowStart < end && currentRow < rowCount) {
            rowEnd = Math.min(getRowEnd(text, rowStart, width), end);

            boolean endOfText = rowEnd == end;
            int newLineSize = endOfText ? 0 : getNewLineSize(text, rowEnd);
            boolean newLineSeparatorFound = newLineSize > 0;
            if (!endOfText && !newLineSeparatorFound) {
                // Back up to avoid cutting words
                while (text.charAt(rowEnd) != ' ') {
                    --rowEnd;
                }
            }

            int rowLength = rowEnd - rowStart;

            if (output != null) {
                output.expandBounds(getRowBounds(font, rowStart, rowLength).move(0, -currentRow * rowHeight));
            }

            if (render) {
                renderRow(textRenderer, alpha, font.getTextWidth(text, rowStart, rowLength), rowHeight, rowStart, rowLength);
            }

            rowStart = newLineSeparatorFound ? rowEnd + newLineSize : consumeSeparators(text, rowEnd, end);

            rowEnd = rowStart;

            ++currentRow;
        }

        return rowEnd;
    }

    private int consumeSeparators(String text, int index, int len) {
        while (index < len && text.charAt(index) == ' ')
            ++index;
        return index;
    }

    private void renderRow(TextRenderer textRenderer, float alpha,
                           float rowWidth, float rowHeight, int offset, int len) {
        final float xOffset = offsetFromHorizontalAlignment(rowWidth);
        final float yOffset = offsetFromVerticalAlignment(textRenderer.getFont());

        if (xOffset != 0 || yOffset != 0) {
            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().translatef(xOffset, yOffset, 0);
        }

        textRenderer.setText(text, offset, len);
        textRenderer.render(alpha);

        if (xOffset != 0 || yOffset != 0) {
            ModelMatrix.instance().popMatrix();
        }

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
                return (font.getAscent() + font.getDescent()) / 2f - font.getAscent();
            case BOTTOM:
                return font.getDescent();
            case BASE_LINE:
            default:
                return 0;
        }
    }

    private int getRowEnd(String text, int startIndex, float pixelWidth) {
        final int length = text.length();
        while (startIndex < length && pixelWidth > 0) {
            if (isNewLine(text, startIndex)) {
                break;
            }

            Font font = getTextRenderer().getFont();
            pixelWidth -= font.getTextWidth(text, startIndex, 1);
            if (pixelWidth > 0) {
                ++startIndex;
            }
        }

        return startIndex;
    }

    private static class TextBoundsOutput {
        private Rectangle bounds;

        void expandBounds(Rectangle bounds) {
            this.bounds = this.bounds == null
                    ? bounds
                    : this.bounds.add(bounds);
        }
    }
}