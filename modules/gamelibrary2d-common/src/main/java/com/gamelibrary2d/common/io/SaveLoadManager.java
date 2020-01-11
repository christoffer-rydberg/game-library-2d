package com.gamelibrary2d.common.io;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * This class is used to save or load object instances to disk, where the path
 * is specified using {@link java.io.File java.io.File} or {@link java.net.URL
 * java.net.URL}. An object instance can be saved (serialized) if it implements
 * the Message interface. In order to load (deserialize) an instance, the class
 * must have a public constructor accepting a
 * {@link com.gamelibrary2d.common.io.DataBuffer DataBuffer} as a single
 * parameter. The buffer is owned by this class and should not be kept/altered
 * after the constructor call. This class can also be used to clone objects,
 * which is done by serializing the object to the
 * {@link com.gamelibrary2d.common.io.DataBuffer DataBuffer} and then
 * deserializing a new instance. This class is threadsafe.
 *
 * @author Christoffer Rydberg
 */
public class SaveLoadManager {

    private final DataBuffer dataBuffer;

    public SaveLoadManager() {
        dataBuffer = new DynamicByteBuffer();
    }

    private static <T> T deserialize(Class<T> clazz, DataBuffer dataBuffer) {
        try {
            return clazz.getDeclaredConstructor(DataBuffer.class).newInstance(dataBuffer);
        } catch (Exception e) {
            throw new GameLibrary2DRuntimeException("Failed to instantiate class '" + clazz.getName()
                    + "'. Ensure that the class has a public constructor accepting a DataBuffer as a single parameter.");
        }
    }

    public synchronized void save(Serializable obj, File file, boolean overwrite) throws IOException {
        file.getParentFile().mkdirs();
        dataBuffer.clear();
        obj.serialize(dataBuffer);
        dataBuffer.flip();
        Write.bytes(dataBuffer, file, overwrite);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Serializable> T clone(T obj) {
        dataBuffer.clear();
        obj.serialize(dataBuffer);
        dataBuffer.flip();
        return deserialize((Class<T>) obj.getClass(), dataBuffer);
    }

    public synchronized <T> T load(Class<T> clazz, File file) throws IOException {
        dataBuffer.clear();
        Read.bytes(new FileInputStream(file), true, dataBuffer);
        dataBuffer.flip();
        return deserialize(clazz, dataBuffer);
    }

    public synchronized <T> T load(Class<T> clazz, URL url) throws IOException {
        dataBuffer.clear();
        Read.bytes(url.openStream(), true, dataBuffer);
        dataBuffer.flip();
        return deserialize(clazz, dataBuffer);
    }
}