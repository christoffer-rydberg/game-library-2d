package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;

public class SerializationUtil {
    public static Rectangle deserializeRectangle(DataBuffer buffer) {
        return new Rectangle(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    public static void serializeRectangle(Rectangle rectangle, DataBuffer buffer) {
        buffer.putFloat(rectangle.xMin());
        buffer.putFloat(rectangle.yMin());
        buffer.putFloat(rectangle.xMax());
        buffer.putFloat(rectangle.yMax());
    }
}