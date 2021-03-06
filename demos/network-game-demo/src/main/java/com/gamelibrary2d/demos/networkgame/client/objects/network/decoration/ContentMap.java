package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.objects.network.ClientObject;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;

import java.util.HashMap;
import java.util.Map;

public class ContentMap {
    private final Map<Byte, Map<Byte, Renderable>> renderers = new HashMap<>();

    private void initializeRenderers(byte primaryType, TextureMap textures, Rectangle bounds, Disposer disposer) {
        Map<Byte, Renderable> renderers = new HashMap<>();
        Surface surface = Quad.create(bounds, disposer);
        for (Byte key : textures.getKeys(primaryType)) {
            Renderer renderer = new SurfaceRenderer<>(surface, textures.getTexture(primaryType, key));
            renderers.put(key, renderer);
        }

        this.renderers.put(primaryType, renderers);
    }

    private void initializePlayerRenderers(GameSettings settings, TextureMap textures, Disposer disposer) {
        initializeRenderers(ObjectTypes.PLAYER, textures, settings.getSpaceCraftBounds().resize(1.25f), disposer);
    }

    private void initializeObstacleRenderers(GameSettings settings, TextureMap textures, Disposer disposer) {
        initializeRenderers(ObjectTypes.OBSTACLE, textures, settings.getObstacleBounds().resize(1.25f), disposer);
    }

    public void initialize(GameSettings settings, TextureMap textures, Disposer disposer) {
        initializePlayerRenderers(settings, textures, disposer);
        initializeObstacleRenderers(settings, textures, disposer);
    }

    public Renderable get(ClientObject obj) {
        return get(obj.getPrimaryType(), obj.getSecondaryType());
    }

    public Renderable get(Byte primaryType, Byte secondaryType) {
        Map<Byte, Renderable> renderers = this.renderers.get(primaryType);
        return renderers != null ? renderers.get(secondaryType) : null;
    }
}
