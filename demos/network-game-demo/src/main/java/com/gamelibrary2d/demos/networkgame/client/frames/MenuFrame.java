package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.InputField;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.client.urls.Images;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.demos.networkgame.client.urls.Particles;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.NavigationPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.updaters.InfiniteUpdater;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.StackOrientation;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.util.sound.MusicPlayer;
import com.gamelibrary2d.util.sound.SoundEffectPlayer;
import com.gamelibrary2d.widgets.Label;

import java.io.IOException;

public class MenuFrame extends AbstractFrame {
    private final DemoGame game;
    private final MusicPlayer musicPlayer;
    private final SoundEffectPlayer soundPlayer;

    private NavigationPanel navigationPanel;
    private GameObject gameTitle;
    private GameObject mainPanel;
    private GameObject hostPanel;
    private GameObject joinPanel;

    private GameObject creditsButton;

    public MenuFrame(DemoGame game, MusicPlayer musicPlayer, SoundEffectPlayer soundPlayer) {
        this.game = game;
        this.musicPlayer = musicPlayer;
        this.soundPlayer = soundPlayer;
    }

    private static void center(GameObject obj, Window window) {
        obj.setPosition(window.width() / 2f, window.height() / 2f);
        obj.getPosition().subtract(obj.getBounds().center());
    }

    private GameObject createGameTitle() throws IOException {
        var texture = DefaultTexture.create(Images.MENU_TITLE, this);
        var surface = Quad.create(Rectangle.centered(texture.getWidth(), texture.getHeight()), this);
        var renderer = new SurfaceRenderer(surface, texture);
        return new DefaultGameObject<>(renderer);
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        navigationPanel = new NavigationPanel();
        mainPanel = createMainPanel();
        hostPanel = createHostPanel();
        joinPanel = createJoinPanel();

        creditsButton = createCreditsButton();
        creditsButton.setPosition(
                19 * game.getWindow().width() / 20f,
                game.getWindow().height() / 20f);

        gameTitle = createGameTitle();
        gameTitle.setPosition(
                game.getWindow().width() / 2,
                game.getWindow().height() * 0.75f);

        var settings = new SaveLoadManager().load(Particles.MENU, ParticleSystemParameters::new);
        var particleSystem = DefaultParticleSystem.create(settings, this);
        var particleEmitter = new SequentialParticleEmitter(particleSystem);
        particleEmitter.getPosition().set(game.getWindow().width() / 2f, game.getWindow().height() / 2f);

        context.register(particleEmitter);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
        var particleEmitter = context.get(SequentialParticleEmitter.class);
        runUpdater(new InfiniteUpdater(particleEmitter));
        add(particleEmitter.getParticleSystem());
        add(navigationPanel);
        add(gameTitle);
        add(creditsButton);
        navigationPanel.navigateTo(mainPanel, false);
    }

    @Override
    protected void onBegin() {
        musicPlayer.play(Music.MENU, 1f, 10f, false);
    }

    @Override
    protected void onEnd() {
        musicPlayer.stop(1f);
    }

    private <T extends GameObject> void stack(Panel<T> panel, T obj) {
        panel.stack(obj, StackOrientation.DOWN);
    }

    private Button createCreditsButton() {
        var label = new Label("Credits", new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.BOTTOM);
        return new Button(label, label.calculateBounds(), game::goToCredits);
    }

    private Button createButton(String text, Action onClick) {
        var background = new SurfaceRenderer(
                Surfaces.button(),
                Textures.button());

        background.getParameters().setRgba(Settings.BUTTON_COLOR);

        var label = new Label(text, new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);

        return new Button(label, background, background.getBounds(), onClick);
    }

    private GameObject createMainPanel() {
        var panel = new DefaultPanel<>();

        stack(panel, createButton("Local", game::startLocalGame));
        stack(panel, createButton("Join", () -> navigationPanel.navigateTo(joinPanel, true)));
        stack(panel, createButton("Host", () -> navigationPanel.navigateTo(hostPanel, true)));
        stack(panel, createButton("Exit", game::exit));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createHostPanel() {
        var panel = new DefaultPanel<>();

        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, createButton("Start game", () ->
                game.hostNetworkGame(tcpField.getIntValue(), udpField.getIntValue())));
        stack(panel, createButton("Go back", navigationPanel::goBack));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createJoinPanel() {
        var panel = new DefaultPanel<>();

        var ipField = new InputField("localhost");
        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        var startButton = createButton("Start game",
                () -> game.joinNetworkGame(ipField.getStringValue(), tcpField.getIntValue(), udpField.getIntValue()));

        var backButton = createButton("Go back", navigationPanel::goBack);

        stack(panel, ipField);
        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
    }
}
