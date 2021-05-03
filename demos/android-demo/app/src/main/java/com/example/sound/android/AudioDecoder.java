package com.example.sound.android;

import android.media.MediaDataSource;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface AudioDecoder {
    void decode(MediaDataSource source, Output output) throws IOException;

    interface Output {
        void initialize(MediaFormat mediaFormat);

        void write(ByteBuffer buffer);

        void finished();
    }
}
