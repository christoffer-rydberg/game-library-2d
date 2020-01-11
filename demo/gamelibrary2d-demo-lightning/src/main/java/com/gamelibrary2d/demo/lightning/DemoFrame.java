package com.gamelibrary2d.demo.lightning;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.lightning.*;
import com.gamelibrary2d.objects.AbstractFrame;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.objects.FrameLayer;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

import java.io.IOException;
import java.net.URL;

class DemoFrame extends AbstractFrame {

    DemoFrame(Game game) {
        super(game);
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

    private GameObject createBackground(Window window) throws IOException {
        var bounds = Rectangle.fromBottomLeft(window.getWidth(), window.getHeight());
        var renderer = createRenderer(bounds, "background.jpg");
        return new BasicObject(renderer);
    }

    private GameObject createTorch(DefaultParticleSystem particleSystem, DefaultDynamicLightMap lightMap) throws IOException {
        var bounds = Rectangle.fromTopLeft(32, 64).move(-5, 10);
        var renderer = createRenderer(bounds, "torch.png");
        return new Cursor(getGame(), renderer, lightMap, particleSystem);
    }

    private LightRenderer createLightRenderer(Window window, LightMap lightMap) {
        var lightRenderer = DefaultLightRenderer.create(window.getWidth(), window.getHeight(), this);
        lightRenderer.addLightMap(lightMap);
        lightRenderer.prepare(32, 32);
        return lightRenderer;
    }

    private DefaultParticleSystem createParticleSystem() throws IOException {
        var settings = new ParticleSettingsSaveLoadManager().load(getUrl("fire.particle"));
        return DefaultParticleSystem.create(1000, settings, new EfficientParticleRenderer(), this);
    }

    @Override
    protected void onPrepare() {
        try {
            var window = getGame().getWindow();
            var particleSystem = createParticleSystem();
            var lightMap = new DefaultDynamicLightMap(new DefaultLightSpreadMatrix(20));
            var frameLayer = new FrameLayer();
            frameLayer.add(createBackground(window));
            frameLayer.add(createTorch(particleSystem, lightMap));
            frameLayer.foregroundParticles().add(particleSystem);
            var lightRenderer = createLightRenderer(window, lightMap);
            frameLayer.setLightRenderer(lightRenderer, lightRenderer::apply, lightRenderer::reset);
            add(frameLayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    protected void onUpdate(float deltaTime) {

    }

    @Override
    protected void onReset() {

    }

    @Override
    protected void onDispose() {

    }
}
