package com.gamelibrary2d.demos.networkgame.common;

public class NetworkConstants {
    private static final int BITS_PER_BYTE = 8;

    public static final int BIT_COUNT_OBJECT_ID = 10;
    public static final int BIT_COUNT_POS_X = 11;
    public static final int BIT_COUNT_POS_Y = 11;
    public static final int BIT_SIZE_HEADER = Integer.BYTES * BITS_PER_BYTE;
}
