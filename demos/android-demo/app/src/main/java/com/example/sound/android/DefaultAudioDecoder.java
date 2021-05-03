package com.example.sound.android;

import android.media.MediaCodec;
import android.media.MediaDataSource;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DefaultAudioDecoder implements AudioDecoder {

    private static MediaExtractor createMediaExtractor(MediaDataSource source) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(source);
        return extractor;
    }

    private static MediaCodec initializeAndCreateDecoder(MediaExtractor extractor, Output output) throws IOException {
        for (int i = 0; i < extractor.getTrackCount(); ++i) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                extractor.selectTrack(i);
                output.initialize(mediaFormat);
                MediaCodec decoder = MediaCodec.createDecoderByType(mime);
                decoder.configure(mediaFormat, null, null, 0);
                return decoder;
            }
        }

        throw new IOException("No audio track found");
    }

    @Override
    public void decode(MediaDataSource source, Output output) throws IOException {
        MediaExtractor extractor = createMediaExtractor(source);

        MediaCodec decoder = initializeAndCreateDecoder(extractor, output);
        decoder.start();

        long timeoutUs = 10000;
        boolean finished = false;
        while (!finished) {
            int inputIndex = decoder.dequeueInputBuffer(timeoutUs);

            if (inputIndex >= 0) {
                ByteBuffer inputBuffer = decoder.getInputBuffer(inputIndex);
                int sampleSize = extractor.readSampleData(inputBuffer, 0);

                if (sampleSize > 0) {
                    decoder.queueInputBuffer(
                            inputIndex,
                            0,
                            sampleSize,
                            extractor.getSampleTime(),
                            0
                    );

                    extractor.advance();
                } else {
                    decoder.queueInputBuffer(
                            inputIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
            }

            MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
            int outPutIndex = decoder.dequeueOutputBuffer(outputInfo, timeoutUs);
            if (outPutIndex >= 0) {
                ByteBuffer outputBuffer = decoder.getOutputBuffer(outPutIndex);
                output.write(outputBuffer);
                decoder.releaseOutputBuffer(outPutIndex, false);
                if ((outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    output.finished();
                    extractor.release();
                    decoder.stop();
                    decoder.release();
                    finished = true;
                }
            }
        }
    }
}
