package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;

public class GameSettings implements Message {
    private final Rectangle gameBounds;
    private final Rectangle boulderBounds;
    private final Rectangle spaceCraftBounds;

    public GameSettings(Rectangle gameBounds, Rectangle boulderBounds, Rectangle spaceCraftBounds) {
        this.gameBounds = gameBounds;
        this.boulderBounds = boulderBounds;
        this.spaceCraftBounds = spaceCraftBounds;
    }

    public GameSettings(DataBuffer buffer) {
        gameBounds = SerializationUtil.deserializeRectangle(buffer);
        boulderBounds = SerializationUtil.deserializeRectangle(buffer);
        spaceCraftBounds = SerializationUtil.deserializeRectangle(buffer);
    }

    public Rectangle getGameBounds() {
        return gameBounds;
    }

    public Rectangle getPortalBounds() {
        return boulderBounds;
    }

    public Rectangle getBoulderBounds() {
        return boulderBounds;
    }

    public Rectangle getSpaceCraftBounds() {
        return spaceCraftBounds;
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        SerializationUtil.serializeRectangle(gameBounds, buffer);
        SerializationUtil.serializeRectangle(boulderBounds, buffer);
        SerializationUtil.serializeRectangle(spaceCraftBounds, buffer);
    }
}

