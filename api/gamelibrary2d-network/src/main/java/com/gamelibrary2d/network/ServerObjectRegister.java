package com.gamelibrary2d.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerObjectRegister<T extends ServerObject> {
    private final Map<Integer, T> dictionary = new HashMap<>();

    private int lastId = -1;

    public ServerObject get(int key) {
        return dictionary.get(key);
    }

    public Collection<T> getAll() {
        return dictionary.values();
    }

    public void register(T obj) {
        int key = ++lastId;
        var previous = dictionary.put(key, obj);
        if (previous != null) {
            dictionary.put(key, previous);
            throw new IllegalStateException("Id is already in use");
        }

        obj.onRegistered(key);
    }

    public void register(int id, T obj) {
        if (id < 0 || id > lastId)
            throw new IllegalStateException("Object has an invalid identifier: " + id);

        if (dictionary.containsKey(id))
            throw new IllegalStateException("An object with the specified identifier has already been addded: " + id);

        dictionary.put(id, obj);

        obj.onRegistered(id);
    }

    public void remove(int key) {
        dictionary.remove(key);
    }

    public void clear() {
        lastId = -1;
        dictionary.clear();
    }
}
