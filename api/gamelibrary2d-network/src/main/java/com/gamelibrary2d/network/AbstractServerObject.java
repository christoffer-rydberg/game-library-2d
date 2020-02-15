package com.gamelibrary2d.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;

public abstract class AbstractServerObject implements ServerObject {

    private final Point position = new Point();

    private int id = Integer.MAX_VALUE;

    private Rectangle bounds;

    @Override
    public void onRegistered(final int id) {
        this.id = id;
    }

    @Override
    public void onUnregistered() {

    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public Point position() {
        return position;
    }

    public int getId() {
        return id;
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        buffer.putInt(id);
        buffer.putFloat(position.getX());
        buffer.putFloat(position.getY());
    }
}