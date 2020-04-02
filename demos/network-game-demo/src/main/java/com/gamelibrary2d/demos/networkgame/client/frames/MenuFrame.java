package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.ObjectFactory;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DefaultPanel;
import com.gamelibrary2d.layers.NavigationPanel;
import com.gamelibrary2d.layers.Panel;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderable.TextBox;
import com.gamelibrary2d.util.StackOrientation;

public class MenuFrame extends AbstractFrame {
    private final DemoGame game;

    private NavigationPanel navigationPanel;
    private GameObject mainPanel;
    private GameObject hostPanel;
    private GameObject joinPanel;

    public MenuFrame(DemoGame game) {
        super(game);
        this.game = game;
    }

    private static void center(GameObject obj, Window window) {
        obj.getPosition().set(window.width() / 2f, window.height() / 2f);
        obj.getPosition().subtract(obj.getBounds().center());
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        navigationPanel = new NavigationPanel();
        mainPanel = createMainPanel();
        hostPanel = createHostPanel();
        joinPanel = createJoinPanel();
        initializer.onLoaded(this::onLoaded);
    }

    private void onLoaded(LoadingContext context) {
        add(navigationPanel);
        navigationPanel.navigateTo(mainPanel, false);
    }

    private <T extends GameObject> void stack(Panel<T> panel, T obj) {
        panel.stack(obj, StackOrientation.DOWN);
    }

    private GameObject createMainPanel() {
        var panel = new DefaultPanel<>();

        var localButton = ObjectFactory.createButton("Local", game::startLocalGame);
        var joinButton = ObjectFactory.createButton("Join", () -> navigationPanel.navigateTo(joinPanel, true));
        var hostButton = ObjectFactory.createButton("Host", () -> navigationPanel.navigateTo(hostPanel, true));
        var exitButton = ObjectFactory.createButton("Exit", game::exit);

        stack(panel, localButton);
        stack(panel, joinButton);
        stack(panel, hostButton);
        stack(panel, exitButton);

        center(panel, game.getWindow());

        return panel;
    }

    private int getIntValue(ComposableObject<TextBox> inputField) {
        return Integer.parseInt(inputField.getContent().getText());
    }

    private String getStringValue(ComposableObject<TextBox> inputField) {
        return inputField.getContent().getText();
    }

    private GameObject createHostPanel() {
        var panel = new DefaultPanel<>();

        var tcpField = ObjectFactory.createInputField("4444");
        var udpField = ObjectFactory.createInputField("4444");

        var startButton = ObjectFactory.createButton("Start game", () -> game.hostNetworkGame(getIntValue(tcpField)));
        var backButton = ObjectFactory.createButton("Go back", navigationPanel::goBack);

        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
    }

    private GameObject createJoinPanel() {
        var panel = new DefaultPanel<>();

        var ipField = ObjectFactory.createInputField("localhost");
        var tcpField = ObjectFactory.createInputField("4444");
        var udpField = ObjectFactory.createInputField("4444");

        var startButton = ObjectFactory.createButton("Start game",
                () -> game.joinNetworkGame(getStringValue(ipField), getIntValue(tcpField)));

        var backButton = ObjectFactory.createButton("Go back", navigationPanel::goBack);

        stack(panel, ipField);
        stack(panel, tcpField);
        stack(panel, udpField);
        stack(panel, startButton);
        stack(panel, backButton);

        center(panel, game.getWindow());

        return panel;
    }
}
