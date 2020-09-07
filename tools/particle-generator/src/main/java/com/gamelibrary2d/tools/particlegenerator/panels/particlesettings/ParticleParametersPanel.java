package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.markers.Parent;
import com.gamelibrary2d.objects.AbstractObjectWrapper;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.renderers.LineRenderer;
import com.gamelibrary2d.renderers.RenderingParameters;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.resources.Bounds;
import com.gamelibrary2d.tools.particlegenerator.resources.Fonts;
import com.gamelibrary2d.tools.particlegenerator.resources.Surfaces;
import com.gamelibrary2d.tools.particlegenerator.resources.Textures;
import com.gamelibrary2d.tools.particlegenerator.util.BooleanProperty;
import com.gamelibrary2d.tools.particlegenerator.util.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.widgets.Box;
import com.gamelibrary2d.tools.particlegenerator.widgets.Checkbox;
import com.gamelibrary2d.tools.particlegenerator.widgets.FloatPropertyTextBox;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.StackOrientation;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.widgets.DefaultWidget;
import com.gamelibrary2d.widgets.Label;

import java.util.List;

public class ParticleParametersPanel extends AbstractObjectWrapper<DefaultPanel<GameObject>> implements Parent<GameObject> {

    private final static float STACK_MARGIN = 4f;

    private final ParticleSystemModel particleSystem;

    public ParticleParametersPanel(ParticleSystemModel particleSystem) {
        setWrapped(new DefaultPanel<>());
        this.particleSystem = particleSystem;
        stack(life(), 0f);
        stack(delay(), STACK_MARGIN);
        stack(speed(), STACK_MARGIN);
        stack(endSpeedFactor(), STACK_MARGIN);
        stack(moveFromCenter(), STACK_MARGIN);
        stack(direction(), STACK_MARGIN);
        stack(centripetalAcceleration(), STACK_MARGIN);
        stack(tangentalAcceleration(), STACK_MARGIN);
        stack(horizontalAcceleration(), STACK_MARGIN);
        stack(verticalAcceleration(), STACK_MARGIN);
        stack(scale(), STACK_MARGIN);
        stack(endScale(), STACK_MARGIN);
        stack(rotation(), STACK_MARGIN);
        stack(colors(), STACK_MARGIN);
        stack(endColors(), STACK_MARGIN);
        stack(alpha(), STACK_MARGIN);
        stack(endAlpha(), STACK_MARGIN);

        getWrapped().recalculateBounds();
    }

    private static <T extends GameObject> void stack(Panel<T> panel, T element, float offset) {
        panel.stack(element, StackOrientation.DOWN, offset);
    }

    private static DefaultGameObject<Label> createLabel(
            String text,
            Font font,
            Color color,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment) {
        var textRenderer = new TextRenderer(font);
        var label = new Label(text, textRenderer);
        label.setHorizontalAlignment(horizontalAlignment);
        label.setVerticalAlignment(verticalAlignment);
        label.setFontColor(color);
        return new DefaultGameObject<>(label);
    }

    private void stack(GameObject element, float offset) {
        stack(getWrapped(), element, offset);
    }

    private ParticleParameters params() {
        return particleSystem.getSettings().getParticleParameters();
    }

    private GameObject life() {
        return new FloatElement(
                "Life",
                new FloatProperty(() -> params().getLife(), v -> params().setLife(v)),
                new FloatProperty(() -> params().getLifeVar(), v -> params().setLifeVar(v))
        );
    }

    private GameObject delay() {
        return new FloatElement(
                "Delay",
                new FloatProperty(() -> params().getDelay(), v -> params().setDelay(v)),
                new FloatProperty(() -> params().getDelayVar(), v -> params().setDelayVar(v))
        );
    }

    private GameObject speed() {
        return new FloatElement(
                "Speed",
                new FloatProperty(() -> params().getSpeed(), v -> params().setSpeed(v)),
                new FloatProperty(() -> params().getSpeedVar(), v -> params().setSpeedVar(v))
        );
    }

