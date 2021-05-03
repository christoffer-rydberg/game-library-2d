package com.example.sound.android;

import android.media.MediaDataSource;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class InternalStreamDataSource extends MediaDataSource {
    private final BufferedInputStream stream;
    private final DataBuffer dataBuffer;

    public InternalStreamDataSource(InputStream stream) {
        this.stream = new BufferedInputStream(stream);
        this.dataBuffer = new DynamicByteBuffer();
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if (size == 0) {
            return 0;
        }

        int streamPosition = dataBuffer.position();

        int end = (int) position + size;
        if (streamPosition < end) {
            int read = Read.bytes(stream, dataBuffer, end - streamPosition);
            if (read == -1) {
                return -1;
            }
            streamPosition += read;
        }

        dataBuffer.flip();
        dataBuffer.position((int) position);

        int length = Math.min(dataBuffer.remaining(), size);
        dataBuffer.get(buffer, offset, length);

        dataBuffer.clear();
        dataBuffer.position(streamPosition);

        return length;
    }

    @Override
    public long getSize() {
        return -1L;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
