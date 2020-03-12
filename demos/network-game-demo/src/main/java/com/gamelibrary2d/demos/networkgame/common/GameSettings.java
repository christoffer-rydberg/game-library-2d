package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Message;

public class GameSettings implements Message {
    private final Rectangle gameBounds;
    private final Rectangle boulderBounds;

    public GameSettings(Rectangle gameBounds, Rectangle boulderBounds) {
        this.gameBounds = gameBounds;
        this.boulderBounds = boulderBounds;
    }

    public GameSettings(DataBuffer buffer) {
        gameBounds = MessageParser.readRectangle(buffer);
        boulderBounds = MessageParser.readRectangle(buffer);
        ;
    }

    public Rectangle getGameBounds() {
        return gameBounds;
    }

    public Rectangle getBoulderBounds() {
        return boulderBounds;
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        MessageParser.writeRectangle(gameBounds, buffer);
        MessageParser.writeRectangle(boulderBounds, buffer);
    }
}