    private GameObject endSpeedFactor() {
        return new FloatElement(
                "End Speed Factor",
                new FloatProperty(() -> params().getEndSpeedFactor(), v -> params().setEndSpeedFactor(v)),
                new FloatProperty(() -> params().getEndSpeedFactorVar(), v -> params().setEndSpeedFactorVar(v))
        );
    }

    private GameObject moveFromCenter() {
        return new BooleanElement(
                "Move From Center",
                new BooleanProperty(() -> params().isMovingFromCenter(), v -> params().setMoveFromCenter(v))
        );
    }

    private GameObject direction() {
        return new FloatElement(
                "Direction",
                new FloatProperty(() -> params().getDirection(), v -> params().setDirection(v)),
                new FloatProperty(() -> params().getDirectionVar(), v -> params().setDirectionVar(v))
        );
    }

    private GameObject centripetalAcceleration() {
        return new FloatElement(
                "Centripetal Acceleration",
                new FloatProperty(() -> params().getCentripetalAcceleration(), v -> params().setCentripetalAcceleration(v)),
                new FloatProperty(() -> params().getCentripetalAccelerationVar(), v -> params().setCentripetalAccelerationVar(v))
        );
    }

    private GameObject tangentalAcceleration() {
        return new FloatElement(
                "Tangental Acceleration",
                new FloatProperty(() -> params().getTangentalAcceleration(), v -> params().setTangentalAcceleration(v)),
                new FloatProperty(() -> params().getTangentalAccelerationVar(), v -> params().setTangentalAccelerationVar(v))
        );
    }

    private GameObject horizontalAcceleration() {
        return new FloatElement(
                "Horizontal Acceleration",
                new FloatProperty(() -> params().getHorizontalAcceleration(), v -> params().setHorizontalAcceleration(v)),
                new FloatProperty(() -> params().getHorizontalAccelerationVar(), v -> params().setHorizontalAccelerationVar(v))
        );
    }

    private GameObject verticalAcceleration() {
        return new FloatElement(
                "Vertical Acceleration",
                new FloatProperty(() -> params().getVerticalAcceleration(), v -> params().setVerticalAcceleration(v)),
                new FloatProperty(() -> params().getVerticalAccelerationVar(), v -> params().setVerticalAccelerationVar(v))
        );
    }

    private GameObject scale() {
        return new FloatElement(
                "Scale",
                new FloatProperty(() -> params().getScale(), v -> params().setScale(v)),
                new FloatProperty(() -> params().getScaleVar(), v -> params().setScaleVar(v))
        );
    }

    private GameObject endScale() {
        var endScale = new FloatElement(
                "End Scale",
                new FloatProperty(() -> params().getEndScale(), v -> params().setEndScale(v)),
                new FloatProperty(() -> params().getEndScaleVar(), v -> params().setEndScaleVar(v))
        );

        return new OptionalElement(endScale, new BooleanProperty(() -> params().isUpdatingScale(), v -> params().setUpdateScale(v)));
    }

    private GameObject rotation() {
        var panel = new DefaultPanel<>();

        var rotation = new FloatElement(
                "Rotation",
                new FloatProperty(() -> params().getRotation(), v -> params().setRotation(v)),
                new FloatProperty(() -> params().getRotationVar(), v -> params().setRotationVar(v))
        );

        var rotationSpeed = new FloatElement(
                "Rotation Speed",
                new FloatProperty(() -> params().getRotationSpeed(), v -> params().setRotationSpeed(v)),
                new FloatProperty(() -> params().getRotationSpeedVar(), v -> params().setRotationSpeedVar(v))
        );

        var rotationAcceleration = new FloatElement(
                "Rotation Acceleration",
                new FloatProperty(() -> params().getRotationAcceleration(), v -> params().setRotationAcceleration(v)),
                new FloatProperty(() -> params().getRotationAccelerationVar(), v -> params().setRotationAccelerationVar(v))
        );

        stack(panel, rotation, 0f);
        stack(panel, rotationSpeed, STACK_MARGIN);
        stack(panel, rotationAcceleration, STACK_MARGIN);

        return new OptionalElement(panel, new BooleanProperty(() -> !params().isRotatedForward(), v -> params().setRotatedForward(!v)));
    }

