package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.demos.networkgame.client.objects.ClientBoulder;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
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

import java.io.IOException;

public class DemoFrame extends AbstractNetworkFrame<DemoFrameClient> {
    private Renderer boulderRenderer;
    private Texture boulderTexture;
    private LayerObject<Renderable> objectLayer = new DynamicLayer<>();

    public DemoFrame(Game game) {
        super(game, new DemoFrameClient());
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        getClient().initialize(this);

        try {
            boulderTexture = Texture.create(
                    getClass().getClassLoader().getResource("boulder.png"),
                    this);
        } catch (IOException e) {
            throw new GameLibrary2DRuntimeException("Failed to initialize frame", e);
        }
    }

    private Renderable createGameArea(Rectangle bounds, float posX, float posY) {
        var quad = Quad.create(bounds, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(RenderSettings.COLOR_R, 1f, 1f, 1f);
        var obj = new BasicObject<>(renderer);
        obj.getPosition().set(posX, posY);
        return obj;
    }

    void applySettings(GameSettings gameSettings) {
        var windowWidth = getGame().getWindow().width();
        var windowHeight = getGame().getWindow().height();
        var gameBounds = gameSettings.getGameBounds();
        var scale = Math.min(windowWidth / gameBounds.width(), windowHeight / gameBounds.height());
        var gameArea = Rectangle.centered(gameBounds.width() * scale, gameBounds.height() * scale);

        add(createGameArea(
                gameArea,
                windowWidth / 2f,
                windowHeight / 2f));

        add(objectLayer);

        objectLayer.getScale().set(scale, scale);
        objectLayer.getPosition().set(windowWidth / 2f + gameArea.xMin(), windowHeight / 2f + gameArea.yMin());

        var boulderBounds = gameSettings.getBoulderBounds();
        var boulderQuad = Quad.create(boulderBounds.resize(1.25f), this);
        boulderRenderer = new SurfaceRenderer(boulderQuad, boulderTexture);
    }

    void addBoulder(ClientBoulder boulder) {
        boulder.setRenderer(boulderRenderer);
        objectLayer.add(boulder);
    }
}