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
        gameBounds = MessageParser.readRectangle(buffer);
        boulderBounds = MessageParser.readRectangle(buffer);
        spaceCraftBounds = MessageParser.readRectangle(buffer);
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
        MessageParser.writeRectangle(gameBounds, buffer);
        MessageParser.writeRectangle(boulderBounds, buffer);
        MessageParser.writeRectangle(spaceCraftBounds, buffer);
    }
}

