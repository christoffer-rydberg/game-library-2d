package com.gamelibrary2d.tools.particlegenerator.panels.renderSettings;

import com.gamelibrary2d.animation.AnimationFactory;
import com.gamelibrary2d.animation.AnimationFormats;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.ResourceDisposer;
import com.gamelibrary2d.eventlisteners.MouseReleaseListener;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.IterativeParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleShape;
import com.gamelibrary2d.renderers.AbstractShaderRenderer;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.rendering.BlendMode;
import com.gamelibrary2d.rendering.PointSmoothing;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Shader;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.BooleanPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.EnumPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.FloatPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.PropertyParameters;
import com.gamelibrary2d.util.io.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RenderSettingsPanel extends StackPanel {

    private final static float ROW_HEIGHT = 15;

    private final FileChooser fileChooser;

    private final ParticleSystemModel particleSystem;

    private final SurfaceRenderer defaultRenderer;

    // Root panels
    private final Panel<GameObject> quadPanel;
    private final Panel<GameObject> pointPanel;

    // Sub panels
    private final Panel<GameObject> useTexturePanel;
    private final Panel<GameObject> useOriginalImageSize;
    private final Panel<GameObject> loopAnimationPanel;
    private final ResourceDisposer quadDisposer;
    private final ResourceDisposer rendererDisposer;
    private URL textureURL;
    private Renderer texturedRenderer;
    private Surface quad;
    private float width;
    private float height;
    private float pointSize;
    private boolean useOriginalSize;
    private float originalWidth;
    private float originalHeight;
    private BlendMode blendMode;
    private EfficientParticleRenderer defaultParticleRenderer;
    private IterativeParticleRenderer iterativeParticleRenderer;

    public RenderSettingsPanel(ParticleSystemModel particleSystem, ParticleFrame frame) {

        super(Orientation.VERTICAL, -ROW_HEIGHT);

        this.particleSystem = particleSystem;

        rendererDisposer = new ResourceDisposer(frame);

        quadDisposer = new ResourceDisposer(frame);

        fileChooser = new FileChooser(System.getenv("TEMP") + "/ParticleGenerator/texture_path.txt");

        defaultParticleRenderer = (EfficientParticleRenderer) particleSystem.getRenderer();

        iterativeParticleRenderer = new IterativeParticleRenderer();

        quad = Quad.create(defaultParticleRenderer.getBounds(), frame);
        width = quad.getBounds().getWidth();
        height = quad.getBounds().getHeight();
        defaultParticleRenderer.setPointSize(width);
        pointSize = defaultParticleRenderer.getPointSize();

        blendMode = defaultParticleRenderer.getBlendMode();

        ShaderProgram defaultParticleShaderProgram = ShaderProgram.create(frame);
        defaultParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/Particle.vertex", ShaderType.VERTEX, frame));
        defaultParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/Particle.fragment", ShaderType.FRAGMENT, frame));
        defaultParticleShaderProgram.bindFragDataLocation(0, "fragColor"); // Optional since the fragment shader only
        // have one "out" variable
        defaultParticleShaderProgram.initialize();
        defaultParticleShaderProgram.initializeMvp(frame.getGame().getWindow().getWidth(),
                frame.getGame().getWindow().getHeight());

        defaultRenderer = new SurfaceRenderer(quad);
        defaultRenderer.setShaderProgram(defaultParticleShaderProgram);
        defaultRenderer.setBlendMode(BlendMode.ADDITIVE);
        particleSystem.getUpdateSettings().setRenderer(defaultRenderer);

        // Create property panels
        Panel<GameObject> updateOnGpuPanel = new BooleanPropertyPanel("Update on GPU", getUpdateOnGpuParameters());
        Panel<GameObject> rendererPanel = new EnumPropertyPanel<>("Renderer", ParticleRendering.class,
                getRendererParameters());
        Panel<GameObject> texturePanel = new ButtonPanel("Texture", "Not loaded", getTextureMouseListener(), null);
        useTexturePanel = new BooleanPropertyPanel("Use Texture", getUseTextureParameters());
        Panel<GameObject> blendModePanel = new EnumPropertyPanel<>("Blend Mode", BlendMode.class, getBlendModeParameters());
        Panel<GameObject> pointSizePanel = new FloatPropertyPanel("Size", getPointSizeParameters());
        Panel<GameObject> widthPanel = new FloatPropertyPanel("Width", getWidthParameters());
        Panel<GameObject> heightPanel = new FloatPropertyPanel("Height", getHeightParameters());
        useOriginalImageSize = new BooleanPropertyPanel("Original Image Size", getOriginalImageSizeParameters());
        Panel<GameObject> shapePanel = new EnumPropertyPanel<>("Shape", ParticleShape.class, getShapeParameters());
        Panel<GameObject> pointSmoothingPanel = new EnumPropertyPanel<>("Point Smoothing", PointSmoothing.class,
                getPointSmoothingParameters());
        loopAnimationPanel = new BooleanPropertyPanel("Loop Animation", getLoopAnimationParameters());

        quadPanel = new StackPanel(Orientation.VERTICAL, -ROW_HEIGHT);
        pointPanel = new StackPanel(Orientation.VERTICAL, -ROW_HEIGHT);

        quadPanel.add(widthPanel);
        quadPanel.add(heightPanel);
        quadPanel.add(rendererPanel);
        quadPanel.add(texturePanel);

        pointPanel.add(pointSizePanel);
        pointPanel.add(pointSmoothingPanel);

        add(shapePanel);
        add(updateOnGpuPanel);
        add(blendModePanel);
        add(quadPanel);
    }

    private static void setText(Button button, String text) {
        button.setText(text);
        button.setBounds(button.getTextRenderer().getFont().textSize(button.getText(), button.getHorizontalAlignment(),
                button.getVerticalAlignment()));
    }

    private PropertyParameters<Boolean> getUpdateOnGpuParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.setUpdatingOnGpu(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.isUpdatingOnGpu());
            }
        };
    }

    private PropertyParameters<Float> getPointSizeParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                pointSize = getParameter(0);
                defaultParticleRenderer.setPointSize(pointSize);
            }

            public boolean updateIfChanged() {
                return setParameter(0, pointSize);
            }
        };
    }

    private PropertyParameters<Float> getWidthParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                width = getParameter(0);
                defaultParticleRenderer.setBounds(Rectangle.centered(width, height));
                useOriginalSize = width == originalWidth && height == originalHeight;
                updateQuad();
            }

            public boolean updateIfChanged() {
                return setParameter(0, width);
            }
        };
    }

    private PropertyParameters<Float> getHeightParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                height = getParameter(0);
                defaultParticleRenderer.setBounds(Rectangle.centered(width, height));
                useOriginalSize = width == originalWidth && height == originalHeight;
                updateQuad();
            }

            public boolean updateIfChanged() {
                return setParameter(0, height);
            }
        };
    }

    private void updateQuad() {

        int prevouslyRegistered = quadDisposer.getResourceCount();

        quad = Quad.create(Rectangle.centered(width, height), quadDisposer);

        defaultRenderer.setSurface(quad);

        if (texturedRenderer != null) {

            // Recreate the texture renderer with the desired quad.
            loadTexture(textureURL, quad);
        }

        quadDisposer.dispose(0, prevouslyRegistered);
    }

    private PropertyParameters<Boolean> getLoopAnimationParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                if (texturedRenderer == null || !(texturedRenderer instanceof AnimationRenderer)) {
                    return;
                }
                AnimationRenderer animationRenderer = (AnimationRenderer) texturedRenderer;
                animationRenderer.setLooping(getParameter(0));
            }

            public boolean updateIfChanged() {
                if (texturedRenderer == null || !(texturedRenderer instanceof AnimationRenderer)) {
                    return setParameter(0, false);
                }
                AnimationRenderer animationRenderer = (AnimationRenderer) texturedRenderer;
                return setParameter(0, animationRenderer.isLooping());
            }
        };
    }

    private PropertyParameters<Boolean> getOriginalImageSizeParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {

                useOriginalSize = getParameter(0);

                if (texturedRenderer != null) {
                    loadTexture(textureURL, useOriginalSize ? null : quad);
                    useOriginalSize = width == originalWidth && height == originalHeight;
                }
            }

            public boolean updateIfChanged() {
                return setParameter(0, useOriginalSize);
            }
        };
    }

    private PropertyParameters<ParticleRendering> getRendererParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {

                var particleRendering = getParameter(0);

                switch (particleRendering) {
                    case Efficient:
                        particleSystem.setRenderer(defaultParticleRenderer);
                        break;
                    case Iterative:
                        particleSystem.setRenderer(iterativeParticleRenderer);
                        break;
                    default:
                        throw new RuntimeException("Argument out of range");
                }
            }

            public boolean updateIfChanged() {
                return setParameter(0, getCurrentParticleRendering());
            }

            private ParticleRendering getCurrentParticleRendering() {
                return particleSystem.getRenderer() == defaultParticleRenderer ? ParticleRendering.Efficient
                        : ParticleRendering.Iterative;
            }
        };
    }

    private PropertyParameters<BlendMode> getBlendModeParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                blendMode = getParameter(0);
                defaultParticleRenderer.setBlendMode(blendMode);
                defaultRenderer.setBlendMode(blendMode);
                if (texturedRenderer != null)
                    setBlendMode(texturedRenderer, blendMode);
            }

            public boolean updateIfChanged() {
                return setParameter(0, blendMode);
            }
        };
    }

    private void setBlendMode(Renderer renderer, BlendMode blendMode) {
        if (renderer instanceof AbstractShaderRenderer) {
            ((AbstractShaderRenderer) renderer).setBlendMode(blendMode);
        }
    }

    private PropertyParameters<ParticleShape> getShapeParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                ParticleShape shape = getParameter(0);
                defaultParticleRenderer.setParticleShape(shape);
                switch (shape) {
                    case RECTANGLE:
                        remove(pointPanel);
                        add(quadPanel);
                        break;
                    case POINT:
                        remove(quadPanel);
                        add(pointPanel);
                        break;
                }
                recalculateBounds();
            }

            public boolean updateIfChanged() {
                return setParameter(0, defaultParticleRenderer.getParticleShape());
            }
        };
    }

    private PropertyParameters<PointSmoothing> getPointSmoothingParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                defaultParticleRenderer.setPointSmoothing(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, defaultParticleRenderer.getPointSmoothing());
            }
        };
    }

    private PropertyParameters<Boolean> getUseTextureParameters() {

        return new PropertyParameters<>(1) {

            public void updateSetting() {

                boolean useRenderer = getParameter(0);

                if (!useRenderer) {
                    setRenderer(defaultRenderer);
                } else {
                    if (texturedRenderer == null) {
                        setRenderer(defaultRenderer);
                    } else {
                        setRenderer(texturedRenderer);
                    }
                }
            }

            public boolean updateIfChanged() {

                Renderer renderer = particleSystem.getUpdateSettings().getRenderer();

                if (renderer == defaultRenderer) {
                    return setParameter(0, false);
                } else {

                    if (texturedRenderer == null) {
                        return setParameter(0, false);
                    }

                    return setParameter(0, true);
                }
            }
        };
    }

    private MouseReleaseListener getTextureMouseListener() {
        return (obj, button, mods, projectedX, projectedY) -> {
            if (button == Mouse.instance().mouseButton1()) {
                loadTexture((Button) obj, quad);
            }
        };
    }

    private void loadTexture(Button textureButton, Surface quad) {

        File file = fileChooser.browse();

        if (file == null)
            return;

        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        loadTexture(url, useOriginalSize ? null : quad);

        setText(textureButton, file.getName());

        setRenderer(texturedRenderer);
    }

    private void loadTexture(URL url, Surface quad) {
        try {
            quadPanel.remove(loopAnimationPanel);
            quadPanel.remove(useOriginalImageSize);
            quadPanel.remove(useTexturePanel);

            quadPanel.add(useTexturePanel);
            quadPanel.add(useOriginalImageSize);

            textureURL = url;

            int prevouslyRegistered = rendererDisposer.getResourceCount();

            // TODO: When should renderer be set? Should this always be true??
            boolean setRenderer = true;// particleSystem.getEmitter().getIndividualRenderer() == texturedRenderer;

            if (url.getPath().endsWith(".gif")) {
                quadPanel.add(loopAnimationPanel);

                if (quad == null) {
                    var animation = AnimationFactory.create(url, AnimationFormats.GIF, Rectangle.centered(1f, 1f), rendererDisposer);
                    var animationRenderer = new AnimationRenderer(animation, true, rendererDisposer);

                    Rectangle size = animationRenderer.getBounds();
                    originalWidth = size.getWidth();
                    originalHeight = size.getHeight();
                    width = originalWidth;
                    height = originalHeight;

                    texturedRenderer = animationRenderer;
                } else {
                    var animation = AnimationFactory.create(url, AnimationFormats.GIF, quad.getBounds(), rendererDisposer);
                    var animationRenderer = new AnimationRenderer(animation, true, rendererDisposer);

                    Rectangle size = animationRenderer.getBounds();
                    originalWidth = size.getWidth();
                    originalHeight = size.getHeight();

                    texturedRenderer = animationRenderer;
                }
            } else {
                Texture texture = Texture.create(url, rendererDisposer);

                originalWidth = texture.getImageWidth();
                originalHeight = texture.getImageHeight();

                if (quad == null) {
                    width = originalWidth;
                    height = originalHeight;
                    quad = Quad.create(Rectangle.centered(width, height), rendererDisposer);
                }

                texturedRenderer = new SurfaceRenderer(quad, texture);
            }

            // Dispose all previous render objects.
            rendererDisposer.dispose(0, prevouslyRegistered);

            if (setRenderer) {
                setRenderer(texturedRenderer);
            }

            recalculateBounds();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRenderer(Renderer renderer) {
        setBlendMode(renderer, blendMode);
        if (renderer instanceof SurfaceRenderer) {
            defaultParticleRenderer.setTexture(((SurfaceRenderer) renderer).getTexture());
        } else if (renderer instanceof AnimationRenderer) {
            var animation = ((AnimationRenderer) renderer).getAnimation();
            var texture = animation.getFrame(0).getTexture();
            defaultParticleRenderer.setTexture(texture);
        } else {
            defaultParticleRenderer.setTexture(null);
        }

        particleSystem.getUpdateSettings().setRenderer(renderer);
    }
}