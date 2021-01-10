package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;

public class SerializationUtil {
    public static Rectangle deserializeRectangle(DataBuffer buffer) {
        return new Rectangle(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    public static void serializeRectangle(Rectangle rectangle, DataBuffer buffer) {
        buffer.putFloat(rectangle.getLowerX());
        buffer.putFloat(rectangle.getLowerY());
        buffer.putFloat(rectangle.getUpperX());
        buffer.putFloat(rectangle.getUpperY());
    }
}