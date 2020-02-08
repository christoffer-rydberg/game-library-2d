package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.common.io.Write;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * This class is used to save or load particle settings to disk, where the path
 * is specified using {@link java.io.File java.io.File} or {@link java.net.URL
 * java.net.URL}. This class can also be used to clone particle settings, which
 * is done by serializing the object to the
 * {@link com.gamelibrary2d.common.io.DataBuffer DataBuffer} and then
 * deserializing a new instance. The buffer is owned by this class and should
 * not be kept/altered after the constructor call. This class is threadsafe.
 */
public class ParticleSettingsSaveLoadManager {

    private final DataBuffer ioBuffer = new DynamicByteBuffer();

    public final synchronized void save(ParticleSpawnSettings spawnSettings, ParticleUpdateSettings updateSettings,
                                        File file, boolean overwrite) throws IOException {
        ioBuffer.clear();
        spawnSettings.serialize(ioBuffer);
        updateSettings.serialize(ioBuffer);
        ioBuffer.flip();
        Write.bytes(ioBuffer, file, overwrite);
    }

    public final synchronized ParticleSettings load(File file) throws IOException {
        ioBuffer.clear();
        Read.bytes(new FileInputStream(file), true, ioBuffer);
        ioBuffer.flip();
        var spawnSettings = load(ioBuffer);
        var updateSettings = new ParticleUpdateSettings(ioBuffer);
        return new ParticleSettings(spawnSettings, updateSettings);
    }

    public final synchronized ParticleSettings load(URL url) throws IOException {
        ioBuffer.clear();
        Read.bytes(url.openStream(), true, ioBuffer);
        ioBuffer.flip();
        var spawnSettings = load(ioBuffer);
        var updateSettings = new ParticleUpdateSettings(ioBuffer);
        return new ParticleSettings(spawnSettings, updateSettings);
    }

    public final synchronized ParticleSpawnSettings clone(ParticleSpawnSettings spawnSettings) {
        ioBuffer.clear();
        spawnSettings.serialize(ioBuffer);
        ioBuffer.flip();
        return load(ioBuffer);
    }

    public final synchronized ParticleUpdateSettings clone(ParticleUpdateSettings updateSettings) {
        ioBuffer.clear();
        updateSettings.serialize(ioBuffer);
        ioBuffer.flip();
        return new ParticleUpdateSettings(ioBuffer);
    }

    private synchronized ParticleSpawnSettings load(DataBuffer buffer) {
        int header = buffer.getInt();
        ParticleSpawnSettings spawnSettings = create(header, buffer);
        if (spawnSettings == null)
            buffer.position(buffer.position() - Integer.BYTES);
        return spawnSettings;
    }

    private synchronized ParticleSpawnSettings create(int header, DataBuffer buffer) {

        switch (header) {

            case BasicSpawnSettings.IO_HEADER:
                return new BasicSpawnSettings(buffer);

            case EllipsoidSpawnSettings.IO_HEADER:
                return new EllipsoidSpawnSettings(buffer);

            default:
                return createSpawnSettings(header, buffer);
        }
    }

    /**
     * Override this method to enable reading and cloning of settings not included
     * in GameLibrary2D, by giving them unique headers when serialized.
     *
     * @param header The IO header of the settings, which has been read from the
     *               beginning of the buffer.
     * @param buffer The buffer containing the serialized settings.
     * @return The settings created from the buffer.
     */
    protected synchronized ParticleSpawnSettings createSpawnSettings(int header, DataBuffer buffer) {
        return null;
    }
}