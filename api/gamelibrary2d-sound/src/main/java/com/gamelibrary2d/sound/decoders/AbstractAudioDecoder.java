package com.gamelibrary2d.sound.decoders;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.sound.SoundBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public abstract class AbstractAudioDecoder implements AudioDecoder {

    /**
     * Loads the specified sound source into a direct byte buffer.
     *
     * @param src The URL of the sound source.
     * @return The loaded direct byte buffer.
     * @throws IOException If an I/O error occurs.
     */
    private static ByteBuffer load(URL src) throws IOException {
        try (InputStream stream = src.openStream()) {
            DataBuffer byteBuffer = new DynamicByteBuffer();
            Read.bytes(stream, byteBuffer);
            byteBuffer.flip();

            ByteBuffer directByteBuffer = BufferUtils.createByteBuffer(byteBuffer.limit());
            directByteBuffer.put(byteBuffer.internalByteBuffer());
            directByteBuffer.flip();
            return directByteBuffer;
        }
    }

    @Override
    public SoundBuffer decode(URL audioUrl, Disposer disposer) throws IOException {
        return decode(load(audioUrl), disposer);
    }
}