package com.gamelibrary2d.sound.decoders;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundBuffer;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * An audio decoder is used to decode audio data to Pulse Coded Modulation (PCM)
 * format and buffer the data in OpenAL.
 */
public interface AudioDecoder {

    /**
     * Decodes the audio data of the the specified URL to PCM and returns a
     * {@link SoundBuffer} which represents the created OpenAL buffer.
     *
     * @param audioUrl The URL of the audio data.
     * @param disposer The responsible disposer of the created {@link SoundBuffer}.
     */
    SoundBuffer decode(URL audioUrl, Disposer disposer) throws IOException;

    /**
     * Decodes the specified audio data to PCM and returns a {@link SoundBuffer}
     * which represents the created OpenAL buffer.
     *
     * @param audioUrl The audio data, read into a direct byte buffer.
     * @param disposer The responsible disposer of the created {@link SoundBuffer}.
     */
    SoundBuffer decode(ByteBuffer audioData, Disposer disposer);

}