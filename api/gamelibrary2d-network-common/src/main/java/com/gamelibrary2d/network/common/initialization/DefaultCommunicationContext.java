package com.gamelibrary2d.network.common.initialization;

import java.util.HashMap;
import java.util.Map;

public class DefaultCommunicationContext implements CommunicationContext {

    private final Map<Object, Object> register = new HashMap<>();

    public void register(Object key, Object obj) {
        register.put(key, obj);
    }

    public <T> T get(Class<T> type, Object key) {
        var obj = register.get(key);
        if (obj != null && type.isAssignableFrom(obj.getClass())) {
            return type.cast(obj);
        }

        return null;
    }
}
