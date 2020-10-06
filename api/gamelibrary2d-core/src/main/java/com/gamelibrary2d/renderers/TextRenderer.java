package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.resources.Font;

public class TextRenderer extends AbstractRenderer {
    private Font font;
    private String text = "";
    private int start;
    private int end;

    public TextRenderer(Font font) {
        setFont(font);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        start = 0;
        end = text.length();
    }

    public void setText(String text, int offset, int len) {
        this.text = text;
        start = offset;
        end = Math.min(text.length(), offset + len);
    }

    @Override
    public Rectangle getBounds() {
        return Rectangle.EMPTY;
    }

    @Override
    protected void applyParameters(float alpha) {
        getParameters().set(ShaderParameters.IS_TEXTURED, 1);
        super.applyParameters(alpha);
    }

    @Override
    public void onRender(ShaderProgram shaderProgram) {
        font.render(shaderProgram, text, start, end);
    }
}