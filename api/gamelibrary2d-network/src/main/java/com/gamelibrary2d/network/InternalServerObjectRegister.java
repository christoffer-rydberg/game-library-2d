package com.gamelibrary2d.network;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class InternalServerObjectRegister {

    private final Map<Integer, ServerObject> dictionary = new HashMap<>();

    private int lastId = -1;

    public ServerObject get(int key) {
        return dictionary.get(key);
    }

    Collection<ServerObject> getValues() {
        return dictionary.values();
    }

    public int add(ServerObject obj) {
        int key = ++lastId;
        var previous = dictionary.put(key, obj);
        if (previous != null) {
            dictionary.put(key, previous);
            throw new GameLibrary2DRuntimeException("Failed to resolve a unique key for object!");
        }
        return key;
    }

    public void remove(int key) {
        dictionary.remove(key);
    }

    public void clear() {
        lastId = -1;
        dictionary.clear();
    }

    public void add(int id, ServerObject obj) {

        if (id < 0 || id > lastId)
            throw new IllegalStateException("Object has an invalid identifier: " + id);

        if (dictionary.containsKey(id))
            throw new IllegalStateException("An object with the specified identifier has already been addded: " + id);

        dictionary.put(id, obj);
    }
}