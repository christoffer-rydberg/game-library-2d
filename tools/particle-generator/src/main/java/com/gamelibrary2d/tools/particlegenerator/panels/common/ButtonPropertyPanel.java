package com.gamelibrary2d.tools.particlegenerator.panels.common;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.eventlisteners.MouseButtonReleaseListener;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderable.Label;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

import java.util.ArrayList;
import java.util.List;

public abstract class ButtonPropertyPanel<T> extends StackPanel implements MouseButtonReleaseListener {

    private final static float MARGIN = 75;

    private final PropertyParameters<T> params;
    private final List<Button> buttons = new ArrayList<Button>();

    public ButtonPropertyPanel(String propertyName, PropertyParameters<T> params) {

        super(Orientation.HORIZONTAL, MARGIN);

        this.params = params;

        var labelContext = new Label();
        labelContext.setVerticalAlignment(VerticalAlignment.TOP);
        labelContext.setHorizontalAlignment(HorizontalAlignment.LEFT);
        labelContext.setVerticalAlignment(VerticalAlignment.TOP);
        labelContext.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        labelContext.setFontColor(Color.WHITE);
        labelContext.setText(propertyName + ":");

        var label = new BasicObject<>(labelContext);
        label.setBounds(labelContext.getTextRenderer().getFont().textSize(
                labelContext.getText(),
                labelContext.getHorizontalAlignment(),
                labelContext.getVerticalAlignment()));
        add(label);

        Font font = Fonts.getDefaultFont();
        for (int i = 0; i < params.getCount(); ++i) {

            params.setParameter(i, params.getParameter(i));

            Button button = new Button();

            var buttonContext = button.getContent();
            buttonContext.setVerticalAlignment(VerticalAlignment.TOP);
            buttonContext.setHorizontalAlignment(HorizontalAlignment.LEFT);
            buttonContext.setTextRenderer(new TextRenderer(font));
            buttonContext.setFontColor(Color.WHITE);
            buttonContext.setText(toString(params.getParameter(i)));
            button.setBounds(font.textSize(buttonContext.getText(),
                    buttonContext.getHorizontalAlignment(), buttonContext.getVerticalAlignment()));
            buttonContext.addTextChangedListener((a, before, after) -> onTextChanged(button, before, after));
            button.addMouseButtonReleaseListener(this);
            button.addMouseButtonReleaseListener(new MouseButtonReleaseListener() {
                public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
                    update((Button) obj);
                }
            });

            buttons.add(button);
            add(button, i == 0 ? 175 : MARGIN);

            // Trigger initial text-changed
            onTextChanged(button, "", buttonContext.getText());
        }
    }

    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (params.updateIfChanged()) {
            for (int i = 0; i < buttons.size(); ++i) {
                buttons.get(i).getContent().setText(toString(params.getParameter(i)));
            }
        }
    }

    private void update(Button button) {
        params.setParameter(buttons.indexOf(button), fromString(button.getContent().getText()));
        params.updateSetting();
    }

    protected abstract String toString(T value);

    protected abstract T fromString(String string);

    protected abstract void onTextChanged(GameObject obj, String before, String after);
}