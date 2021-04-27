package com.gamelibrary2d.sound.lwjgl.decoders;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.lwjgl.DefaultSoundBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public interface AudioDecoder {

    DefaultSoundBuffer decode(InputStream stream, Disposer disposer) throws IOException;

    DefaultSoundBuffer decode(ByteBuffer data, Disposer disposer);

    default DefaultSoundBuffer decode(URL url, Disposer disposer) throws IOException {
        try (InputStream stream = url.openStream()) {
            return decode(stream, disposer);
        }
    }
}