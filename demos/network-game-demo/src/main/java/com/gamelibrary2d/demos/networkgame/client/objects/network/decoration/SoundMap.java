package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.decoders.AudioDecoder;
import com.gamelibrary2d.sound.decoders.VorbisDecoder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SoundMap {
    private final SoundManager soundManager;
    private final AudioDecoder decoder;
    private final Map<Byte, Map<Byte, URL>> sound = new HashMap<>();
    private final Set<Byte> emptyKeySet = Collections.unmodifiableSet(new HashSet<>(0));

    public SoundMap(SoundManager soundManager) {
        this.soundManager = soundManager;
        this.decoder = new VorbisDecoder();
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

    private Map<Byte, URL> load(String resourceFolder, String regex) throws IOException {
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
                return load(path, pattern);
            }
        } else {
            Path path = Paths.get(uri);
            return load(path, pattern);
        }
    }

    private Map<Byte, URL> load(Path path, Pattern pattern) throws IOException {
        Map<Byte, URL> sound = new HashMap<>();
        try (Stream<Path> walk = Files.walk(path, 1)) {
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                path = it.next();
                if (pattern == null || pattern.matcher(path.toString()).find()) {
                    Byte key = readFileNameAsByte(path);
                    if (key != null) {
                        URL url = path.toUri().toURL();
                        soundManager.loadSoundBuffer(url, decoder);
                        sound.put(key, url);
                    }
                }
            }
        }

        return sound;
    }

    private void initializeDestroyedObjects() throws IOException {
        Map<Byte, URL> sounds = load("/sounds/obstacles/destroyed/", "^*.ogg$");
        this.sound.put(ObjectTypes.OBSTACLE, sounds);
    }

    public void initialize() throws IOException {
        initializeDestroyedObjects();
    }

    public URL getDestroyedSound(Byte primaryType, Byte secondaryType) {
        Map<Byte, URL> sounds = this.sound.get(primaryType);
        if (sounds != null) {
            URL sound = sounds.get(secondaryType);
            return sound != null ? sound : sounds.get((byte) 0);
        } else {
            return null;
        }
    }
}