package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.renderers.ParticleShape;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.GenericProperty;
import com.gamelibrary2d.tools.particlegenerator.resources.Fonts;
import com.gamelibrary2d.tools.particlegenerator.widgets.Button;
import com.gamelibrary2d.util.BlendMode;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.PointSmoothing;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.util.io.FileChooser;
import com.gamelibrary2d.widgets.Label;

import java.io.File;
import java.io.IOException;

public class RenderingPanel extends AbstractPanel<GameObject> {

    private final static String TEMP_PATH = System.getenv("TEMP") + "/ParticleGenerator/texture_path.txt";

    private final FileChooser fileChooser;
    private final ParticleSystemModel particleSystem;
    private final Panel<GameObject> iterativePanel;
    private final Panel<GameObject> efficientPanel;
    private final Panel<GameObject> efficientGpuPanel;
    private final ShapePanel shapePanel;
    private final Label textureLabel;

    private Panel<GameObject> activePanel;

    public RenderingPanel(ParticleSystemModel particleSystem) {
        fileChooser = new FileChooser(TEMP_PATH);

        textureLabel = new Label();
        textureLabel.setAlignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
        textureLabel.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        textureLabel.setFontColor(Color.SOFT_BLUE);
        textureLabel.setText("None");

        this.particleSystem = particleSystem;
        this.shapePanel = new ShapePanel(particleSystem);
        this.iterativePanel = createIterativePanel();
        this.efficientPanel = createEfficientPanel();
        this.efficientGpuPanel = createEfficientGpuPanel();

        PanelUtil.stack(this, particleSystemType(), 0f);
        PanelUtil.stack(this, blendMode());
        setActivePanel(efficientPanel);
    }

    private void updateActivePanel() {
        switch (particleSystem.getParticleSystemType()) {
            case ITERATIVE:
                setActivePanel(iterativePanel);
                break;
            case EFFICIENT:
                setActivePanel(efficientPanel);
                break;
            case EFFICIENT_GPU:
                setActivePanel(efficientGpuPanel);
                break;
        }
    }

    private String loadTexture() {
        try {
            File file = fileChooser.browse();
            if (file != null) {
                var url = file.toURI().toURL();
                particleSystem.loadTexture(url);
                return file.getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        particleSystem.removeTexture();
        return null;
    }

    @Override
    public void onRender(float alpha) {
        updateActivePanel();
        shapePanel.updateActivePanel();
        super.onRender(alpha);
    }

    private void setActivePanel(Panel<GameObject> activePanel) {
        if (this.activePanel != activePanel) {
            remove(this.activePanel);
            recalculateBounds();
            this.activePanel = activePanel;
            PanelUtil.stack(this, activePanel);
        }
    }

    private GameObject particleSystemType() {
        return new PanelElement.Enum<>(
                "Particle System",
                ParticleSystemModel.ParticleSystemType.class,
                new GenericProperty<>(particleSystem::getParticleSystemType, particleSystem::setParticleSystemType)
        );
    }

    private GameObject blendMode() {
        return new PanelElement.Enum<>(
                "Blend Mode",
                BlendMode.class,
                new GenericProperty<>(particleSystem::getBlendMode, particleSystem::setBlendMode)
        );
    }

    private Panel<GameObject> createIterativePanel() {
        var panel = new DefaultPanel<>();
        PanelUtil.stack(panel, createQuadPanel(), 0f);
        return panel;
    }

    private Panel<GameObject> createEfficientPanel() {
        var panel = new DefaultPanel<>();
        PanelUtil.stack(panel, shapePanel, 0f);
        return panel;
    }

    private Panel<GameObject> createEfficientGpuPanel() {
        var panel = new DefaultPanel<>();
        PanelUtil.stack(panel, shapePanel, 0f);
        return panel;
    }

    private Panel<GameObject> createQuadPanel() {
        var panel = new DefaultPanel<>();

        var widthElement = new PanelElement.Float(
                "Width",
                new FloatProperty(
                        particleSystem::getWidth,
                        particleSystem::setWidth)
        );

        var heightElement = new PanelElement.Float(
                "Height",
                new FloatProperty(
                        particleSystem::getHeight,
                        particleSystem::setHeight)
        );

        var textureButton = new Button<>(textureLabel, () -> {
            var fileName = loadTexture();
            textureLabel.setText(fileName == null ? "None" : fileName);
        });

        textureButton.setBounds(Fonts.getDefaultFont().textSize(textureLabel.getText(),
                textureLabel.getHorizontalAlignment(), textureLabel.getVerticalAlignment()));

        textureButton.setPosition(0, -2);

        var textureElement = new PanelElement.CustomElement("Texture", textureButton);

        PanelUtil.stack(panel, widthElement, 0f);
        PanelUtil.stack(panel, heightElement);
        PanelUtil.stack(panel, textureElement);
        return panel;
    }

    private class ShapePanel extends AbstractPanel<GameObject> {
        private final ParticleSystemModel particleSystem;
        private final Panel<GameObject> pointPanel;
        private final Panel<GameObject> quadPanel;

        private Panel<GameObject> activePanel;

        ShapePanel(ParticleSystemModel particleSystem) {
            this.particleSystem = particleSystem;
            this.quadPanel = createQuadPanel();
            this.pointPanel = createPointShapePanel();
            PanelUtil.stack(this, particleShape(), 0f);
            updateActivePanel();
        }

        void updateActivePanel() {
            switch (particleSystem.getEfficientRenderer().getParticleShape()) {
                case POINT:
                    setActivePanel(pointPanel);
                    break;
                case QUAD:
                    setActivePanel(quadPanel);
                    break;
            }
        }

        private void setActivePanel(Panel<GameObject> activePanel) {
            if (this.activePanel != activePanel) {
                remove(this.activePanel);
                recalculateBounds();
                this.activePanel = activePanel;
                PanelUtil.stack(this, activePanel);
            }
        }

        private GameObject particleShape() {
            return new PanelElement.Enum<>(
                    "Particle Shape",
                    ParticleShape.class,
                    new GenericProperty<>(
                            () -> particleSystem.getEfficientRenderer().getParticleShape(),
                            v -> particleSystem.getEfficientRenderer().setParticleShape(v))
            );
        }

        private GameObject pointSize() {
            return new PanelElement.Float(
                    "Point Size",
                    new FloatProperty(
                            () -> particleSystem.getEfficientRenderer().getPointSize(),
                            v -> particleSystem.getEfficientRenderer().setPointSize(v))
            );
        }

        private GameObject pointSmoothing() {
            return new PanelElement.Enum<>(
                    "Point Smoothing",
                    PointSmoothing.class,
                    new GenericProperty<>(
                            () -> particleSystem.getEfficientRenderer().getPointSmoothing(),
                            v -> particleSystem.getEfficientRenderer().setPointSmoothing(v))
            );
        }

        private Panel<GameObject> createPointShapePanel() {
            var panel = new DefaultPanel<>();
            PanelUtil.stack(panel, pointSize(), 0f);
            PanelUtil.stack(panel, pointSmoothing());
            return panel;
        }
    }
}