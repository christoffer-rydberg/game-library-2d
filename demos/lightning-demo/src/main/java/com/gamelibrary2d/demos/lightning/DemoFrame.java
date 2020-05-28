package com.gamelibrary2d.demos.lightning;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DefaultLayerObject;
import com.gamelibrary2d.lightning.*;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

import java.io.IOException;
import java.net.URL;

class DemoFrame extends AbstractFrame {

    private final Game game;

    DemoFrame(Game game) {
        this.game = game;
    }

    private URL getUrl(String resource) {
        return getClass().getClassLoader().getResource(resource);
    }

    private SurfaceRenderer createRenderer(Rectangle bounds, String image) throws IOException {
        var backgroundUrl = getUrl(image);
        var backgroundTexture = Texture.create(backgroundUrl, this);
        var backgroundQuad = Quad.create(bounds, this);
        return new SurfaceRenderer(backgroundQuad, backgroundTexture);
    }

    private Renderable createBackground(Window window) throws IOException {
        var bounds = Rectangle.fromBottomLeft(window.width(), window.height());
        return createRenderer(bounds, "background.jpg");
    }

    private Renderable createTorch(DefaultParticleSystem particleSystem, DefaultDynamicLightMap lightMap) throws IOException {
        var bounds = Rectangle.fromTopLeft(32, 64).move(-5, 10);
        var renderer = createRenderer(bounds, "torch.png");
        return new Cursor(game, renderer, lightMap, particleSystem);
    }

    private LightRenderer createLightRenderer(Window window, LightMap lightMap) {
        var lightRenderer = DefaultLightRenderer.create(window.width(), window.height(), this);
        lightRenderer.addLightMap(lightMap);
        lightRenderer.prepare(32, 32);
        return lightRenderer;
    }

    private DefaultParticleSystem createParticleSystem() throws IOException {
        var settings = new SaveLoadManager().load(getUrl("fire.particle"), ParticleSystemSettings::new);
        return DefaultParticleSystem.create(1000, settings, this);
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        try {
            var window = game.getWindow();
            var particleSystem = createParticleSystem();
            var lightMap = new DefaultDynamicLightMap(new DefaultLightSpreadMatrix(20));
            var frameLayer = new DefaultLayerObject<>();
            frameLayer.getBackground().add(createBackground(window));
            frameLayer.add(createTorch(particleSystem, lightMap));
            frameLayer.getForeground().add(particleSystem);
            var lightRenderer = createLightRenderer(window, lightMap);
            frameLayer.setOverlay(alpha -> {
                lightRenderer.apply();
                lightRenderer.render(alpha);
                lightRenderer.reset();
            });
            add(frameLayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}
