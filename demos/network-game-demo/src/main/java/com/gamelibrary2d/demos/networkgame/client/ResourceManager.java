package com.gamelibrary2d.demos.networkgame.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ResourceManager {
    InputStream open(String resource) throws IOException;

    List<String> list(String path) throws IOException;

    default <T> T load(String resource, ResourceLoader<T> loader) throws IOException {
        try (InputStream stream = this.open(resource)) {
            return loader.load(stream);
        }
    }

    interface ResourceLoader<T> {
        T load(InputStream stream) throws IOException;
    }
}
