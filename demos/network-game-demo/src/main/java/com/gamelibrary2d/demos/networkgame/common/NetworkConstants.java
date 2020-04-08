package com.gamelibrary2d.demos.networkgame.common;

public class NetworkConstants {
    private static final int MAX_OBJECTS = 1000;
    private static final int BITS_PER_BYTE = 8;
    private static final int HEADER_BYTE_SIZE = Integer.BYTES;

    public static final int HEADER_BIT_SIZE = HEADER_BYTE_SIZE * BITS_PER_BYTE;
    public static final int OBJECT_ID_BIT_SIZE = 10;
    public static final int POS_X_BIT_SIZE = 10;
    public static final int POS_Y_BIT_SIZE = 10;
    public static final int ROTATION_BIT_SIZE = 9;

    private static final int OBJECT_BIT_SIZE = OBJECT_ID_BIT_SIZE + POS_X_BIT_SIZE + POS_Y_BIT_SIZE;
    private static final int OBJECTS_BIT_SIZE = OBJECT_BIT_SIZE * MAX_OBJECTS;
    private static final int OBJECTS_BYTE_SIZE = (int) Math.ceil((double) OBJECTS_BIT_SIZE / BITS_PER_BYTE);

    public static final int UPDATE_BUFFER_BYTE_SIZE = HEADER_BYTE_SIZE + OBJECTS_BYTE_SIZE;
}
