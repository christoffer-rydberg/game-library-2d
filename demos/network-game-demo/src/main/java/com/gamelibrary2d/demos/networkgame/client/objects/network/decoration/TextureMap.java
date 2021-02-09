package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Texture;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TextureMap {
    private final Map<Byte, Map<Byte, Texture>> textures = new HashMap<>();
    private final Set<Byte> emptyKeySet = Collections.unmodifiableSet(new HashSet<>(0));

    private static Byte tryParseByte(String s) {
        try {
            return Byte.parseByte(s);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private static Byte readFileNameAsByte(Path path) {
        String name = path.getFileName().toString();
        int pos = name.lastIndexOf(".");
        return pos > 0 ? tryParseByte(name.substring(0, pos)) : null;
    }

    private static Texture createTexture(Path path, Disposer disposer) throws IOException {
        URL url = path.toUri().toURL();
        return DefaultTexture.create(url, disposer);
    }

    private Map<Byte, Texture> load(String resourceFolder, String regex, Disposer disposer) throws IOException {
        Pattern pattern = regex != null ? Pattern.compile(regex) : null;

        URI uri;
        try {
            uri = TextureMap.class.getResource(resourceFolder).toURI();
        } catch (URISyntaxException e) {
            throw new IOException(String.format("Failed to create URI for resource folder: '%s'", resourceFolder), e);
        }

        if (uri.getScheme().equals("jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                Path path = fileSystem.getPath(resourceFolder);
                return load(path, pattern, disposer);
            }
        } else {
            Path path = Paths.get(uri);
            return load(path, pattern, disposer);
        }
    }

    private Map<Byte, Texture> load(Path path, Pattern pattern, Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = new HashMap<>();
        try (Stream<Path> walk = Files.walk(path, 1)) {
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                path = it.next();
                if (pattern == null || pattern.matcher(path.toString()).find()) {
                    Byte key = readFileNameAsByte(path);
                    if (key != null) {
                        Texture texture = createTexture(path, disposer);
                        textures.put(key, texture);
                    }
                }
            }
        }

        return textures;
    }

    private void initializePlayerTextures(Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = load("/images/spacecrafts/", "^*.png$", disposer);
        this.textures.put(ObjectTypes.PLAYER, textures);
    }

    private void initializeObstacleTextures(Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = load("/images/obstacles/", "^*.png$", disposer);
        this.textures.put(ObjectTypes.OBSTACLE, textures);
    }

    public void initialize(Disposer disposer) throws IOException {
        initializePlayerTextures(disposer);
        initializeObstacleTextures(disposer);
    }

    public Texture getTexture(Byte primaryType, Byte secondaryType) {
        Map<Byte, Texture> textures = this.textures.get(primaryType);
        return textures != null ? textures.get(secondaryType) : null;
    }

    public Set<Byte> getKeys(byte primaryType) {
        Map<Byte, Texture> textures = this.textures.get(primaryType);
        return textures != null ? textures.keySet() : emptyKeySet;
    }
}
