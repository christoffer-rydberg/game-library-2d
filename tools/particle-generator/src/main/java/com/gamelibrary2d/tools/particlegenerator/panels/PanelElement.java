package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.ComposableGameObject;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.*;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.ParticleGenerator;
import com.gamelibrary2d.tools.particlegenerator.properties.BooleanProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.GenericProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.IntegerProperty;
import com.gamelibrary2d.tools.particlegenerator.resources.Bounds;
import com.gamelibrary2d.tools.particlegenerator.resources.Fonts;
import com.gamelibrary2d.tools.particlegenerator.resources.Surfaces;
import com.gamelibrary2d.tools.particlegenerator.resources.Textures;
import com.gamelibrary2d.tools.particlegenerator.widgets.*;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.DefaultWidget;
import com.gamelibrary2d.widgets.Label;
import com.gamelibrary2d.widgets.TextField;

public class PanelElement {
    private static final Color BASE_LINE_COLOR = Color.SOFT_BLUE;
    private static final Color ENUM_BASE_LINE_COLOR = Color.LAVENDER;
    private static final Color ENUM_FONT_COLOR = Color.SOFT_BLUE;

    private static Label createLabel(
            String text,
            Font font,
            Color color,
            HorizontalTextAlignment horizontalAlignment,
            VerticalTextAlignment verticalAlignment) {
        TextRenderer textRenderer = new TextRenderer(font);
        Label label = new Label(text, textRenderer, color);
        label.setAlignment(horizontalAlignment, verticalAlignment);
        return label;
    }

    private static DefaultGameObject<Label> createLabelObject(
            String text,
            Font font,
            Color color,
            HorizontalTextAlignment horizontalAlignment,
            VerticalTextAlignment verticalAlignment) {
        return new DefaultGameObject<>(createLabel(
                text, font, color, horizontalAlignment, verticalAlignment
        ));
    }

    private static DefaultGameObject<Label> createParameterLabel(String parameterName) {
        DefaultGameObject<Label> label = createLabelObject(
                parameterName + ":",
                Fonts.getDefaultFont(),
                Color.SAND,
                HorizontalTextAlignment.RIGHT,
                VerticalTextAlignment.BASE_LINE
        );

        label.setPosition(-10, 0);
        label.setBounds(new Rectangle(10, 10, 10, 12));
        return label;
    }

    public static class Boolean extends AbstractPanel<GameObject> {
        public Boolean(String parameterName, BooleanProperty parameter) {
            DefaultGameObject<Label> label = createParameterLabel(parameterName);

            LineRenderer lineRenderer = new LineRenderer();
            lineRenderer.getParameters().setColor(Color.SILVER);

            Checkbox checkBox = new Checkbox(
                    Surfaces.defaultCheckbox(),
                    lineRenderer,
                    Fonts.getLargeFont(),
                    parameter);

            checkBox.setPosition(
                    Surfaces.propertyBaseLine().getBounds().getWidth() / 2f - checkBox.getBounds().getCenterX(),
                    -Surfaces.defaultCheckbox().getBounds().getLowerY() - 2f);

            add(label);
            add(checkBox);
        }
    }

    public static class Float extends AbstractPanel<GameObject> {

        public Float(String parameterName, FloatProperty parameter) {
            this(parameterName, parameter, null);
        }

        public Float(String parameterName, FloatProperty parameter, FloatProperty variance) {
            ComposableGameObject<Label> label = createParameterLabel(parameterName);

            GameObject parameterWidget = createPropertyWidget(parameter, BASE_LINE_COLOR);
            parameterWidget.setPosition(-parameterWidget.getBounds().getLowerX(), 1f);

            add(label);
            add(parameterWidget);

            if (variance != null) {
                GameObject varianceWidget = createPropertyWidget(variance, BASE_LINE_COLOR);
                varianceWidget.setPosition(120, 1f);

                GameObject plusLabel = createLabelObject(
                        "+",
                        Fonts.getDefaultFont(),
                        Color.SAND,
                        HorizontalTextAlignment.CENTER,
                        VerticalTextAlignment.CENTER);

                plusLabel.setPosition(
                        varianceWidget.getPosition().getX() + varianceWidget.getBounds().getLowerX() - 8,
                        varianceWidget.getPosition().getY() + 3);

                GameObject minusLabel = createLabelObject(
                        "-",
                        Fonts.getDefaultFont(),
                        Color.SAND,
                        HorizontalTextAlignment.CENTER,
                        VerticalTextAlignment.CENTER);

                minusLabel.setPosition(
                        varianceWidget.getPosition().getX() + varianceWidget.getBounds().getLowerX() - 8,
                        varianceWidget.getPosition().getY() - 3);

                add(plusLabel);
                add(minusLabel);
                add(varianceWidget);
            }
        }

        private GameObject createPropertyWidget(FloatProperty property, Color backgroundColor) {
            Renderer background = new SurfaceRenderer(
                    Surfaces.propertyBaseLine(),
                    Textures.propertyBaseLine()
            );

            background.getParameters().setColor(backgroundColor);
            background.getParameters().set(ShaderParameters.ALPHA, 0.5f);

            FloatPropertyTextField textBox = new FloatPropertyTextField(new TextRenderer(Fonts.getDefaultFont()), property);
            textBox.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.BASE_LINE);

            DefaultWidget<Renderable> propertyWidget = new DefaultWidget<>(textBox);
            propertyWidget.setBackground(background);
            propertyWidget.addFocusChangedListener(focused ->
                    background.getParameters().setColor(focused ? Color.GOLD : backgroundColor));
            propertyWidget.setBounds(Bounds.PROPERTY_BASE_LINE.pad(0, 0, 0, 15f));

