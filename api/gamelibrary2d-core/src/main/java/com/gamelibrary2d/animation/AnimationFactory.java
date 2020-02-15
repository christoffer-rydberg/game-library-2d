package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.functional.Func;
import org.apache.commons.imaging.ImageReadException;

import java.io.IOException;
import java.net.URL;

public class AnimationFactory {

    public static final Point NO_CONSTRAINTS = new Point(Rectangle.INFINITE_VALUE, Rectangle.INFINITE_VALUE);

    public static Animation create(URL url, String format, Rectangle bounds, Disposer disposer) throws IOException {
        return create(readAllBytes(url), format, bounds, disposer);
    }

    public static Animation create(URL url, String format, Rectangle scale, Point sizeConstraints, Disposer disposer) throws IOException {
        return create(readAllBytes(url), format, scale, sizeConstraints, disposer);
    }

    public static Animation create(byte[] bytes, String format, Rectangle bounds, Disposer disposer) throws IOException {
        return create(bytes, format, bounds, InternalResizeMode.ABSOLUTE, NO_CONSTRAINTS, disposer);
    }

    public static Animation create(byte[] bytes, String format, Rectangle scale, Point sizeConstraints, Disposer disposer) throws IOException {
        return create(bytes, format, scale, InternalResizeMode.SCALE, sizeConstraints, disposer);
    }

    private static Animation create(byte[] bytes, String format, Rectangle bounds, InternalResizeMode resizeMode, Point sizeConstraints, Disposer disposer) throws IOException {
        try {
            Func<Point, Rectangle> resizeFunc = size -> resizeFunc(bounds, resizeMode, sizeConstraints, size);
            switch (format) {
                case AnimationFormats.GIF:
                    return InternalGifAnimationFactory.create(bytes, resizeFunc, disposer);
                default:
                    throw new IllegalArgumentException(String.format("Unsupported format: %s", format));
            }
        } catch (ImageReadException e) {
            throw new IOException("Failed to read image", e);
        }
    }

    private static Rectangle applyConstraints(Rectangle bounds, Point imageSize, Point sizeConstraints) {
        float scale = bounds.width();
        var aspect = bounds.resize(1f / scale);
        scale = Math.min(scale, sizeConstraints.getX() / imageSize.getX());
        scale = Math.min(scale, sizeConstraints.getY() / imageSize.getY());
        return aspect.resize(scale);
    }

    private static Rectangle resizeFunc(Rectangle bounds, InternalResizeMode resizeMode, Point sizeConstraints, Point imageSize) {
        switch (resizeMode) {
            case ABSOLUTE:
                return bounds.resize(1f / imageSize.getX(), 1f / imageSize.getY());
            case SCALE:
                return applyConstraints(bounds, imageSize, sizeConstraints);
            default:
                throw new IllegalStateException("Unexpected value: " + resizeMode);
        }
    }

    private static byte[] readAllBytes(URL url) throws IOException {
        try (var stream = url.openStream()) {
            return stream.readAllBytes();
        }
    }
}