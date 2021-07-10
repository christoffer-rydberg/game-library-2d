package com.gamelibrary2d.demos.networkgame.client.frames.menu;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.settings.Colors;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.Button;
import com.gamelibrary2d.demos.networkgame.client.objects.widgets.InputField;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.components.containers.DefaultPanel;
import com.gamelibrary2d.components.containers.NavigationPanel;
import com.gamelibrary2d.components.containers.Panel;
import com.gamelibrary2d.components.objects.AbstractPointerAwareComposedGameObject;
import com.gamelibrary2d.components.objects.GameObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.StackOrientation;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.Label;

class MenuPanel extends AbstractPointerAwareComposedGameObject<NavigationPanel> {
    MenuPanel(DemoGame game) {
        NavigationPanel navigationPanel = new NavigationPanel();
        GameObject hostPanel = createHostPanel(game, navigationPanel);
        GameObject joinPanel = createJoinPanel(game, navigationPanel);
        GameObject mainPanel = createMainPanel(game, navigationPanel, hostPanel, joinPanel);
        navigationPanel.navigateTo(mainPanel, false);
        setContent(navigationPanel);
    }

    private static <T extends GameObject> void stack(Panel<T> panel, T obj) {
        panel.stack(obj, StackOrientation.DOWN);
    }

    private static Button createButton(String text, Action onClick) {
        Renderer background = new SurfaceRenderer<>(
                Surfaces.button(),
                Textures.button());

        background.getParameters().setColor(Colors.BUTTON_COLOR);

        Label label = new Label(text, new TextRenderer(Fonts.button()));
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);

        return new Button(label, background, background.getBounds(), onClick);
    }

    private static void center(GameObject obj, Window window) {
        obj.setPosition(window.getWidth() / 2f, window.getHeight() / 2f);
        obj.getPosition().subtract(obj.getBounds().getCenter());
    }

    private static GameObject createMainPanel(DemoGame game, NavigationPanel navigationPanel, GameObject hostPanel, GameObject joinPanel) {
        Panel<GameObject> panel = new DefaultPanel<>();

        stack(panel, createButton("Local", game::startLocalGame));
        stack(panel, createButton("Join", () -> navigationPanel.navigateTo(joinPanel, true)));
        stack(panel, createButton("Host", () -> navigationPanel.navigateTo(hostPanel, true)));
        stack(panel, createButton("Exit", game::exit));

        center(panel, game.getWindow());

        return panel;
    }

    private static GameObject createHostPanel(DemoGame game, NavigationPanel navigationPanel) {
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

    private static GameObject createJoinPanel(DemoGame game, NavigationPanel navigationPanel) {
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
}