    private GameObject colors() {
        var panel = new DefaultPanel<>();

        var r = new FloatElement(
                "Color R",
                new FloatProperty(() -> params().getColorR(), v -> params().setColorR(v)),
                new FloatProperty(() -> params().getColorRVar(), v -> params().setColorRVar(v))
        );

        var g = new FloatElement(
                "Color G",
                new FloatProperty(() -> params().getColorG(), v -> params().setColorG(v)),
                new FloatProperty(() -> params().getColorGVar(), v -> params().setColorGVar(v))
        );

        var b = new FloatElement(
                "Color B",
                new FloatProperty(() -> params().getColorB(), v -> params().setColorB(v)),
                new FloatProperty(() -> params().getColorBVar(), v -> params().setColorBVar(v))
        );

        stack(panel, r, 0f);
        stack(panel, g, STACK_MARGIN);
        stack(panel, b, STACK_MARGIN);

        panel.recalculateBounds();

        return panel;
    }

    private GameObject endColors() {
        var panel = new DefaultPanel<>();

        var r = new FloatElement(
                "End Color R",
                new FloatProperty(() -> params().getEndColorR(), v -> params().setEndColorR(v)),
                new FloatProperty(() -> params().getEndColorRVar(), v -> params().setEndColorRVar(v))
        );

        var g = new FloatElement(
                "End Color G",
                new FloatProperty(() -> params().getEndColorG(), v -> params().setEndColorG(v)),
                new FloatProperty(() -> params().getEndColorGVar(), v -> params().setEndColorGVar(v))
        );

        var b = new FloatElement(
                "End Color B",
                new FloatProperty(() -> params().getEndColorB(), v -> params().setEndColorB(v)),
                new FloatProperty(() -> params().getEndColorBVar(), v -> params().setEndColorBVar(v))
        );

        stack(panel, r, 0f);
        stack(panel, g, STACK_MARGIN);
        stack(panel, b, STACK_MARGIN);

        panel.recalculateBounds();

        return new OptionalElement(panel, new BooleanProperty(() -> params().isUpdatingColor(), v -> params().setUpdateColor(v)));
    }

    private GameObject alpha() {
        return new FloatElement(
                "Alpha",
                new FloatProperty(() -> params().getAlpha(), v -> params().setAlpha(v)),
                new FloatProperty(() -> params().getAlphaVar(), v -> params().setAlphaVar(v))
        );
    }

    private GameObject endAlpha() {
        var endAlpha = new FloatElement(
                "End Alpha",
                new FloatProperty(() -> params().getEndAlpha(), v -> params().setEndAlpha(v)),
                new FloatProperty(() -> params().getEndAlphaVar(), v -> params().setEndAlphaVar(v))
        );

        return new OptionalElement(endAlpha, new BooleanProperty(() -> params().isUpdatingAlpha(), v -> params().setUpdateAlpha(v)));
    }

    @Override
    public List<GameObject> getChildren() {
        return getWrapped().getChildren();
    }

    private static class BooleanElement extends AbstractPanel<GameObject> {
        BooleanElement(String parameterName, BooleanProperty parameter) {
            var label = createLabel(
                    parameterName + ":",
                    Fonts.getDefaultFont(),
                    Color.SAND,
                    HorizontalAlignment.RIGHT,
                    VerticalAlignment.CENTER
            );
            label.setPosition(-10, -2);
            label.setBounds(new Rectangle(0, 1, 0, 12));

            var checkBoxRenderer = new LineRenderer();
            checkBoxRenderer.setColor(Color.SILVER);

            var checkBox = new Checkbox(
                    new Box(Rectangle.centered(14f, 14f), checkBoxRenderer),
                    Fonts.getLargeFont(),
                    parameter);

            checkBox.setPosition(
                    Surfaces.propertyTextBox().getBounds().width() / 2f,
                    0f);

            add(label);
            add(checkBox);

            recalculateBounds();
        }
    }

