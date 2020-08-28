package com.gamelibrary2d.tools.particlegenerator.panels.emitter;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.objects.ToggleButton;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.widgets.events.MouseButtonReleased;

public class EmitterPanel extends AbstractPanel<GameObject> {

    private final ParticleSystemModel particleSystem;
    private final ToggleButton launchSequentialButton;

    public EmitterPanel(ParticleSystemModel particleSystem) {

        this.particleSystem = particleSystem;

        Font font = Fonts.getMenuFont();

        Button launchButton = new Button();

        var launchButtonContext = launchButton.getContent();
        launchButtonContext.setVerticalAlignment(VerticalAlignment.TOP);
        launchButtonContext.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        launchButtonContext.setTextRenderer(new TextRenderer(font));
        launchButtonContext.setFontColor(new Color(1, 1, 1, 1));
        launchButtonContext.setText("Launch");
        launchButton.setBounds(font.textSize(launchButtonContext.getText(),
                launchButtonContext.getHorizontalAlignment(), launchButtonContext.getVerticalAlignment()));
        launchButton.setPosition(0, 0);
        launchButton.addMouseButtonReleasedListener(new LaunchEventHandler());

        launchSequentialButton = new ToggleButton();

        var seqContext = launchSequentialButton.getContent();
        seqContext.setText("Launch Sequential");
        seqContext.setTextRenderer(new TextRenderer(font));
        seqContext.setFontColor(new Color(1, 1, 1, 1));
        seqContext.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        seqContext.setVerticalAlignment(VerticalAlignment.TOP);

        launchSequentialButton.setBounds(font.textSize(seqContext.getText(),
                seqContext.getHorizontalAlignment(), seqContext.getVerticalAlignment()));
        launchSequentialButton.setPosition(0, -50);
        launchSequentialButton.addMouseButtonReleasedListener(new LaunchSequentialEventHandler());

        Button launchAllButton = new Button();

        var launchContext = launchAllButton.getContent();
        launchContext.setText("Launch All");
        launchContext.setTextRenderer(new TextRenderer(font));
        launchContext.setFontColor(new Color(1, 1, 1, 1));
        launchContext.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        launchContext.setVerticalAlignment(VerticalAlignment.TOP);

        launchAllButton.setBounds(font.textSize(launchContext.getText(),
                launchContext.getHorizontalAlignment(), launchContext.getVerticalAlignment()));
        launchAllButton.setPosition(0, -100);
        launchAllButton.addMouseButtonReleasedListener(new LaunchAllEventHandler());

        add(launchButton);
        add(launchSequentialButton);
        add(launchAllButton);
    }

    public boolean isLaunchingSequential() {
        return launchSequentialButton.isToggled();
    }

    private class LaunchEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                particleSystem.emit(ParticleFrame.PosX, ParticleFrame.PosY);
            }
        }
    }

    private class LaunchSequentialEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                launchSequentialButton.setToggled(!launchSequentialButton.isToggled());
            }
        }
    }

    private class LaunchAllEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                particleSystem.emitAll(ParticleFrame.PosX, ParticleFrame.PosY);
            }
        }
    }
}