package com.gamelibrary2d.animation.io;

import com.gamelibrary2d.common.Rectangle;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class AnimationLoader {
    private final static Map<String, AnimationReader> animationReaders = initializeAnimationReaders();

    private static Map<String, AnimationReader> initializeAnimationReaders() {
        var animationLoaders = new HashMap<String, AnimationReader>();
        animationLoaders.put(StandardAnimationFormats.GIF, new InternalGifReader());
        return animationLoaders;
    }

    private static List<Path> getFilePaths(Path folderPath, Pattern pattern) throws IOException {
        var filePaths = new ArrayList<Path>();
        try (var stream = Files.walk(folderPath, 1)) {
            for (var itr = stream.iterator(); itr.hasNext(); ) {
                var filePath = itr.next();
                if (pattern.matcher(filePath.toString()).find()) {
                    filePaths.add(filePath);
                }
            }
        }

        filePaths.sort(Comparator.comparing(a -> a.getFileName().toString()));

        return filePaths;
    }

    /**
     * Registers an animation reader for the specified format.
     *
     * @param format The animation format.
     * @param reader The animation reader.
     */
    public static void registerAnimationReader(String format, AnimationReader reader) {
        animationReaders.put(format.toLowerCase(), reader);
    }

    /**
     * Loads an animation from the specified stream.
     *
     * @param stream The animation stream.
     * @param format The animation format.
     */
    public static AnimationMetadata load(InputStream stream, String format) throws IOException {
        var lowerCaseFormat = format.toLowerCase();
        if (animationReaders.containsKey(lowerCaseFormat)) {
            return animationReaders.get(lowerCaseFormat).read(stream);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported format: %s", format));
        }
    }

    /**
     * Loads an animation from the specified url.
     *
     * @param url    The animation url.
     * @param format The animation format.
     */
    public static AnimationMetadata load(URL url, String format) throws IOException {
        try (var stream = url.openStream()) {
            return load(stream, format);
        }
    }

    /**
     * Loads an animation from all image files inside the specified folder that matches the specified pattern.
     */
    public static AnimationMetadata load(Path folderPath, Pattern filePattern, float frameDuration) throws IOException {
        var filePaths = getFilePaths(folderPath, filePattern);
        var frames = new ArrayList<AnimationFrameMetadata>(filePaths.size());
        for (var filePath : filePaths) {
            var image = ImageIO.read(filePath.toUri().toURL());
            frames.add(new AnimationFrameMetadata(
                    image,
                    new Rectangle(0, 0, 1, 1),
                    -image.getWidth() / 2f,
                    -image.getHeight() / 2f,
                    frameDuration,
                    false,
                    false));
        }

        return new AnimationMetadata(frames);
    }
}
