package com.gamelibrary2d.demos.networkgame.client.frames.menu;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.urls.Images;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.components.frames.AbstractFrame;
import com.gamelibrary2d.components.frames.InitializationContext;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.containers.*;
import com.gamelibrary2d.components.denotations.KeyAware;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.objects.DefaultGameObject;
import com.gamelibrary2d.components.objects.GameObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.sound.MusicPlayer;
import com.gamelibrary2d.sound.SoundPlayer;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.OpacityUpdate;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.Label;

import java.io.IOException;
import java.io.InputStream;

public class MenuFrame extends AbstractFrame implements KeyAware {
    private final DemoGame game;
    private final ResourceManager resourceManager;
    private final MusicPlayer musicPlayer;
    private final SoundPlayer soundPlayer;
    private final SequentialUpdater introUpdater = new SequentialUpdater();

    private Credits credits;
    private Layer<Renderable> foregroundLayer;
    private LayerObject<Renderable> backgroundLayer;

    private boolean foregroundLayerIsHidden = true;

    public MenuFrame(DemoGame game, ResourceManager resourceManager, MusicPlayer musicPlayer, SoundPlayer soundPlayer) {
        this.game = game;
        this.resourceManager = resourceManager;
        this.musicPlayer = musicPlayer;
        this.soundPlayer = soundPlayer;
    }

    private DefaultTexture createTexture(InputStream s) throws IOException {
        return DefaultTexture.create(s, this);
    }

    private Renderable createBackground() throws IOException {
        Texture backgroundTexture = resourceManager.load(Images.MENU_BACKGROUND, this::createTexture);

        Surface backgroundSurface = Surfaces.coverArea(
                new Rectangle(0, 0, game.getWindow().getWidth(), game.getWindow().getHeight()),
                backgroundTexture.getWidth(),
                backgroundTexture.getHeight(),
                this);

        return new SurfaceRenderer<>(backgroundSurface, backgroundTexture);
    }

    private LayerObject<Renderable> createBackgroundLayer() throws IOException {
        DefaultLayerObject<Renderable> backgroundLayer = new DefaultLayerObject<>();
        backgroundLayer.setAutoClearing(false);
        backgroundLayer.add(createBackground());
        return backgroundLayer;
    }

    private GameObject createGameTitle() throws IOException {
        Texture texture = resourceManager.load(
                Images.MENU_TITLE,
                stream -> DefaultTexture.create(stream, this));

        Surface surface = Quad.create(Rectangle.create(texture.getWidth(), texture.getHeight()), this);
        Renderer renderer = new SurfaceRenderer<>(surface, texture);
        return new DefaultGameObject<>(renderer);
    }

    private Layer<Renderable> createForegroundLayer() throws IOException {
        GameObject gameTitle = createGameTitle();
        gameTitle.setPosition(
                game.getWindow().getWidth() / 2f,
                game.getWindow().getHeight() * 0.75f);

        GameObject creditsButton = createCreditsButton();
        creditsButton.setPosition(
                19 * game.getWindow().getWidth() / 20f,
                game.getWindow().getHeight() / 20f);

        BasicLayer<Renderable> foregroundLayer = new BasicLayer<>();
        foregroundLayer.setAutoClearing(false);
        foregroundLayer.add(gameTitle);
        foregroundLayer.add(new MenuPanel(game));
        foregroundLayer.add(creditsButton);

        return foregroundLayer;
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        backgroundLayer = createBackgroundLayer();
        foregroundLayer = createForegroundLayer();
        credits = new Credits(game.getWindow(), resourceManager);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
        hideCredits();
        add(backgroundLayer);
        add(foregroundLayer);
        add(credits);
        runIntro();
    }

    private void runIntro() {
        foregroundLayer.setOpacity(0f);
        backgroundLayer.setScale(10f);

        introUpdater.clear();

        introUpdater.add(new DurationUpdater(
                19f,
                true,
                new IntroZoomUpdate(backgroundLayer)
        ));

        introUpdater.add(new InstantUpdater(dt -> ShowMenu()));

        runUpdater(introUpdater);
    }

    private void ShowMenu() {
        if (foregroundLayerIsHidden) {
            runUpdater(new DurationUpdater(
                    4f,
                    true,
                    new OpacityUpdate<>(foregroundLayer, 1f)
            ));

            foregroundLayerIsHidden = false;
        }
    }

    @Override
    protected void onBegin() {
        musicPlayer.play(Music.MENU, 1f, false, 10f, false);
    }

    @Override
    protected void onEnd() {
        musicPlayer.stop(2f);
    }

    private void showCredits() {
        foregroundLayer.setEnabled(false);
        credits.enable(this::hideCredits);
    }

    private void hideCredits() {
        credits.disable();
        foregroundLayer.setEnabled(true);
    }

    private Button createCreditsButton() {
        Label label = new Label("Credits", new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.BOTTOM);
        return new Button(label, label.calculateBounds(), this::showCredits);
    }

    @Override
    public void keyDown(int key, boolean repeat) {
        if (key == Keyboard.instance().keyEscape()) {
            if (credits.isEnabled()) {
                hideCredits();
            } else {
                ShowMenu();
            }
        } else if (key == Keyboard.instance().keyEnter()) {
            if (credits.isEnabled()) {
                credits.setSpeedFactor(10f);
            }
        }
    }

    @Override
    public void keyUp(int key) {
        if (key == Keyboard.instance().keyEnter()) {
            if (credits.isEnabled()) {
                credits.setSpeedFactor(1f);
            }
        }
    }

    private static class IntroZoomUpdate implements Updatable {
        private final GameObject obj;
        private final float initialScale;

        private float alpha;

        IntroZoomUpdate(GameObject obj) {
            this.obj = obj;
            this.initialScale = obj.getScale().getX();
        }

        @Override
        public void update(float deltaTime) {
            alpha += deltaTime;
            float radius = initialScale - 1;
            float x = -radius * (1f - alpha) + 0 * alpha;
            double y = Math.sqrt(radius * radius - x * x);
            obj.setScale(initialScale - (float) y);
        }
    }
}