    private static class FloatElement extends AbstractPanel<GameObject> {
        FloatElement(String parameterName, FloatProperty parameter, FloatProperty variance) {
            var label = createLabel(
                    parameterName + ":",
                    Fonts.getDefaultFont(),
                    Color.SAND,
                    HorizontalAlignment.RIGHT,
                    VerticalAlignment.BOTTOM);

            label.setPosition(-10, -3);

            var parameterWidget = createPropertyWidget(parameter, Color.LIGHT_BLUE);
            parameterWidget.setPosition(-parameterWidget.getBounds().xMin(), 0f);

            var varianceWidget = createPropertyWidget(variance, Color.LIGHT_BLUE);
            varianceWidget.setPosition(120, 0f);

            var plusLabel = createLabel(
                    "+",
                    Fonts.getDefaultFont(),
                    Color.SAND,
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.BOTTOM);

            plusLabel.setPosition(
                    varianceWidget.getPosition().getX() + varianceWidget.getBounds().xMin() - 8,
                    varianceWidget.getPosition().getY() + varianceWidget.getBounds().yMin() - 1);

            var minusLabel = createLabel(
                    "-",
                    Fonts.getDefaultFont(),
                    Color.SAND,
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.BOTTOM
            );
            minusLabel.setPosition(
                    varianceWidget.getPosition().getX() + varianceWidget.getBounds().xMin() - 8,
                    varianceWidget.getPosition().getY() + varianceWidget.getBounds().yMin() - 6);

            add(label);
            add(parameterWidget);
            add(plusLabel);
            add(minusLabel);
            add(varianceWidget);

            recalculateBounds();
        }

        private GameObject createPropertyWidget(FloatProperty property, Color backgroundColor) {
            var textBox = new FloatPropertyTextBox(new TextRenderer(Fonts.getDefaultFont()), property);
            textBox.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textBox.setVerticalAlignment(VerticalAlignment.BOTTOM);

            var background = new SurfaceRenderer(
                    Surfaces.propertyTextBox(),
                    Textures.propertyBaseLine()
            );

            background.getParameters().setRgb(backgroundColor);
            background.getParameters().set(RenderingParameters.ALPHA, 0.5f);

            textBox.setBackground(background);

            var propertyWidget = new DefaultWidget<>(textBox);
            propertyWidget.addFocusChangedListener(focused ->
                    background.getParameters().setRgb(focused ? Color.GOLD : backgroundColor));
            propertyWidget.setBounds(Bounds.PROPERTY_TEXT_BOX.pad(0, 0, 0, 15f));

            return propertyWidget;
        }
    }

    private static class OptionalElement extends AbstractPanel<GameObject> {
        private final GameObject content;

        OptionalElement(GameObject content, BooleanProperty isEnabled) {
            this.content = content;

            var bounds = content.getBounds().pad(2f, 2f, 40f, 2f);

            var boundingBox = new DefaultGameObject<>(new Box(bounds, new LineRenderer()));

            var checkBoxRenderer = new LineRenderer();
            checkBoxRenderer.setColor(Color.SILVER);

            var checkBox = new Checkbox(
                    new Box(Rectangle.centered(12f, 12f), checkBoxRenderer),
                    Fonts.getDefaultFont(),
                    isEnabled,
                    this::onEnabled,
                    this::onDisabled);

            checkBox.setPosition(
                    bounds.xMax() - checkBox.getBounds().xMax() - 6f,
                    bounds.yMax() - checkBox.getBounds().yMax() - 6f);

            add(content);
            add(boundingBox);
            add(checkBox);

            if (isEnabled.get()) {
                onEnabled();
            } else {
                onDisabled();
            }
        }

        private void onEnabled() {
            content.setOpacity(1f);
        }

        private void onDisabled() {
            content.setOpacity(0.5f);
        }
    }
}