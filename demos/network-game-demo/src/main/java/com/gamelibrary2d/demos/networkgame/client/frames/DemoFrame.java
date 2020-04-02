package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.client.objects.ClientBoulder;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.layers.LayerObject;
import com.gamelibrary2d.network.AbstractNetworkFrame;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.util.RenderSettings;

public class DemoFrame extends AbstractNetworkFrame<DemoFrameClient> {
    private Renderable gameArea;
    private Renderable boulderRenderer;
    private LayerObject<Renderable> objectLayer = new DynamicLayer<>();

    public DemoFrame(Game game) {
        super(game, new DemoFrameClient());
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        getClient().initialize(this);
        initializer.onLoaded(this::onLoaded);
    }

    private Renderable createGameArea(Rectangle bounds, float posX, float posY) {
        var quad = Quad.create(bounds, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(RenderSettings.COLOR_R, 1f, 1f, 1f);
        var gameArea = new BasicObject<>(renderer);
        gameArea.setPosition(posX, posY);
        return gameArea;
    }

    private Renderer createBoulderRenderer(Rectangle bounds) {
        var quad = Quad.create(bounds.resize(1.25f), this);
        return new SurfaceRenderer(quad, Textures.boulder());
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

        boulderRenderer = createBoulderRenderer(gameSettings.getBoulderBounds());
    }

    private void onLoaded(LoadingContext loadingContext) {
        add(gameArea);
        add(objectLayer);
    }

    void addBoulder(ClientBoulder boulder) {
        boulder.setContent(boulderRenderer);
        objectLayer.add(boulder);
    }
}