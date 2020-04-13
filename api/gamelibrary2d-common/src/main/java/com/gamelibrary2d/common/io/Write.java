package com.gamelibrary2d.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

public class Write {

    private static boolean createFileAndDirs(File file) throws IOException {
        File parent = file.getParentFile();
        return parent != null && (parent.mkdirs() || file.createNewFile());
    }

    private static void writeToFile(byte[] array, int off, int len, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(array, off, len);
        }
    }

    public static void bytes(byte[] array, int offset, int len, File file, boolean overwrite) throws IOException {
        if (file.exists()) {
            if (!overwrite) {
                throw new IOException(String.format("File already exists: '%s'", file));
            }

            if (file.canWrite()) {
                Random rand = new Random(System.currentTimeMillis());
                String tempFilePath = file.getAbsolutePath() + rand.nextInt(Integer.MAX_VALUE);
                File temporaryFile = new File(tempFilePath);
                if (createFileAndDirs(temporaryFile)) {
                    // Save buffer to a temporary file
                    writeToFile(array, offset, len, temporaryFile);

                    // Delete the original file
                    if (!file.delete()) {
                        throw new IOException(String.format("Failed to delete file: '%s'.", file));
                    }

                    // Rename temporary file to original
                    if (!temporaryFile.renameTo(file)) {
                        throw new IOException(String.format("Failed to rename file '%s' to '%s'.", tempFilePath, file));
                    }

                } else {
                    throw new IOException(String.format("Failed to create temporary file: '%s'.", tempFilePath));
                }
            } else {
                throw new IOException(String.format("File is readonly: '%s'", file));
            }
        } else if (createFileAndDirs(file)) {
            writeToFile(array, offset, len, file);
        } else {
            throw new IOException(String.format("Failed to create file: '%s'", file));
        }
    }

    public static void bytes(DataBuffer buffer, File file, boolean overwrite) throws IOException {
        bytes(buffer.array(), buffer.position(), buffer.limit(), file, overwrite);
        buffer.position(buffer.limit());
    }

    public static void text(String text, File file, Charset charset, boolean overwrite) throws IOException {
        var bytes = text.getBytes(charset);
        bytes(bytes, 0, bytes.length, file, overwrite);
    }

    public static void textWithSizeHeader(String text, Charset charset, DataBuffer dataBuffer) {
        var bytes = text.getBytes(charset);
        dataBuffer.putInt(bytes.length);
        dataBuffer.put(bytes);
    }
}
