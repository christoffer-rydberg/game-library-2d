package com.gamelibrary2d.common.io;

import java.nio.ByteBuffer;

public class BitParser {

    private ByteBuffer byteBuffer;

    private int bytePosition;

    private int bitIndex;

    public BitParser() {
    }

    public BitParser(ByteBuffer byteBuffer) {
        setByteBuffer(byteBuffer);
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        this.position(byteBuffer.position() * 8);
    }

    public void putInt(int i) {
        putInt(i, 32);
    }

    public void putInt(int i, int bits) {

        if (bits == 0) {
            return;
        }
        bits = Math.min(bits, 32);

        if (bits > 24) {
            put((byte) (i >>> 24), bits - 24);
            put((byte) (i >>> 16));
            put((byte) (i >>> 8));
            put((byte) i);
        } else if (bits > 16) {
            put((byte) (i >>> 16), bits - 16);
            put((byte) (i >>> 8));
            put((byte) i);
        } else if (bits > 8) {
            put((byte) (i >>> 8), bits - 8);
            put((byte) i);
        } else if (bits > 0) {
            put((byte) i, bits);
        }
    }

    public void put(byte b) {
        put(b, 8);
    }

    public void putFloat(float f) {
        putInt(Float.floatToIntBits(f), 32);
    }

    public float getFloat() {
        return Float.intBitsToFloat(getInt(32));
    }

    public void put(byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            put(bytes[i], 8);
        }
    }

    public void put(byte b, int bits) {

        if (bits <= 0 || bits > 8)
            throw new IllegalStateException("Invalid bit count: " + bits);

        // Adjust value to correct bit count and shift to current bit position.
        int newBits = ((b << (8 - bits)) & 0xff) >>> bitIndex;

        // Retrieve the bits at the current byte position
        int oldBits = byteBuffer.get(bytePosition);

        // Remaining bits after insertion, will be negative if the added value
        // does not fit in the current byte.
        int bitsRemaining = 8 - bitIndex - bits;

        // Create a bit mask to null the bits where the new bits are inserted.
        int bitMask;
        int leftBitMask = 0xff00 >>> bitIndex;
        if (bitsRemaining <= 0) {
            bitMask = leftBitMask;
        } else {
            int rightBitMask = ~(0xff << bitsRemaining);
            bitMask = leftBitMask | rightBitMask;
        }

        // Update the byte at the current byte position
        byteBuffer.put(bytePosition, (byte) (newBits | (oldBits & bitMask)));

        if (bitsRemaining < 0) {
            ++bytePosition;
            bitIndex = 0;
            put(b, -bitsRemaining); // Put remaining bits in next byte.
        } else {
            bitIndex += bits;
        }
    }

    public int getInt() {
        return getInt(32);
    }

    public int getInt(int bits) {

        if (bits <= 0 || bits > 32)
            throw new IllegalStateException("Invalid bit count: " + bits);

        int result;
        if (bits > 24) {
            result = (get(bits - 24) & 0xFF) << 24;
            result |= (get() & 0xFF) << 16;
            result |= (get() & 0xFF) << 8;
            result |= (get() & 0xFF);
        } else if (bits > 16) {
            result = (get(bits - 16) & 0xFF) << 16;
            result |= (get() & 0xFF) << 8;
            result |= (get() & 0xFF);
        } else if (bits > 8) {
            int b1 = get(bits - 8) & 0xFF;
            int b2 = get() & 0xFF;
            result = b1 << 8;
            result |= b2;
        } else {
            result = (get(bits) & 0xFF);
        }

        return result;
    }

    public byte get() {
        return get(8);
    }

    public byte get(int bits) {

        if (bits <= 0 || bits > 8)
            throw new IllegalStateException("Invalid bit count: " + bits);

        int remainingBits = (8 - bitIndex);

        int leftBits = ((byteBuffer.get(bytePosition) << bitIndex) & 0xFF) >>> (8 - bits);

        if (bits > remainingBits) {
            bits -= remainingBits;
            ++bytePosition;
            bitIndex = bits;
            int rightBits = (byteBuffer.get(bytePosition) & 0xff) >>> (8 - bits);
            return (byte) (leftBits | rightBits);
        } else {
            bitIndex += bits;
            return (byte) leftBits;
        }
    }

    /**
     * @return The current bit position.
     */
    public long position() {
        return bytePosition * 8 + bitIndex;
    }

    /**
     * Sets the current bit position. This will also update the
     * {@link #bytePosition() bytePosition} and the {@link #bitIndex() bitIndex} .
     *
     * @param position The new bit position.
     */
    public void position(long position) {
        bytePosition = (int) (position / 8);
        bitIndex = (int) (position % 8);
    }

    /**
     * @return The position of the current byte in the byte array.
     */
    public int bytePosition() {
        return bytePosition;
    }

    /**
     * @return The bit index of the current byte.
     */
    public int bitIndex() {
        return bitIndex;
    }
}