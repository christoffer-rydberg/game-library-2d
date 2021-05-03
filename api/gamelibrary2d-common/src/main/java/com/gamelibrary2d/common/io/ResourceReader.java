package com.gamelibrary2d.common.io;

import com.gamelibrary2d.common.functional.Func;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceReader {
    private final DataBuffer buffer;

    public ResourceReader() {
        this.buffer = new DynamicByteBuffer();
    }

    public ResourceReader(DataBuffer buffer) {
        this.buffer = buffer;
    }

    public <T> T read(InputStream stream, Func<DataBuffer, T> factory) throws IOException {
        buffer.clear();
        Read.bytes(stream, buffer);
        buffer.flip();
        return factory.invoke(buffer);
    }

    public <T> T read(File file, Func<DataBuffer, T> factory) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            return read(stream, factory);
        }
    }

    public <T> T read(URL url, Func<DataBuffer, T> factory) throws IOException {
        try (InputStream stream = url.openStream()) {
            return read(stream, factory);
        }
    }
}
