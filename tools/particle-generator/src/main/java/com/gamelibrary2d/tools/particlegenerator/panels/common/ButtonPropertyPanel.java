package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.eventlisteners.MouseReleaseListener;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.TextChangedListener;
import com.gamelibrary2d.objects.TextObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.rendering.HorizontalAlignment;
import com.gamelibrary2d.rendering.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

import java.util.ArrayList;
import java.util.List;

public abstract class ButtonPropertyPanel<T> extends StackPanel implements MouseReleaseListener, TextChangedListener {

    private final static float MARGIN = 75;

    private final PropertyParameters<T> params;
    private final List<Button> buttons = new ArrayList<Button>();

    public ButtonPropertyPanel(String propertyName, PropertyParameters<T> params) {

        super(Orientation.HORIZONTAL, MARGIN);

        this.params = params;

        TextObject label = new TextObject();
        label.setVerticalAlignment(VerticalAlignment.TOP);
        label.setHorizontalAlignment(HorizontalAlignment.LEFT);
        label.setVerticalAlignment(VerticalAlignment.TOP);
        label.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        label.setFontColor(Color.WHITE);
        label.setText(propertyName + ":");
        label.setBounds(label.getTextRenderer().getFont().textSize(
                label.getText(),
                label.getHorizontalAlignment(),
                label.getVerticalAlignment()));
        add(label);

        Font font = Fonts.getDefaultFont();
        for (int i = 0; i < params.getCount(); ++i) {

            params.setParameter(i, params.getParameter(i));

            Button button = new Button();
            button.setVerticalAlignment(VerticalAlignment.TOP);
            button.setHorizontalAlignment(HorizontalAlignment.LEFT);
            button.setTextRenderer(new TextRenderer(font));
            button.setFontColor(Color.WHITE);
            button.setText(toString(params.getParameter(i)));
            button.setBounds(font.textSize(button.getText(),
                    button.getHorizontalAlignment(), button.getVerticalAlignment()));
            button.addTextChangedListener(this);
            button.addMouseReleaseListener(this);
            button.addMouseReleaseListener(new MouseReleaseListener() {
                public void onMouseRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
                    update((Button) obj);
                }
            });

            buttons.add(button);
            add(button, i == 0 ? 175 : MARGIN);

            // Trigger initial text-changed
            onTextChanged(button, "", button.getText());
        }
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);

        if (params.updateIfChanged()) {
            for (int i = 0; i < buttons.size(); ++i) {
                buttons.get(i).setText(toString(params.getParameter(i)));
            }
        }
    }

    private void update(Button button) {
        params.setParameter(buttons.indexOf(button), fromString(button.getText()));
        params.updateSetting();
    }

    protected abstract String toString(T value);

    protected abstract T fromString(String string);
}