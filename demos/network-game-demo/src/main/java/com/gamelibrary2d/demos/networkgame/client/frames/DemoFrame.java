package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.objects.DemoClientObject;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.layers.LayerObject;
import com.gamelibrary2d.network.AbstractNetworkFrame;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.util.RenderSettings;

import java.util.HashMap;
import java.util.Map;

public class DemoFrame extends AbstractNetworkFrame<DemoFrameClient> {
    private final DemoGame game;
    private Renderable gameArea;
    private Map<Byte, Renderable> renderers = new HashMap<>();
    private LayerObject<Renderable> objectLayer = new DynamicLayer<>();

    public DemoFrame(DemoGame game) {
        super(game, new DemoFrameClient());
        this.game = game;
    }

    @Override
    protected void onInitialize() {
        getClient().initialize(this);
    }

    @Override
    protected void onLoad(LoadingContext context) {

    }

    @Override
    protected void onLoaded(LoadingContext loadingContext) {
        add(gameArea);
        add(objectLayer);
    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private Renderable createGameArea(Rectangle bounds, float posX, float posY) {
        var quad = Quad.create(bounds, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(RenderSettings.COLOR_R, 1f, 1f, 1f);
        var gameArea = new BasicObject<>(renderer);
        gameArea.setPosition(posX, posY);
        return gameArea;
    }

    private Renderer createRenderer(Rectangle bounds, Texture texture) {
        return new SurfaceRenderer(Quad.create(bounds, this), texture);
    }

    void applySettings(GameSettings gameSettings) {
        var windowWidth = getGame().getWindow().width();
        var windowHeight = getGame().getWindow().height();
        var gameBounds = gameSettings.getGameBounds();
        var scale = Math.min(windowWidth / gameBounds.width(), windowHeight / gameBounds.height());
        var scaledGameBounds = Rectangle.centered(gameBounds.width(), gameBounds.height()).resize(scale);

        gameArea = createGameArea(
                scaledGameBounds,
                windowWidth / 2f,
                windowHeight / 2f);

        objectLayer.setScale(scale, scale);
        objectLayer.setPosition(
                windowWidth / 2f + scaledGameBounds.xMin(),
                windowHeight / 2f + scaledGameBounds.yMin());

        renderers.put(ObjectIdentifiers.PLAYER, createRenderer(
                gameSettings.getSpaceCraftBounds().resize(2f),
                Textures.spacecraft()));

        renderers.put(ObjectIdentifiers.PORTAL, createRenderer(
                gameSettings.getPortalBounds().resize(2f),
                Textures.boulder()));

        renderers.put(ObjectIdentifiers.BOULDER, createRenderer(
                gameSettings.getBoulderBounds().resize(1.25f),
                Textures.boulder()));
    }

    void destroy(DemoClientObject obj) {
        objectLayer.remove(obj);
    }

    void spawn(DemoClientObject obj) {
        obj.setContent(renderers.get(obj.getObjectIdentifier()));
        objectLayer.add(obj);
    }

    void goToMenu() {
        game.goToMenu();
    }

}