package com.gamelibrary2d.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class Read {

    public static void bytes(InputStream is, boolean closeStream, DataBuffer buffer) throws IOException {
        try {
            while (true) {
                int readBytes = is.read(buffer.array(), buffer.position(), buffer.remaining());
                if (readBytes == -1)
                    break;
                buffer.position(buffer.position() + readBytes);
                buffer.ensureRemaining(1);
            }
        } finally {
            if (closeStream) {
                is.close();
            }
        }
    }

    public static DataBuffer bytes(InputStream is, boolean closeStream) throws IOException {
        var buffer = new DynamicByteBuffer(512);
        bytes(is, closeStream, buffer);
        return buffer;
    }

    public static void bytesWithSizeHeader(InputStream is, boolean closeStream, DataBuffer buffer) throws IOException {
        try {
            buffer.ensureRemaining(4);
            is.read(buffer.array(), buffer.position(), buffer.remaining());
            var length = buffer.getInt();
            buffer.position(0);
            buffer.ensureRemaining(length);
            buffer.position(is.read(buffer.array(), buffer.position(), buffer.remaining()));
        } finally {
            if (closeStream) {
                is.close();
            }
        }
    }

    public static DataBuffer bytesWithSizeHeader(InputStream is, boolean closeStream) throws IOException {
        var buffer = new DynamicByteBuffer(Integer.BYTES);
        bytesWithSizeHeader(is, closeStream, buffer);
        return buffer;
    }

    public static String text(URL url, Charset charset) throws IOException {
        return text(url.openStream(), charset, true);
    }

    public static String text(File file, Charset charset) throws IOException {
        return text(new FileInputStream(file), charset, true);
    }

    public static String text(InputStream is, Charset charset, boolean closeStream) throws IOException {
        DataBuffer buffer = new DynamicByteBuffer(512);
        bytes(is, closeStream, buffer);
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
}