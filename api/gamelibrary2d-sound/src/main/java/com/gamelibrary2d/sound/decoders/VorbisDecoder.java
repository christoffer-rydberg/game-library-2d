package com.gamelibrary2d.sound.decoders;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundBuffer;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * {@link AudioDecoder} for Vorbis OGG audio data.
 */
public class VorbisDecoder extends AbstractAudioDecoder {

    /**
     * Opens the audio data in vorbis and returns a handle.
     */
    private static long openVorbisMemory(ByteBuffer audioData) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            long handle = stb_vorbis_open_memory(audioData, error, null);
            if (handle == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }
            return handle;
        }
    }

    public SoundBuffer decode(ByteBuffer audioData, Disposer disposer) {

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {

            // Create a vorbis memory handle
            long vorbisHandle = openVorbisMemory(audioData);

            // Load vorbis info
            stb_vorbis_get_info(vorbisHandle, info);

            // Decode and create OpenAl buffer
            SoundBuffer soundBuffer = decodeAndCreateOpenAlBuffer(vorbisHandle, info);

            disposer.registerDisposal(soundBuffer);

            return soundBuffer;
        }
    }

    /**
     * Decodes to Pulse Coded Modulation (PCM) format and creates an OpenAL buffer.
     *
     * @param vorbisHandle The Vorbis memory handle.
     * @param info         The Vorbis info.
     * @return Handle for the created OpenAl buffer.
     */
    private SoundBuffer decodeAndCreateOpenAlBuffer(long vorbisHandle, STBVorbisInfo info) {

        // Create and populate PCM buffer.
        ShortBuffer pcm = MemoryUtil.memAllocShort(stb_vorbis_stream_length_in_samples(vorbisHandle) * info.channels());
        int channels = info.channels();
        pcm.limit(stb_vorbis_get_samples_short_interleaved(vorbisHandle, channels, pcm) * channels);

        // Create and populate OpenAl buffer.
        int openAlHandle = alGenBuffers();
        alBufferData(openAlHandle, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm,
                info.sample_rate());

        // Clean up
        MemoryUtil.memFree(pcm);
        stb_vorbis_close(vorbisHandle);

        return new SoundBuffer(openAlHandle);
    }
}