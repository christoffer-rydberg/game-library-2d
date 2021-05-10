package com.example.sound.android;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

interface AudioSink {
    void begin(MediaFormat mediaFormat);

    void write(ByteBuffer audioData) throws InterruptedException;

    void end();
}
