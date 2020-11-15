package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.network.ClientObject;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.ContentMap;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.EffectMap;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.SoundMap;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.TextureMap;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.PictureFrame;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.TimeLabel;
import com.gamelibrary2d.demos.networkgame.client.urls.Images;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.DefaultLayerObject;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.network.AbstractNetworkFrame;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.DefaultFont;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.ParallelUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.EmptyUpdate;
import com.gamelibrary2d.updates.ScaleUpdate;
import com.gamelibrary2d.util.sound.MusicPlayer;
import com.gamelibrary2d.util.sound.SoundEffectPlayer;

import java.io.IOException;

public class DemoFrame extends AbstractNetworkFrame<DemoFrameClient> {
    private final DemoGame game;

    private final DefaultLayerObject<Renderable> gameLayer = new DefaultLayerObject<>();
    private final Layer<Renderable> backgroundLayer = new BasicLayer<>();
    private final Layer<Renderable> backgroundEffects = new BasicLayer<>();
    private final Layer<ClientObject> objectLayer = new BasicLayer<>();
    private final Layer<Renderable> foregroundEffects = new BasicLayer<>();

    private final SoundMap sounds;
    private final EffectMap effects;
    private final TextureMap textures = new TextureMap();
    private final ContentMap content = new ContentMap();

    private final MusicPlayer musicPlayer;

    private Texture background;
    private TimeLabel timeLabel;
    private GameSettings gameSettings;

    public DemoFrame(DemoGame game, MusicPlayer musicPlayer, SoundEffectPlayer soundPlayer) {
        this.game = game;
        this.musicPlayer = musicPlayer;
        this.sounds = new SoundMap(soundPlayer.getSoundManager());
        this.effects = new EffectMap(sounds, soundPlayer);
        setClient(new DemoFrameClient(this));
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        background = DefaultTexture.create(Images.BACKGROUND, this);

        sounds.initialize();
        textures.initialize(this);
        effects.initialize(textures, this);

        var font = new java.awt.Font("Gabriola", java.awt.Font.BOLD, 64);
        timeLabel = new TimeLabel(new TextRenderer(DefaultFont.create(font, this)));
        timeLabel.setPosition(game.getWindow().width() / 2f, 9 * game.getWindow().height() / 10f);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
        effects.onLoaded(backgroundEffects, foregroundEffects);

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

        gameLayer.setScale(scale, scale);
        gameLayer.setPosition(
                windowWidth / 2f + scaledGameBounds.xMin(),
                windowHeight / 2f + scaledGameBounds.yMin());

        content.initialize(gameSettings, textures, this);

        backgroundLayer.add(createBackground(
                new Rectangle(0, 0, windowWidth, windowHeight),
                scaledGameBounds.move(windowWidth / 2f, windowHeight / 2f)));
    }

    @Override
    protected void onBegin() {
        musicPlayer.play(Music.GAME, 0.5f, 1f, false);
    }

    @Override
    protected void onEnd() {
        musicPlayer.stop(1f);
    }

    private Renderable createBackground(Rectangle windowBounds, Rectangle gameBounds) {
        var gameFrame = PictureFrame.create(windowBounds, gameBounds, Color.BLACK, this);

        var imageRatio = background.getWidth() / background.getHeight();

        var gameBoundsWidth = gameBounds.width();
        var gameBoundsHeight = gameBounds.height();

        var width = Math.max(gameBounds.width(), gameBoundsHeight * imageRatio);
        var height = Math.max(gameBounds.height(), gameBoundsWidth / imageRatio);

        var paddedBackgroundBounds = gameBounds.pad(
                (width - gameBoundsWidth) / 2f,
                (height - gameBoundsHeight) / 2f);

        var backgroundImage = new SurfaceRenderer(
                Quad.create(paddedBackgroundBounds, this),
                background);

        return alpha -> {
            backgroundImage.render(alpha);
            gameFrame.render(alpha);
        };
    }

    void destroy(ClientObject obj) {
        objectLayer.remove(obj);
        obj.destroy();
    }

    void spawn(ClientObject obj) {
        obj.setScale(0f);
        runUpdater(new DurationUpdater(1f, new ScaleUpdate(obj, 1f)));

        obj.setContent(content.get(obj));
        obj.setUpdateEffect(effects.getUpdate(obj));
        obj.setDestroyedEffect(effects.getDestroyed(obj));

        objectLayer.add(obj);
    }

    void goToMenu() {
        game.goToMenu();
    }

    public void gameOver() {
        var portalPosition = gameSettings.getGameBounds().center();

        var parallelUpdater = new ParallelUpdater();
        objectLayer.getChildren().stream()
                .map(obj -> new DurationUpdater(
                        2f,
                        true,
                        new SuckedIntoPortalUpdate(obj, portalPosition)))
                .forEach(parallelUpdater::add);

        var sequentialUpdater = new SequentialUpdater();
        sequentialUpdater.add(new DurationUpdater(1.5f, new EmptyUpdate()));
        sequentialUpdater.add(parallelUpdater);
        sequentialUpdater.add(new InstantUpdater(dt -> objectLayer.clear()));
        sequentialUpdater.add(new DurationUpdater(5f, new EmptyUpdate()));
        sequentialUpdater.add(new InstantUpdater(dt -> getClient().requestNewGame()));

        runUpdater(sequentialUpdater);
    }

    public void gameEnded() {
        objectLayer.clear();
        getClient().requestNewGame();
    }

    public void setTime(int seconds) {
        timeLabel.setTimeFromSeconds(seconds);
    }

    private static class SuckedIntoPortalUpdate implements Updatable {
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
        public void update(float deltaTime) {
            timer += deltaTime;
            target.getPosition().lerp(originX, originY, goalX, goalY, Math.min(timer * 2f, 1f));
            target.setScale(1f - timer);
        }
    }
}