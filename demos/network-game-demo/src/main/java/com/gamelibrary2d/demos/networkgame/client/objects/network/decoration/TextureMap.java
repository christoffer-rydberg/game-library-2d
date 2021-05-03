package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Texture;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class TextureMap {
    private final ResourceManager resourceManager;
    private final Map<Byte, Map<Byte, Texture>> textures = new HashMap<>();
    private final Set<Byte> emptyKeySet = Collections.unmodifiableSet(new HashSet<>(0));

    public TextureMap(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

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

    private Map<Byte, Texture> load(String resourceFolder, String regex, Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = new HashMap<>();
        Pattern pattern = regex != null ? Pattern.compile(regex) : null;
        List<String> paths = resourceManager.list(resourceFolder);
        for (String path : paths) {
            if (pattern == null || pattern.matcher(path).find()) {
                Byte key = readFileNameAsByte(Paths.get(path));
                if (key != null) {
                    Texture texture = resourceManager.load(path, s -> DefaultTexture.create(s, disposer));
                    textures.put(key, texture);
                }
            }
        }

        return textures;
    }

    private void initializePlayerTextures(Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = load("images/spacecrafts/", "^*.png$", disposer);
        this.textures.put(ObjectTypes.PLAYER, textures);
    }

    private void initializeObstacleTextures(Disposer disposer) throws IOException {
        Map<Byte, Texture> textures = load("images/obstacles/", "^*.png$", disposer);
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
