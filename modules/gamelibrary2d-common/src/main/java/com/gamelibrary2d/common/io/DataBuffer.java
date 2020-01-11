package com.gamelibrary2d.common.io;

import java.nio.ByteBuffer;

public interface DataBuffer {

    void put(byte b);

    void put(byte[] bytes);

    void put(DataBuffer buffer);

    void put(ByteBuffer buffer);

    void put(byte[] message, int off, int len);

    void putInt(int i);

    void putFloat(float f);

    void putDouble(double d);

    void putLong(long l);

    void putBool(boolean b);

    byte get();

    byte get(int i);

    void get(byte[] bytes, int offset, int length);

    default void get(byte[] bytes) {
        get(bytes, 0, bytes.length);
    }

    int getInt();

    float getFloat();

    double getDouble();

    long getLong();

    boolean getBool();

    int position();

    void position(int i);

    void clear();

    void flip();

    byte[] array();

    ByteBuffer internalByteBuffer();

    void ensureRemaining(int bytes);

    int limit();

    void limit(int limit);

    int capacity();

    int remaining();

    default void putEnum(Enum<?> e) {
        int length = e.getClass().getEnumConstants().length;
        if (length < 256)
            put((byte) e.ordinal());
        else
            putInt(e.ordinal());
    }

    default <T extends Enum<?>> T getEnum(Class<T> enumType) {
        T[] enumConstants = enumType.getEnumConstants();
        int length = enumConstants.length;
        if (length < 256)
            return enumType.getEnumConstants()[get() & 0xFF];
        else
            return enumType.getEnumConstants()[getInt()];
    }

}