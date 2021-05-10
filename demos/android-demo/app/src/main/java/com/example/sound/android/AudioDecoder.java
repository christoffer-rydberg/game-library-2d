package com.example.sound.android;

import android.media.MediaDataSource;

import java.io.IOException;

public interface AudioDecoder {
    void decode(MediaDataSource source, AudioSink sink) throws IOException, InterruptedException;
}
