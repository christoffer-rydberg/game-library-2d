package com.gamelibrary2d.demos.networkgame;

import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.TextureMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DesktopResourceManager implements ResourceManager {

    private static List<String> getResources(Path fileSystemPath, Path resourceFolder) throws IOException {
        int resourcePathLength = 1 + resourceFolder.getNameCount();
        List<String> resources = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(fileSystemPath, 1)) {
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                Path fullPath = it.next().toAbsolutePath();
                String normalizedFullPath = fullPath.toString().replace('\\', '/');
                String normalizedResourceFolder = resourceFolder.toString().replace('\\', '/');
                if (!normalizedFullPath.endsWith(normalizedResourceFolder)) {
                    int fullPathLength = fullPath.getNameCount();
                    Path resourcePath = fullPath.subpath(fullPathLength - resourcePathLength, fullPathLength);
                    resources.add(resourcePath.toString());
                }
            }
        }

        return resources;
    }

    @Override
    public InputStream open(String resource) throws IOException {
        return this.getClass().getResource(String.format("/%s", resource)).openStream();
    }

    @Override
    public List<String> list(String path) throws IOException {
        URI uri;
        try {
            uri = TextureMap.class.getResource(String.format("/%s", path)).toURI();
        } catch (URISyntaxException e) {
            throw new IOException(String.format("Failed to create URI for resource path: '%s'", path), e);
        }

        if (uri.getScheme().equals("jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                return getResources(fileSystem.getPath(String.format("/%s", path)), Paths.get(path));
            }
        } else {
            return getResources(Paths.get(uri), Paths.get(path));
        }
    }
}
