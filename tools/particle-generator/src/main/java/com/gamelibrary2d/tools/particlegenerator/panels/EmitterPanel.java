package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.resources.Fonts;
import com.gamelibrary2d.tools.particlegenerator.widgets.Button;
import com.gamelibrary2d.tools.particlegenerator.widgets.ToggleButton;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.Label;

public class EmitterPanel extends AbstractPanel<GameObject> {
    private final ToggleButton emitSequentialButton;

    public EmitterPanel(ParticleSystemModel particleSystem) {
        Font font = Fonts.getMenuFont();

        var emitButtonConent = new Label();
        emitButtonConent.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.TOP);
        emitButtonConent.setTextRenderer(new TextRenderer(font));
        emitButtonConent.setFontColor(Color.SOFT_BLUE);
        emitButtonConent.setText("Emit");

        var emitButton = new Button<>(emitButtonConent, particleSystem::emit);
        emitButton.setBounds(emitButtonConent.getTextBounds());
        emitButton.setPosition(0, 0);

        emitSequentialButton = new ToggleButton();
        var emitSequentialContent = emitSequentialButton.getContent();
        emitSequentialContent.setText("Emit Sequential");
        emitSequentialContent.setTextRenderer(new TextRenderer(font));
        emitSequentialContent.setFontColor(Color.SOFT_BLUE);
        emitSequentialContent.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.TOP);

        emitSequentialButton.setBounds(emitSequentialContent.getTextBounds());
        emitSequentialButton.setPosition(0, -50);

        var emitAllContext = new Label();
        emitAllContext.setText("Emit All");
        emitAllContext.setTextRenderer(new TextRenderer(font));
        emitAllContext.setFontColor(Color.SOFT_BLUE);
        emitAllContext.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.TOP);

        var emitAllButton = new Button<>(emitAllContext, particleSystem::emitAll);
        emitAllButton.setBounds(emitAllContext.getTextBounds());
        emitAllButton.setPosition(0, -100);

        add(emitButton);
        add(emitSequentialButton);
        add(emitAllButton);
    }

    public boolean isLaunchingSequential() {
        return emitSequentialButton.isToggled();
    }
}