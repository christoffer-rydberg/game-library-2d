package com.gamelibrary2d.common.io;

import java.nio.ByteBuffer;

public class DynamicByteBuffer implements DataBuffer {

    private static final int DEFAULT_INITIAL_CAPACITY = 100;

    private ByteBuffer internalByteBuffer;

    public DynamicByteBuffer(int initialCapacity) {
        internalByteBuffer = ByteBuffer.allocate(initialCapacity);
    }

    public DynamicByteBuffer() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ByteBuffer internalByteBuffer() {
        return internalByteBuffer;
    }

    public byte get() {
        return internalByteBuffer.get();
    }

    public int position() {
        return internalByteBuffer.position();
    }

    public int limit() {
        return internalByteBuffer.limit();
    }

    public void limit(int limit) {
        internalByteBuffer.limit(limit);
    }

    public int capacity() {
        return internalByteBuffer.capacity();
    }

    public void position(int i) {
        internalByteBuffer.position(i);
    }

    public byte[] array() {
        return internalByteBuffer.array();
    }

    public void put(byte b) {
        ensureRemaining(1);
        internalByteBuffer.put(b);
    }

    public void put(byte[] src) {
        ensureRemaining(src.length);
        internalByteBuffer.put(src);
    }

    public void put(byte[] src, int offset, int length) {
        ensureRemaining(length);
        internalByteBuffer.put(src, offset, length);
    }

    public void putInt(int value) {
        ensureRemaining(Integer.BYTES);
        internalByteBuffer.putInt(value);
    }

    public void putFloat(float value) {
        ensureRemaining(Float.BYTES);
        internalByteBuffer.putFloat(value);
    }

    public void putDouble(double value) {
        ensureRemaining(Double.BYTES);
        internalByteBuffer.putDouble(value);
    }

    @Override
    public void put(ByteBuffer buffer) {
        ensureRemaining(buffer.remaining());
        internalByteBuffer.put(buffer);
    }

    @Override
    public void put(DataBuffer buffer) {
        ensureRemaining(buffer.remaining());
        internalByteBuffer.put(buffer.internalByteBuffer());
    }

    public byte get(int i) {
        return internalByteBuffer.get(i);
    }

    public void get(byte[] dst, int offset, int length) {
        internalByteBuffer.get(dst, offset, length);
    }

    public int getInt() {
        return internalByteBuffer.getInt();
    }

    public float getFloat() {
        return internalByteBuffer.getFloat();
    }

    public void ensureRemaining(int bytes) {
        final int position = internalByteBuffer.position();
        final int minCapacity = bytes + position;
        final int capacity = capacity();

        if (minCapacity > capacity) {
            int newCapacity = capacity * 2;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            ByteBuffer increasedBuffer = ByteBuffer.allocate(newCapacity);
            internalByteBuffer.flip();
            increasedBuffer.put(internalByteBuffer);
            internalByteBuffer = increasedBuffer;
        }
    }

    public void flip() {
        internalByteBuffer.flip();
    }

    public void clear() {
        internalByteBuffer.clear();
    }

    @Override
    public void putLong(long l) {
        internalByteBuffer.putLong(l);
    }

    @Override
    public double getDouble() {
        return internalByteBuffer.getDouble();
    }

    @Override
    public long getLong() {
        return internalByteBuffer.getLong();
    }

    @Override
    public void putBool(boolean b) {
        put((byte) (b ? 1 : 0));
    }

    @Override
    public boolean getBool() {
        return get() == 1;
    }

    @Override
    public int remaining() {
        return internalByteBuffer.remaining();
    }
}