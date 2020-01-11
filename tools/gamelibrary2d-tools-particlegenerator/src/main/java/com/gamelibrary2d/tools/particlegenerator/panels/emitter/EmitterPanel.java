package com.gamelibrary2d.tools.particlegenerator.panels.emitter;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.eventlisteners.MouseReleaseListener;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.objects.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.rendering.HorizontalAlignment;
import com.gamelibrary2d.rendering.VerticalAlignment;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.objects.ToggleButton;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

public class EmitterPanel extends AbstractPanel<GameObject> {

    private final ParticleSystemModel particleSystem;
    private final ToggleButton launchSequentialButton;

    public EmitterPanel(ParticleSystemModel particleSystem) {

        this.particleSystem = particleSystem;

        Font font = Fonts.getMenuFont();

        Button launchButton = new Button();
        launchButton.setVerticalAlignment(VerticalAlignment.TOP);
        launchButton.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        launchButton.setTextRenderer(new TextRenderer(font));
        launchButton.setFontColor(new Color(1, 1, 1, 1));
        launchButton.setText("Launch");
        launchButton.setBounds(font.textSize(launchButton.getText(),
                launchButton.getHorizontalAlignment(), launchButton.getVerticalAlignment()));
        launchButton.getPosition().set(0, 0);
        launchButton.addMouseReleaseListener(new LaunchEventHandler());

        launchSequentialButton = new ToggleButton();
        launchSequentialButton.setText("Launch Sequential");
        launchSequentialButton.setTextRenderer(new TextRenderer(font));
        launchSequentialButton.setFontColor(new Color(1, 1, 1, 1));
        launchSequentialButton.setToggledTextRenderer(new TextRenderer(font));
        launchSequentialButton.setToggledFontColor(new Color(0, 1, 0, 1));
        launchSequentialButton.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        launchSequentialButton.setVerticalAlignment(VerticalAlignment.TOP);
        launchSequentialButton.setBounds(font.textSize(launchSequentialButton.getText(),
                launchSequentialButton.getHorizontalAlignment(), launchSequentialButton.getVerticalAlignment()));
        launchSequentialButton.getPosition().set(0, -50);
        launchSequentialButton.addMouseReleaseListener(new LaunchSequentialEventHandler());

        Button launchAllButton = new Button();
        launchAllButton.setText("Launch All");
        launchAllButton.setTextRenderer(new TextRenderer(font));
        launchAllButton.setFontColor(new Color(1, 1, 1, 1));
        launchAllButton.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        launchAllButton.setVerticalAlignment(VerticalAlignment.TOP);
        launchAllButton.setBounds(font.textSize(launchAllButton.getText(),
                launchAllButton.getHorizontalAlignment(), launchAllButton.getVerticalAlignment()));
        launchAllButton.getPosition().set(0, -100);
        launchAllButton.addMouseReleaseListener(new LaunchAllEventHandler());

        add(launchButton);
        add(launchSequentialButton);
        add(launchAllButton);
    }

    public boolean isLaunchingSequential() {
        return launchSequentialButton.isToggled();
    }

    private class LaunchEventHandler implements MouseReleaseListener {

        @Override
        public void onMouseRelease(GameObject obj, int button, int mods,
                                   float projectedX, float projectedY) {

            if (obj.isPixelVisible(projectedX, projectedY) && button == Mouse.instance().mouseButton1()) {
                particleSystem.emit(ParticleFrame.PosX, ParticleFrame.PosY, 0);
            }
        }
    }

    private class LaunchSequentialEventHandler implements MouseReleaseListener {

        @Override
        public void onMouseRelease(GameObject obj, int button, int mods,
                                   float projectedX, float projectedY) {

            if (obj.isPixelVisible(projectedX, projectedY) && button == Mouse.instance().mouseButton1()) {
                launchSequentialButton.setToggled(!launchSequentialButton.isToggled());
            }
        }
    }

    private class LaunchAllEventHandler implements MouseReleaseListener {

        @Override
        public void onMouseRelease(GameObject obj, int button, int mods,
                                   float projectedX, float projectedY) {

            if (obj.isPixelVisible(projectedX, projectedY) && button == Mouse.instance().mouseButton1()) {
                particleSystem.emitAll(ParticleFrame.PosX, ParticleFrame.PosY, 0);
            }
        }
    }
}