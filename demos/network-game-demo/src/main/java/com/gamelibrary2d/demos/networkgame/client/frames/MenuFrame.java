package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.InputField;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.NavigationPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.util.StackOrientation;

public class MenuFrame extends AbstractFrame {
    private final DemoGame game;

    private NavigationPanel navigationPanel;
    private GameObject mainPanel;
    private GameObject hostPanel;
    private GameObject joinPanel;

    public MenuFrame(DemoGame game) {
        this.game = game;
    }

    private static void center(GameObject obj, Window window) {
        obj.setPosition(window.width() / 2f, window.height() / 2f);
        obj.getPosition().subtract(obj.getBounds().center());
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        navigationPanel = new NavigationPanel();
        mainPanel = createMainPanel();
        hostPanel = createHostPanel();
        joinPanel = createJoinPanel();
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {
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

        var localButton = new Button("Local", game::startLocalGame);
        var joinButton = new Button("Join", () -> navigationPanel.navigateTo(joinPanel, true));
        var hostButton = new Button("Host", () -> navigationPanel.navigateTo(hostPanel, true));
        var exitButton = new Button("Exit", game::exit);

        stack(panel, localButton);
        stack(panel, joinButton);
        stack(panel, hostButton);
        stack(panel, exitButton);

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createHostPanel() {
        var panel = new DefaultPanel<>();

        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        var startButton = new Button("Start game", () -> game.hostNetworkGame(tcpField.getIntValue()));
        var backButton = new Button("Go back", navigationPanel::goBack);

        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createJoinPanel() {
        var panel = new DefaultPanel<>();

        var ipField = new InputField("localhost");
        var tcpField = new InputField("4444");
        var udpField = new InputField("4444");

        var startButton = new Button("Start game",
                () -> game.joinNetworkGame(ipField.getStringValue(), tcpField.getIntValue()));

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
