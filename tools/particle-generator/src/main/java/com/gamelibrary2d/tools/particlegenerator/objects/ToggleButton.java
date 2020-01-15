package com.gamelibrary2d.tools.particlegenerator.objects;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.TextRenderer;

public class ToggleButton extends Button {

    private boolean toggled;
    private Renderer renderer;
    private Renderer toggledRenderer;
    private TextRenderer textRenderer;
    private TextRenderer toggledTextRenderer;

    private Color fontColor;
    private Color toggledFontColor;

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Color getFontColor() {
        return fontColor;
    }

    @Override
    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getToggledFontColor() {
        return toggledFontColor;
    }

    public void setToggledFontColor(Color toggledFontColor) {
        this.toggledFontColor = toggledFontColor;
    }

    public Renderer getToggledRenderer() {
        return toggledRenderer;
    }

    public void setToggledRenderer(final Renderer toggledRenderer) {
        this.toggledRenderer = toggledRenderer;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void setTextRenderer(final TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public TextRenderer getToggledTextRenderer() {
        return toggledTextRenderer;
    }

    public void setToggledTextRenderer(TextRenderer toggledTextRenderer) {
        this.toggledTextRenderer = toggledTextRenderer;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Override
    protected void onRender(float alpha) {

        if (toggled) {
            super.setRenderer(toggledRenderer);
            super.setTextRenderer(toggledTextRenderer);
            super.setFontColor(toggledFontColor);
        } else {
            super.setRenderer(renderer);
            super.setTextRenderer(textRenderer);
            super.setFontColor(fontColor);
        }

        super.onRender(alpha);

        super.setRenderer(renderer);
        super.setTextRenderer(textRenderer);
        super.setFontColor(fontColor);
    }
}