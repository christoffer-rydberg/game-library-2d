package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.InputField;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.client.urls.Images;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.*;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.objects.GameObject;
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
import com.gamelibrary2d.updates.PositionUpdate;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.StackOrientation;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.Label;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MenuFrame extends AbstractFrame implements KeyAware {
    private final DemoGame game;
    private final ResourceManager resourceManager;
    private final MusicPlayer musicPlayer;
    private final SoundPlayer soundPlayer;
    private final SequentialUpdater introUpdater = new SequentialUpdater();
    private LayerObject<Renderable> menu;
    private Credits credits;
    private LayerObject<Renderable> backgroundLayer;
    private Renderable background;
    private NavigationPanel navigationPanel;
    private GameObject gameTitle;
    private GameObject mainPanel;
    private GameObject hostPanel;
    private GameObject joinPanel;
    private GameObject creditsButton;

    private boolean menuIsHidden = true;

    public MenuFrame(DemoGame game, ResourceManager resourceManager, MusicPlayer musicPlayer, SoundPlayer soundPlayer) {
        this.game = game;
        this.resourceManager = resourceManager;
        this.musicPlayer = musicPlayer;
        this.soundPlayer = soundPlayer;
    }

    private static void center(GameObject obj, Window window) {
        obj.setPosition(window.getWidth() / 2f, window.getHeight() / 2f);
        obj.getPosition().subtract(obj.getBounds().getCenter());
    }

    private DefaultTexture createTexture(InputStream s) throws IOException {
        return DefaultTexture.create(s, this);
    }

    private GameObject createGameTitle() throws IOException {
        Texture texture = resourceManager.load(Images.MENU_TITLE, this::createTexture);
        Surface surface = Quad.create(Rectangle.create(texture.getWidth(), texture.getHeight()), this);
        Renderer renderer = new SurfaceRenderer<>(surface, texture);
        return new DefaultGameObject<>(renderer);
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

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        background = createBackground();

        navigationPanel = new NavigationPanel();
        mainPanel = createMainPanel();
        hostPanel = createHostPanel();
        joinPanel = createJoinPanel();

        creditsButton = createCreditsButton();
        creditsButton.setPosition(
                19 * game.getWindow().getWidth() / 20f,
                game.getWindow().getHeight() / 20f);

        gameTitle = createGameTitle();
        gameTitle.setPosition(
                game.getWindow().getWidth() / 2f,
                game.getWindow().getHeight() * 0.75f);

        backgroundLayer = new DefaultLayerObject<>();
        menu = new DefaultLayerObject<>();
        credits = new Credits(game.getWindow(), resourceManager);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
        hideCredits();
        add(backgroundLayer);
        add(menu);
        add(credits);
        backgroundLayer.add(background);
        menu.add(navigationPanel);
        menu.add(gameTitle);
        menu.add(creditsButton);
        navigationPanel.navigateTo(mainPanel, false);

        runIntro();
    }

    private void runIntro() {
        menu.setOpacity(0f);
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
        if (menuIsHidden) {
            runUpdater(new DurationUpdater(
                    4f,
                    true,
                    new OpacityUpdate(menu, 1f)
            ));

            menuIsHidden = false;
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

    private <T extends GameObject> void stack(Panel<T> panel, T obj) {
        panel.stack(obj, StackOrientation.DOWN);
    }

    private void showCredits() {
        menu.setEnabled(false);
        credits.show(this::hideCredits);
    }

    private void hideCredits() {
        credits.hide();
        menu.setEnabled(true);
    }

    private Button createCreditsButton() {
        Label label = new Label("Credits", new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.BOTTOM);
        return new Button(label, label.calculateBounds(), this::showCredits);
    }

    private Button createButton(String text, Action onClick) {
        Renderer background = new SurfaceRenderer<>(
                Surfaces.button(),
                Textures.button());

        background.getParameters().setColor(Settings.BUTTON_COLOR);

        Label label = new Label(text, new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);

        return new Button(label, background, background.getBounds(), onClick);
    }

    private GameObject createMainPanel() {
        Panel<GameObject> panel = new DefaultPanel<>();

        stack(panel, createButton("Local", game::startLocalGame));
        stack(panel, createButton("Join", () -> navigationPanel.navigateTo(joinPanel, true)));
        stack(panel, createButton("Host", () -> navigationPanel.navigateTo(hostPanel, true)));
        stack(panel, createButton("Exit", game::exit));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createHostPanel() {
        Panel<GameObject> panel = new DefaultPanel<>();

        InputField tcpField = new InputField("4444");
        InputField udpField = new InputField("4444");

        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, createButton("Start game", () ->
                game.hostNetworkGame(tcpField.getIntValue(), udpField.getIntValue())));
        stack(panel, createButton("Go back", navigationPanel::goBack));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createJoinPanel() {
        Panel<GameObject> panel = new DefaultPanel<>();

        InputField ipField = new InputField("localhost");
        InputField tcpField = new InputField("4444");
        InputField udpField = new InputField("4444");

        Button startButton = createButton("Start game",
                () -> game.joinNetworkGame(ipField.getStringValue(), tcpField.getIntValue(), udpField.getIntValue()));

        Button backButton = createButton("Go back", navigationPanel::goBack);

        stack(panel, ipField);
        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
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

    private static class Credits extends AbstractLayer<GameObject> {
        private final Window window;
        private final SequentialUpdater updater = new SequentialUpdater();
        private DefaultGameObject<Label> credits;
        private float speedFactor;

        Credits(Window window, ResourceManager resourceManager) throws IOException {
            this.window = window;
            try (InputStream stream = resourceManager.open("credits.txt")) {
                String text = Read.text(stream, StandardCharsets.UTF_8);
                Label label = new Label(text, new TextRenderer(Fonts.button()), Color.SOFT_BLUE);
                credits = new DefaultGameObject<>(label);
                add(credits);
            }
        }

        void setSpeedFactor(float speedFactor) {
            this.speedFactor = speedFactor;
        }

        @Override
        protected void onUpdate(float deltaTime) {
            super.onUpdate(deltaTime);
            updater.update(deltaTime * speedFactor);
        }

        void hide() {
            setEnabled(false);
        }

        void show(Action onFinished) {
            setEnabled(true);
            setSpeedFactor(1f);

            credits.setPosition(window.getWidth() / 2f, 0);
            float height = credits.getContent().calculateBounds().getHeight();
            float windowHeight = window.getHeight();

            updater.clear();

            updater.add(new DurationUpdater(
                    60f,
                    true,
                    new PositionUpdate(credits, 0, height + windowHeight)));

            updater.add(new InstantUpdater(dt -> onFinished.perform()));
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
