package com.example.sound.android;

import android.media.MediaCodec;
import android.media.MediaDataSource;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DefaultAudioDecoder implements AudioDecoder {
    private final String format;

    public DefaultAudioDecoder(String format) {
        this.format = format;
    }

    private static MediaExtractor createMediaExtractor(MediaDataSource source) throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(source);
        for (int i = 0; i < mediaExtractor.getTrackCount(); ++i) {
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
            if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio")) {
                mediaExtractor.selectTrack(i);
                return mediaExtractor;
            }
        }

        throw new IOException("Audio track is missing from source");
    }

    private void decode(MediaExtractor extractor, Output output) throws IOException {
        MediaFormat mediaFormat = extractor.getTrackFormat(extractor.getSampleTrackIndex());
        mediaFormat.setString(MediaFormat.KEY_MIME, format);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 22050);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, 2);

        MediaCodec audioCodec = MediaCodec.createDecoderByType(format);
        audioCodec.configure(mediaFormat, null, null, 0);
        audioCodec.start();

        boolean finished = false;
        while (!finished) {
            int inputIndex = audioCodec.dequeueInputBuffer(10000);
            if (inputIndex >= 0) {
                ByteBuffer byteBuffer = audioCodec.getInputBuffer(inputIndex);
                int sampleSize = extractor.readSampleData(byteBuffer, 0);
                if (sampleSize <= 0) {
                    audioCodec.queueInputBuffer(
                            inputIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    audioCodec.queueInputBuffer(
                            inputIndex,
                            0,
                            sampleSize,
                            extractor.getSampleTime(),
                            0
                    );

                    extractor.advance();
                }
            }

            MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
            int outPutIndex = audioCodec.dequeueOutputBuffer(outputInfo, 10000);
            while (outPutIndex >= 0) {
                output.write(audioCodec.getOutputBuffer(outPutIndex));
                audioCodec.releaseOutputBuffer(outPutIndex, false);
                outPutIndex = audioCodec.dequeueOutputBuffer(outputInfo, 10000);
                if ((outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    output.finished();
                    extractor.release();
                    audioCodec.stop();
                    audioCodec.release();
                    finished = true;
                }
            }
        }
    }

    @Override
    public void decode(MediaDataSource source, Output output) throws IOException {
        decode(createMediaExtractor(source), output);
    }
}
