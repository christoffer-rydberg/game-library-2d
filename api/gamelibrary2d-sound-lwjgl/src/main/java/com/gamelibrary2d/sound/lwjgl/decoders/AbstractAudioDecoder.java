package com.gamelibrary2d.sound.lwjgl.decoders;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.sound.lwjgl.DefaultSoundBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class AbstractAudioDecoder implements AudioDecoder {

    /**
     * Loads the sound stream into a direct byte buffer.
     */
    private static ByteBuffer load(InputStream stream) throws IOException {
        DataBuffer dynamicBuffer = new DynamicByteBuffer();
        Read.bytes(stream, dynamicBuffer);
        dynamicBuffer.flip();

        ByteBuffer buffer = BufferUtils.createByteBuffer(dynamicBuffer.remaining());
        buffer.put(dynamicBuffer.internalByteBuffer());
        buffer.flip();

        return buffer;
    }

    @Override
    public DefaultSoundBuffer decode(InputStream stream, Disposer disposer) throws IOException {
        return decode(load(stream), disposer);
    }
}