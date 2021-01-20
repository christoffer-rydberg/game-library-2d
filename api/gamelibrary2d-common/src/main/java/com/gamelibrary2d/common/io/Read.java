package com.gamelibrary2d.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Read {

    public static void bytes(InputStream is, DataBuffer buffer) throws IOException {
        while (true) {
            int readBytes = is.read(buffer.array(), buffer.position(), buffer.remaining());
            if (readBytes == -1)
                break;
            buffer.position(buffer.position() + readBytes);
            buffer.ensureRemaining(1);
        }
    }

    public static DataBuffer bytes(InputStream is) throws IOException {
        var buffer = new DynamicByteBuffer(512);
        bytes(is, buffer);
        return buffer;
    }

    public static void bytesWithSizeHeader(InputStream is, DataBuffer buffer) throws IOException {
        buffer.ensureRemaining(4);
        is.read(buffer.array(), buffer.position(), buffer.remaining());
        var length = buffer.getInt();
        buffer.position(0);
        buffer.ensureRemaining(length);
        buffer.position(is.read(buffer.array(), buffer.position(), buffer.remaining()));
    }

    public static DataBuffer bytesWithSizeHeader(InputStream is) throws IOException {
        var buffer = new DynamicByteBuffer(Integer.BYTES);
        bytesWithSizeHeader(is, buffer);
        return buffer;
    }

    public static String text(URL url, Charset charset) throws IOException {
        try (var stream = url.openStream()) {
            return text(stream, charset);
        }
    }

    public static String text(File file, Charset charset) throws IOException {
        try (var stream = new FileInputStream(file)) {
            return text(stream, charset);
        }
    }

    public static String text(InputStream is, Charset charset) throws IOException {
        DataBuffer buffer = new DynamicByteBuffer(512);
        bytes(is, buffer);
        buffer.flip();
        int length = buffer.remaining();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        return new String(bytes, charset);
    }

    public static String textWithSizeHeader(DataBuffer dataBuffer, Charset charset) {
        int length = dataBuffer.getInt();
        byte[] bytes = new byte[length];
        dataBuffer.get(bytes, 0, length);
        return new String(bytes, charset);
    }

    public static String textWithSizeHeader(DataBuffer dataBuffer) {
        return textWithSizeHeader(dataBuffer, StandardCharsets.UTF_8);
    }
}