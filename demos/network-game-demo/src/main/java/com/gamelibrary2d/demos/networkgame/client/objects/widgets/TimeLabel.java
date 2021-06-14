package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.components.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.Label;

public class TimeLabel extends AbstractGameObject {

    private final Label label;

    public TimeLabel(TextRenderer textRenderer) {
        label = new Label(textRenderer);
        label.setColor(Color.LAVENDER);
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        setTime(0, 0);
    }

    public void setTimeFromSeconds(int seconds) {
        int min = seconds / 60;
        int sec = seconds - min * 60;
        setTime(min, sec);
    }

    public void setTime(int min, int sec) {
        label.setText(String.format("%02d:%02d", min, sec));
    }

    @Override
    protected void onRender(float alpha) {
        label.render(alpha);
    }

    @Override
    public Rectangle getBounds() {
        return Rectangle.EMPTY;
    }
}
