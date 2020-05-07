package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.InputField;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.demos.networkgame.client.urls.Particles;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.NavigationPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.updaters.InfiniteUpdater;
import com.gamelibrary2d.util.StackOrientation;
import com.gamelibrary2d.util.sound.MusicPlayer;
import com.gamelibrary2d.util.sound.SoundEffectPlayer;

import java.io.IOException;

public class MenuFrame extends AbstractFrame {
    private final DemoGame game;
    private final MusicPlayer musicPlayer;
    private final SoundEffectPlayer soundPlayer;

    private NavigationPanel navigationPanel;
    private GameObject mainPanel;
    private GameObject hostPanel;
    private GameObject joinPanel;

    public MenuFrame(MusicPlayer musicPlayer, SoundEffectPlayer soundPlayer, DemoGame game) {
        this.game = game;
        this.musicPlayer = musicPlayer;
        this.soundPlayer = soundPlayer;
    }

    private static void center(GameObject obj, Window window) {
        obj.setPosition(window.width() / 2f, window.height() / 2f);
        obj.getPosition().subtract(obj.getBounds().center());
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        navigationPanel = new NavigationPanel();
        mainPanel = createMainPanel();
        hostPanel = createHostPanel();
        joinPanel = createJoinPanel();

        var settings = new ParticleSettingsSaveLoadManager().load(Particles.MENU);
        var particleSystem = DefaultParticleSystem.create(10000, settings, this);
        var particleEmitter = new SequentialParticleEmitter(particleSystem);
        particleEmitter.getPosition().set(game.getWindow().width() / 2, game.getWindow().height() / 2);
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
        navigationPanel.navigateTo(mainPanel, false);
    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private <T extends GameObject> void stack(Panel<T> panel, T obj) {
        panel.stack(obj, StackOrientation.DOWN);
    }

    private GameObject createMainPanel() {
        var panel = new DefaultPanel<>();

        stack(panel, new Button("Local", game::startLocalGame));
        stack(panel, new Button("Join", () -> navigationPanel.navigateTo(joinPanel, true)));
        stack(panel, new Button("Host", () -> navigationPanel.navigateTo(hostPanel, true)));
        stack(panel, new Button("Exit", game::exit));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createHostPanel() {
        var panel = new DefaultPanel<>();

        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, new Button("Start game", () ->
                game.hostNetworkGame(tcpField.getIntValue(), udpField.getIntValue())));
        stack(panel, new Button("Go back", navigationPanel::goBack));

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createJoinPanel() {
        var panel = new DefaultPanel<>();

        var ipField = new InputField("localhost");
        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        var startButton = new Button("Start game",
                () -> game.joinNetworkGame(ipField.getStringValue(), tcpField.getIntValue(), udpField.getIntValue()));

        var backButton = new Button("Go back", navigationPanel::goBack);

        stack(panel, ipField);
        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
    }
}
