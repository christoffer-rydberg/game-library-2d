package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.network.ClientObject;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.TimeLabel;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.client.urls.Particles;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.DefaultLayerObject;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.network.AbstractNetworkFrame;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.renderers.QuadsRenderer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.ParallelUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.EmptyUpdate;
import com.gamelibrary2d.updates.ScaleUpdate;
import com.gamelibrary2d.updates.Update;
import com.gamelibrary2d.util.BlendMode;
import com.gamelibrary2d.util.QuadShape;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DemoFrame extends AbstractNetworkFrame<DemoFrameClient> {
    private final DemoGame game;

    private final Map<Byte, Renderable> contents = new HashMap<>();
    private final Map<Byte, Func<ClientObject, Updatable>> updateActions = new HashMap<>();
    private final Map<Byte, ParameterizedAction<ClientObject>> destroyActions = new HashMap<>();

    private final Layer<Renderable> backgroundLayer = new BasicLayer<>();
    private final DefaultLayerObject<Renderable> gameLayer = new DefaultLayerObject<>();
    private final Layer<Renderable> backgroundEffects = new BasicLayer<>();
    private final Layer<ClientObject> objectLayer = new BasicLayer<>();
    private final Layer<Renderable> foregroundEffects = new BasicLayer<>();

    private TimeLabel timeLabel;

    private GameSettings gameSettings;

    public DemoFrame(DemoGame game) {
        this.game = game;
        setClient(new DemoFrameClient(this));
    }

    private DefaultParticleSystem loadParticleSystem(
            InitializationContext context, URL url, ParticleRenderer renderer, int capacity)
            throws IOException {
        var settings = new SaveLoadManager().load(url, b -> new ParticleSystemSettings(b, renderer));
        var particleSystem = DefaultParticleSystem.create(capacity, settings, this);
        context.register(url, particleSystem);
        return particleSystem;
    }

    private DefaultParticleSystem loadParticleSystem(InitializationContext context, URL url, int capacity)
            throws IOException {
        return loadParticleSystem(context, url, new EfficientParticleRenderer(), capacity);
    }

    private void initializeUpdateParticles(byte objectIdentifier, DefaultParticleSystem particleSystem) {
        updateActions.put(objectIdentifier, obj -> {
            var particleEmitter = new SequentialParticleEmitter(particleSystem);
            return deltaTime -> {
                particleEmitter.getPosition().set(obj.getParticleHotspot());
                particleEmitter.getPosition().rotate(obj.getDirection());
                particleEmitter.getPosition().add(obj.getPosition());
                particleEmitter.update(deltaTime);
            };
        });
    }

    private void initializeDestructionParticles(byte objectIdentifier, DefaultParticleSystem particleSystem) {
        destroyActions.put(objectIdentifier, obj -> {
            var pos = obj.getPosition();
            particleSystem.emitAll(pos.getX(), pos.getY());
        });
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
    protected void onInitialize(InitializationContext context) throws IOException {
        var portalPS = loadParticleSystem(context, Particles.PORTAL, 1000);
        initializeUpdateParticles(ObjectIdentifiers.PORTAL, portalPS);

        var boulderPS = loadParticleSystem(context, Particles.BOULDER, 10000);
        initializeUpdateParticles(ObjectIdentifiers.BOULDER, boulderPS);

        var enginePS = loadParticleSystem(context, Particles.ENGINE, 1000);
        initializeUpdateParticles(ObjectIdentifiers.PLAYER, enginePS);

        var shockwavePS = loadParticleSystem(context, Particles.SHOCK_WAVE, 1000);

        var renderer = new EfficientParticleRenderer();
        renderer.setBlendMode(BlendMode.TRANSPARENCY);
        renderer.setTexture(Textures.boulder());
        var boulderExplosionPS = loadParticleSystem(
                context, Particles.BOULDER_EXPLOSION, renderer, 1000);

        var font = new java.awt.Font("Gabriola", java.awt.Font.BOLD, 64);
        timeLabel = new TimeLabel(new TextRenderer(Font.create(font, this)));
        timeLabel.setPosition(game.getWindow().width() / 2f, 9 * game.getWindow().height() / 10f);

        destroyActions.put(ObjectIdentifiers.BOULDER, obj -> {
            var pos = obj.getPosition();
            shockwavePS.emitAll(pos.getX(), pos.getY());
            boulderExplosionPS.emitAll(pos.getX(), pos.getY());
        });

        var explosionPS = loadParticleSystem(context, Particles.EXPLOSION, 1000);
        initializeDestructionParticles(ObjectIdentifiers.PLAYER, explosionPS);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
        backgroundEffects.add(context.get(ParticleSystem.class, Particles.PORTAL));
        backgroundEffects.add(context.get(ParticleSystem.class, Particles.BOULDER));

        foregroundEffects.add(context.get(ParticleSystem.class, Particles.ENGINE));
        foregroundEffects.add(context.get(ParticleSystem.class, Particles.EXPLOSION));
        foregroundEffects.add(context.get(ParticleSystem.class, Particles.SHOCK_WAVE));

        gameLayer.add(backgroundEffects);
        gameLayer.add(objectLayer);
        gameLayer.add(foregroundEffects);

        add(backgroundLayer);
        add(gameLayer);
        add(timeLabel);
    }

    void applySettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;

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

        var space = new DefaultGameObject<>(a -> {
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
        obj.destroy();
    }

    void spawn(ClientObject obj) {
        obj.setScale(0f);
        runUpdater(new DurationUpdater(1f, new ScaleUpdate(obj, 1f)));

        obj.setContent(contents.get(obj.getObjectIdentifier()));

        var updateActionFactory = updateActions.get(obj.getObjectIdentifier());
        if (updateActionFactory != null) {
            obj.setUpdateAction(updateActionFactory.invoke(obj));
        }

        obj.setDestroyAction(destroyActions.get(obj.getObjectIdentifier()));

        objectLayer.add(obj);
    }

    void goToMenu() {
        game.goToMenu();
    }

    public void gameOver() {
        var portalPosition = gameSettings.getGameBounds().center();

        var parallelUpdater = new ParallelUpdater();
        objectLayer.getChildren().stream()
                .map(obj -> new DurationUpdater(2f, new SuckedIntoPortalUpdate(obj, portalPosition)))
                .forEach(updater -> parallelUpdater.add(updater));

        var sequentialUpdater = new SequentialUpdater();
        sequentialUpdater.add(new DurationUpdater(1.5f, new EmptyUpdate()));
        sequentialUpdater.add(parallelUpdater);
        sequentialUpdater.add(new InstantUpdater((x, y) -> objectLayer.clear()));
        sequentialUpdater.add(new DurationUpdater(5f, new EmptyUpdate()));
        sequentialUpdater.add(new InstantUpdater((x, y) -> getClient().requestNewGame()));

        runUpdater(sequentialUpdater);
    }

    public void gameEnded() {
        objectLayer.clear();
        getClient().requestNewGame();
    }

    public void setTime(int seconds) {
        timeLabel.setTimeFromSeconds(seconds);
    }

    private class SuckedIntoPortalUpdate implements Update {
        private final ClientObject target;
        private final float originX;
        private final float originY;
        private final float goalX;
        private final float goalY;

        private float timer;

        SuckedIntoPortalUpdate(ClientObject target, Point goal) {
            this.target = target;
            this.originX = target.getPosition().getX();
            this.originY = target.getPosition().getY();
            this.goalX = goal.getX();
            this.goalY = goal.getY();
        }

        @Override
        public void apply(float deltaTime, float scaledDeltaTime) {
            timer += scaledDeltaTime;
            target.getPosition().lerp(originX, originY, goalX, goalY, Math.min(timer * 2f, 1f));
            target.setScale(1f - timer);
        }
    }
}