package com.gamelibrary2d.framework;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;

public interface Window {

    void initialize();

    void setEventListener(WindowEventListener eventListener);

    void show();

    void setTitle(String string);

    void pollEvents();

    void render(Renderable content, float alpha);

    void dispose();

    boolean isCloseRequested();

    void focus();

    boolean isFullScreen();

    /**
     * The width of the window.
     * <br>
     * <br>
     * In windowed mode, the width is always expressed in pixels.
     * <br>
     * <br>
     * In fullscreen mode, however, the width might differ from {@link #getMonitorWidth()} depending on resolution.
     */
    int getWidth();

    /**
     * The height of the window.
     * <br>
     * <br>
     * In windowed mode, the height is always expressed in pixels.
     * <br>
     * <br>
     * In fullscreen mode, however, the height might differ from {@link #getMonitorHeight()} depending on resolution.
     */
    int getHeight();

    /**
     * The pixel width of the primary monitor.
     */
    int getMonitorWidth();

    /**
     * The pixel height of the primary monitor.
     */
    int getMonitorHeight();

    /**
     * The physical width of the primary monitor in millimetres.
     */
    double getPhysicalWidth();

    /**
     * The physical height of the primary monitor in millimetres.
     */
    double getPhysicalHeight();

    default double getPhysicalPixelWidth() {
        double physicalWidth = getPhysicalWidth();
        return isFullScreen()
                ? physicalWidth / getWidth()
                : physicalWidth / getMonitorWidth();
    }

    default double getPhysicalPixelHeight() {
        double physicalHeight = getPhysicalHeight();
        return isFullScreen()
                ? physicalHeight / getHeight()
                : physicalHeight / getMonitorHeight();
    }

    default Point getWindowSize() {
        return new Point(getWidth(), getHeight());
    }

    default double getPhysicalMonitorSize() {
        double physicalWidth = getPhysicalWidth();
        double physicalHeight = getPhysicalHeight();
        return Math.sqrt(physicalWidth * physicalWidth + physicalHeight * physicalHeight);
    }

    default double getPhysicalWindowSize() {
        if (isFullScreen()) {
            return getPhysicalMonitorSize();
        } else {
            double widthFactor = (double) getWidth() / getMonitorWidth();
            double heightFactor = (double) getHeight() / getMonitorHeight();
            double physicalWidth = getPhysicalWidth() * widthFactor;
            double physicalHeight = getPhysicalHeight() * heightFactor;
            return Math.sqrt(physicalWidth * physicalWidth + physicalHeight * physicalHeight);
        }
    }

    /**
     * Provides a content scale - a factor that can be used to scale UI components and keep a reasonable size for
     * different monitors and/or window sizes.
     * <br>
     * <br>
     * Example:
     * <br>
     * Say that you want a component to appear as approximately 100x100 mm on a window with a physical size of
     * 700 mm (the physical length of the diagonal).
     * <br>
     * <br>
     * 1. Create a {@link Rectangle} of size 100x100.
     * <br>
     * <br>
     * 2. Create a content scale with the base size set to 700.
     * <br>
     * <br>
     * 3. Apply the content scale to the rectangle (e.g. by using {@link Rectangle#resize}).
     * <br>
     * <br>
     * <p>
     * The base size is needed as a point of reference. In the above example you have set your reference to 700 mm.
     * By keeping that very same base size when running your game on a different monitor (or in windowed mode with a different window size),
     * your component will scale to fit that screen. For example, if you run the above example (base size = 700) on a monitor with a physical size
     * of 350 mm, the component will appear as approximately 50x50 mm.
     * <br>
     * <br>
     * Since the scale is determined by the length of the window's diagonal, the result might not be desirable for
     * windows with vastly different aspect ratios.
     *
     * @param baseSize The physical length of the window's diagonal in millimetres.
     */
    default Point getContentScale(double baseSize) {
        double scaleFactor = getPhysicalWindowSize() / baseSize;
        double invertedPixelSizeX = 1.0 / getPhysicalPixelWidth();
        double invertedPixelSizeY = 1.0 / getPhysicalPixelHeight();
        return new Point((float) (invertedPixelSizeX * scaleFactor), (float) (invertedPixelSizeY * scaleFactor));
    }
}