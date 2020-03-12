package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.layers.LayerObject;
import com.gamelibrary2d.network.AbstractNetworkFrame;
import com.gamelibrary2d.network.FrameClient;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;

public class DemoFrame extends AbstractNetworkFrame {

    private Renderer boulderRenderer;
    private Texture boulderTexture;
    private LayerObject<Renderable> worldLayer = new DynamicLayer<>();

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected FrameClient initializeNetworkFrame(FrameInitializer initializer) {
        try {
            boulderTexture = Texture.create(
                    getClass().getClassLoader().getResource("boulder.png"),
                    this);

            return new DemoFrameClient(this);
        } catch (IOException e) {
            throw new GameLibrary2DRuntimeException("Failed to initialize frame", e);
        }
    }

    private Renderable createWorldArea(Rectangle bounds, float posX, float posY) {
        var quad = Quad.create(bounds, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(RenderSettings.COLOR_R, 1f, 1f, 1f);
        var obj = new BasicObject<>(renderer);
        obj.position().set(posX, posY);
        return obj;
    }

    void applySettings(GameSettings gameSettings) {
        var windowWidth = game().window().width();
        var windowHeight = game().window().height();
        var gameBounds = gameSettings.getGameBounds();
        var scale = Math.min(windowWidth / gameBounds.width(), windowHeight / gameBounds.height());
        var worldArea = Rectangle.centered(gameBounds.width() * scale, gameBounds.height() * scale);

        add(createWorldArea(
                worldArea,
                windowWidth / 2f,
                windowHeight / 2f));

        add(worldLayer);

        worldLayer.getScale().set(scale, scale);
        worldLayer.position().set(windowWidth / 2f + worldArea.xMin(), windowHeight / 2f + worldArea.yMin());

        var boulderBounds = gameSettings.getBoulderBounds();
        var boulderQuad = Quad.create(boulderBounds.resize(1.25f), this);
        boulderRenderer = new SurfaceRenderer(boulderQuad, boulderTexture);
    }

    void addBoulder(ClientBoulder boulder) {
        boulder.setRenderer(boulderRenderer);
        this.registerObject(boulder);
        worldLayer.add(boulder);
    }
}