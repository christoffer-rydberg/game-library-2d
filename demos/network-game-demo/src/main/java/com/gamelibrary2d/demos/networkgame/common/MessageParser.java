package com.gamelibrary2d.demos.networkgame.common;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;

public class MessageParser {
    public static Rectangle readRectangle(DataBuffer buffer) {
        return new Rectangle(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    public static void writeRectangle(Rectangle rectangle, DataBuffer buffer) {
        buffer.putFloat(rectangle.xMin());
        buffer.putFloat(rectangle.yMin());
        buffer.putFloat(rectangle.xMax());
        buffer.putFloat(rectangle.yMax());
    }
}