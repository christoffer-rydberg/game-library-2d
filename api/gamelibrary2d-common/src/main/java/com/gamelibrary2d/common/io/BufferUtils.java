package com.gamelibrary2d.common.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils {
    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createFloatBuffer(int capacity) {
        return createByteBuffer(Float.BYTES * capacity).asFloatBuffer();
    }

    public static IntBuffer createIntBuffer(int capacity) {
        return createByteBuffer(Integer.BYTES * capacity).asIntBuffer();
    }
}