            return propertyWidget;
        }
    }

    public static class Integer extends AbstractPanel<GameObject> {
        public Integer(String parameterName, IntegerProperty parameter) {
            this(parameterName, parameter, null);
        }

        public Integer(String parameterName, IntegerProperty parameter, IntegerProperty variance) {
            GameObject label = createParameterLabel(parameterName);

            GameObject parameterWidget = createPropertyWidget(parameter, BASE_LINE_COLOR);
            parameterWidget.setPosition(-parameterWidget.getBounds().getLowerX(), 1f);

            add(label);
            add(parameterWidget);

            if (variance != null) {
                GameObject varianceWidget = createPropertyWidget(variance, BASE_LINE_COLOR);
                varianceWidget.setPosition(120, 1f);

                GameObject plusLabel = createLabelObject(
                        "+",
                        Fonts.getDefaultFont(),
                        Color.SAND,
                        HorizontalTextAlignment.CENTER,
                        VerticalTextAlignment.BASE_LINE);

                plusLabel.setPosition(
                        varianceWidget.getPosition().getX() + varianceWidget.getBounds().getLowerX() - 8,
                        varianceWidget.getPosition().getY() + 3);

                GameObject minusLabel = createLabelObject(
                        "-",
                        Fonts.getDefaultFont(),
                        Color.SAND,
                        HorizontalTextAlignment.CENTER,
                        VerticalTextAlignment.BASE_LINE);

                minusLabel.setPosition(
                        varianceWidget.getPosition().getX() + varianceWidget.getBounds().getLowerX() - 8,
                        varianceWidget.getPosition().getY() - 3);

                add(plusLabel);
                add(minusLabel);
                add(varianceWidget);
            }
        }

        private GameObject createPropertyWidget(IntegerProperty property, Color backgroundColor) {
            Renderer background = new SurfaceRenderer(
                    Surfaces.propertyBaseLine(),
                    Textures.propertyBaseLine()
            );

            background.getParameters().setColor(backgroundColor);
            background.getParameters().set(ShaderParameters.ALPHA, 0.5f);

            TextField textBox = new IntegerPropertyTextField(new TextRenderer(Fonts.getDefaultFont()), property);
            textBox.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.BASE_LINE);

            DefaultWidget<Renderable> propertyWidget = new DefaultWidget<>(textBox);
            propertyWidget.setBackground(background);
            propertyWidget.addFocusChangedListener(focused ->
                    background.getParameters().setColor(focused ? Color.GOLD : backgroundColor));
            propertyWidget.setBounds(Bounds.PROPERTY_BASE_LINE.pad(0, 0, 0, 15f));

            return propertyWidget;
        }
    }

    public static class Optional extends AbstractPanel<GameObject> {
        private final GameObject content;

        public Optional(GameObject content, BooleanProperty isEnabled) {
            this.content = content;

            Rectangle bounds = content.getBounds().pad(2f, 2f, 40f, 2f);
            Box box = Box.create(bounds, ParticleGenerator.GLOBAL_DISPOSER);
            LineRenderer lineRenderer = new LineRenderer();

            DefaultGameObject<Renderable> boundingBox = new DefaultGameObject<>(alpha -> box.render(lineRenderer, alpha));
            boundingBox.setBounds(bounds);

            LineRenderer checkBoxRenderer = new LineRenderer();
            checkBoxRenderer.getParameters().setColor(Color.SILVER);

            Checkbox checkBox = new Checkbox(
                    Surfaces.cornerCheckbox(),
                    checkBoxRenderer,
                    Fonts.getDefaultFont(),
                    isEnabled,
                    this::onEnabled,
                    this::onDisabled);

            checkBox.setPosition(
                    bounds.getUpperX() - checkBox.getBounds().getUpperX() - 6f,
                    bounds.getUpperY() - checkBox.getBounds().getUpperY() - 6f);

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

    public static class Enum<T extends java.lang.Enum<T>> extends AbstractPanel<GameObject> {

        public Enum(String parameterName, Class<T> enumType, GenericProperty<T> property) {
            GameObject label = createParameterLabel(parameterName);

            Renderer background = new SurfaceRenderer(
                    Surfaces.propertyBaseLine(),
                    Textures.propertyBaseLine()
            );

            background.getParameters().setColor(ENUM_BASE_LINE_COLOR);
            background.getParameters().set(ShaderParameters.ALPHA, 0.5f);

            DefaultGameObject<Renderer> backgroundObj = new DefaultGameObject<>(background);
            backgroundObj.setPosition(-backgroundObj.getBounds().getLowerX(), 0f);
            backgroundObj.setBounds(Bounds.PROPERTY_BASE_LINE.pad(0, 0, 0, 15f));

            Label enumLabel = createLabel(
                    property.get().toString(),
                    Fonts.getSmallFont(),
                    ENUM_FONT_COLOR,
                    HorizontalTextAlignment.CENTER,
                    VerticalTextAlignment.BASE_LINE
            );

            float backgroundWidth = backgroundObj.getBounds().getWidth();

            EnumWidget<T> widget = new EnumWidget<>(enumType, enumLabel, property);
            widget.setPosition(backgroundWidth / 2, 0);
            widget.setBounds(new Rectangle(
                    -backgroundWidth / 2,
                    backgroundObj.getBounds().getLowerY(),
                    backgroundWidth / 2,
                    backgroundObj.getBounds().getUpperY()));

            add(label);
            add(backgroundObj);
            add(widget);
        }
    }

    public static class CustomElement extends AbstractPanel<GameObject> {

        public CustomElement(String parameterName, GameObject element) {
            GameObject label = createParameterLabel(parameterName);
            add(label);
            add(element);
        }
    }
}
