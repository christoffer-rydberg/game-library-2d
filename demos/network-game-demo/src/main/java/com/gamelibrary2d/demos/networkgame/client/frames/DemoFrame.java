package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.ClientObject;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.network.common.client.Client;
import com.gamelibrary2d.network.common.exceptions.NetworkAuthenticationException;
import com.gamelibrary2d.network.common.exceptions.NetworkConnectionException;
import com.gamelibrary2d.network.common.exceptions.NetworkInitializationException;
import com.gamelibrary2d.network.common.initialization.DefaultCommunicationContext;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.renderers.QuadsRenderer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.util.QuadShape;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DemoFrame extends AbstractFrame {
    private final DemoGame game;
    private final DemoFrameClient client;
    private final Map<Byte, Renderable> contents = new HashMap<>();
    private final Map<Byte, ClientObject.UpdateAction> updateActions = new HashMap<>();
    private final Layer<Renderable> backgroundLayer = new BasicLayer<>();
    private final DynamicLayer<Renderable> gameLayer = new DynamicLayer<>();
    private final Layer<Renderable> backgroundEffects = new BasicLayer<>();
    private final Layer<ClientObject> objectLayer = new BasicLayer<>();
    private final Layer<Renderable> foregroundEffects = new BasicLayer<>();

    public DemoFrame(DemoGame game) {
        this.game = game;
        this.client = new DemoFrameClient(this);
    }

    private DefaultParticleSystem loadParticleSystem(String resourceName, int capacity) throws IOException {
        var url = getClass().getClassLoader().getResource(resourceName);
        var settings = new ParticleSettingsSaveLoadManager().load(url);
        return DefaultParticleSystem.create(capacity, settings, this);
    }

    private void initializeObjectParticles(InitializationContext context, byte objectIdentifier, String resourceName)
            throws IOException {
        var portalSystem = loadParticleSystem(resourceName, 10000);
        var portalEmitter = new SequentialParticleEmitter(portalSystem);

        updateActions.put(objectIdentifier, (obj, deltaTime) -> {
            portalEmitter.getPosition().set(obj.getPosition());
            portalEmitter.update(deltaTime);
        });

        context.register(resourceName, portalSystem);
    }

    private Renderable createStars(int count, Rectangle bounds) {
        var random = RandomInstance.get();
        float[] positions = new float[count * 2];
        for (int i = 0; i < count; ++i) {
            var x = bounds.xMin() + random.nextFloat() * bounds.width();
            var y = bounds.yMin() + random.nextFloat() * bounds.height();
            var index = i * 2;
            positions[index] = x;
            positions[index + 1] = y;
        }

        var starPositions = PositionBuffer.create(positions, this);
        var starsRenderer = new QuadsRenderer(Rectangle.centered(6f, 6f));
        starsRenderer.setShape(QuadShape.RADIAL_GRADIENT);
        starsRenderer.setColor(Color.LIGHT_YELLOW);

        return a -> starsRenderer.render(a, starPositions, 0, starPositions.capacity());
    }

    @Override
    protected void onInitialize(InitializationContext context) throws InitializationException {
        try {
            initializeObjectParticles(context, ObjectIdentifiers.PORTAL, "portal.particle");
            initializeObjectParticles(context, ObjectIdentifiers.BOULDER, "boulder.particle");
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

    @Override
    protected void onLoad(InitializationContext context) throws InitializationException {
        try {
            client.clearInbox();
            var clientContext = new DefaultCommunicationContext();
            context.register(clientContext);
            client.prepare(clientContext);
        } catch (NetworkInitializationException | NetworkConnectionException | NetworkAuthenticationException e) {
            throw new InitializationException("Failed to initialize client", e);
        }
    }

    @Override
    protected void onLoaded(InitializationContext context) {
        backgroundEffects.add(context.get(ParticleSystem.class, "portal.particle"));
        foregroundEffects.add(context.get(ParticleSystem.class, "boulder.particle"));

        gameLayer.add(backgroundEffects);
        gameLayer.add(objectLayer);
        gameLayer.add(foregroundEffects);

        add(backgroundLayer);
        add(gameLayer);

        client.prepared(context.get(DefaultCommunicationContext.class));
    }

    @Override
    protected final void handleUpdate(float deltaTime) {
        client.update(deltaTime, dt -> super.handleUpdate(dt));
    }

    void applySettings(GameSettings gameSettings) {
        var windowWidth = game.getWindow().width();
        var windowHeight = game.getWindow().height();
        var gameBounds = gameSettings.getGameBounds();
        var scale = Math.min(windowWidth / gameBounds.width(), windowHeight / gameBounds.height());
        var scaledGameBounds = Rectangle.centered(gameBounds.width(), gameBounds.height()).resize(scale);

        var background = createBackground(
                scaledGameBounds,
                windowWidth / 2f,
                windowHeight / 2f);

        gameLayer.setScale(scale, scale);
        gameLayer.setPosition(
                windowWidth / 2f + scaledGameBounds.xMin(),
                windowHeight / 2f + scaledGameBounds.yMin());

        contents.put(ObjectIdentifiers.PLAYER, createRenderer(
                gameSettings.getSpaceCraftBounds().resize(2f),
                Textures.spacecraft()));

        contents.put(ObjectIdentifiers.BOULDER, createRenderer(
                gameSettings.getBoulderBounds().resize(1.25f),
                Textures.boulder()));

        backgroundLayer.add(background);
    }

    @Override
    protected void onBegin() {
        game.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onEnd() {
        game.setBackgroundColor(Color.BLACK);
    }

    private Renderable createBackground(Rectangle bounds, float posX, float posY) {
        var background = new SurfaceRenderer(Quad.create(bounds, this));
        background.updateSettings(RenderSettings.COLOR_R, 0f, 0f, 0f);
        var stars = createStars(Math.round(bounds.area() * 0.0005f), bounds);

        var space = new BasicObject<>(a -> {
            background.render(a);
            stars.render(a);
        });
        space.setPosition(posX, posY);

        return space;
    }

    private Renderer createRenderer(Rectangle bounds, Texture texture) {
        return new SurfaceRenderer(Quad.create(bounds, this), texture);
    }

    void destroy(ClientObject obj) {
        objectLayer.remove(obj);
    }

    void spawn(ClientObject obj) {
        obj.setContent(contents.get(obj.getObjectIdentifier()));
        obj.setUpdateAction(updateActions.get(obj.getObjectIdentifier()));
        objectLayer.add(obj);
    }

    void goToMenu() {
        game.goToMenu();
    }

    public Client getClient() {
        return client;
    }
}