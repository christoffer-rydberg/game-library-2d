package com.gamelibrary2d.common.io;

import com.gamelibrary2d.common.functional.Func;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class SaveLoadManager {

    private final DataBuffer ioBuffer = new DynamicByteBuffer();

    public void save(Serializable target, File file, boolean overwrite) throws IOException {
        ioBuffer.clear();
        target.serialize(ioBuffer);
        ioBuffer.flip();
        Write.bytes(ioBuffer, file, overwrite);
    }

    public <T> T load(File file, Func<DataBuffer, T> factory) throws IOException {
        try (var stream = new FileInputStream(file)) {
            ioBuffer.clear();
            Read.bytes(stream, ioBuffer);
            ioBuffer.flip();
            return factory.invoke(ioBuffer);
        }
    }

    public <T> T load(URL url, Func<DataBuffer, T> factory) throws IOException {
        try (var stream = url.openStream()) {
            ioBuffer.clear();
            Read.bytes(stream, ioBuffer);
            ioBuffer.flip();
            return factory.invoke(ioBuffer);
        }
    }

    public <T extends Serializable> T clone(T target, Func<DataBuffer, T> factory) {
        ioBuffer.clear();
        target.serialize(ioBuffer);
        ioBuffer.flip();
        return factory.invoke(ioBuffer);
    }
}
