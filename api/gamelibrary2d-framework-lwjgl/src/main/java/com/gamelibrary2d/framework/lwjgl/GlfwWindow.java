package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlfwWindow implements Window {

    public static boolean SETUP_DEBUG_MESSAGE_CALLBACK = false;

    private final int width;
    private final int height;
    private final boolean fullScreen;
    private final List<WindowHint> additionalWindowHints = new ArrayList<>();
    private int windowWidth;
    private int windowHeight;
    private int monitorWidth;
    private int monitorHeight;
    private int physicalWidth;
    private int physicalHeight;

    private long windowHandle;

    private long monitor;

    private String title;

    private boolean initialized;

    private MouseCursorMode mouseCursorMode = MouseCursorMode.NORMAL;

    private Callback debugProc;

    /**
     * Last known X coordinate of mouse cursor.
     */
    private float cursorPosX;
    /**
     * Last known Y coordinate of mouse cursor.
     */
    private float cursorPosY;

    protected GlfwWindow(String title, int width, int height, boolean fullScreen) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.fullScreen = fullScreen;
    }

    /**
     * Creates an instance of {@link GlfwWindow} in full-screen mode.
     */
    public static GlfwWindow createFullScreen(String title) {
        return new GlfwWindow(title, -1, -1, true);
    }

    /**
     * Creates an instance of {@link GlfwWindow} in full-screen mode with the
     * specified resolution.
     */
    public static GlfwWindow createFullScreen(String title, int resolutionX, int resolutionY) {
        return new GlfwWindow(title, resolutionX, resolutionY, true);
    }

    /**
     * Creates an instance of {@link GlfwWindow} in windowed mode running at
     * full-screen size with no borders.
     */
    public static GlfwWindow createWindowed(String title) {
        return new GlfwWindow(title, -1, -1, false);
    }

    /**
     * Creates an instance of {@link GlfwWindow} in windowed mode with the
     * specified size.
     *
     * @param width  The window's width in pixels
     * @param height The window's height in pixels
     */
    public static GlfwWindow createWindowed(String title, int width, int height) {
        return new GlfwWindow(title, width, height, false);
    }

    @Override
    public void initialize() {
        if (!initialized) {
            GLFWErrorCallback.createPrint(System.err).set();

            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            glfwDefaultWindowHints();

            monitor = glfwGetPrimaryMonitor();
            GLFWVidMode videoMode = glfwGetVideoMode(monitor);

            this.monitorWidth = videoMode.width();
            this.monitorHeight = videoMode.height();

            if (fullScreen) {
                int actualWidth = width <= 0 ? monitorWidth : Math.min(width, monitorWidth);
                int actualHeight = height <= 0 ? monitorHeight : Math.min(height, monitorHeight);
                onCreate(title, actualWidth, actualHeight, monitor);
            } else {
                boolean isWindowedFullscreen = width <= 0;

                if (isWindowedFullscreen) {
                    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                    glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

                    boolean isTransparentFrameBuffer = additionalWindowHints.stream()
                            .filter(x -> x.hint == GLFW_TRANSPARENT_FRAMEBUFFER).reduce((first, second) -> second)
                            .map(x -> x.value == GLFW_TRUE).orElse(false);

                    if (isTransparentFrameBuffer) {
                        // Transparent buffer does not work in full-screen. Make window smaller:
                        onCreate(title, videoMode.width() - 1, videoMode.height(), NULL);
                    } else {
                        onCreate(title, videoMode.width(), videoMode.height(), NULL);
                    }
                } else {
                    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
                    onCreate(title, width, height, NULL);

                    // Center window
                    int xPos = Math.max(0, (videoMode.width() - width) / 2);
                    int yPos = Math.max(30, (videoMode.height() - height) / 2);
                    glfwSetWindowPos(windowHandle, xPos, yPos);
                }
            }

            focus();

            initialized = true;
        }
    }

    private void onCreate(String title, int width, int height, long monitor) {
        for (WindowHint hint : additionalWindowHints)
            glfwWindowHint(hint.hint, hint.value);

        long windowId = glfwCreateWindow(width, height, title, monitor, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        windowWidth = width;
        windowHeight = height;

        windowHandle = windowId;
        glfwMakeContextCurrent(windowId);

        GL.createCapabilities();

        if (SETUP_DEBUG_MESSAGE_CALLBACK)
            debugProc = GLUtil.setupDebugMessageCallback();

        // Enable v-sync
        glfwSwapInterval(1);

        IntBuffer x = BufferUtils.createIntBuffer(1);
        IntBuffer y = BufferUtils.createIntBuffer(1);
        glfwGetMonitorPhysicalSize(this.monitor, x, y);

        physicalWidth = x.get(0);
        physicalHeight = y.get(0);
    }

    @Override
    public void show() {
        glfwShowWindow(windowHandle);
    }

    public void hide() {
        glfwHideWindow(windowHandle);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    @Override
    public void render(Renderable content, float alpha) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        if (content != null) {
            content.render(alpha);
        }

        glfwSwapBuffers(windowHandle);
    }

    public MouseCursorMode getMouseCursorMode() {
        return mouseCursorMode;
    }

    public void setMouseCursorMode(MouseCursorMode mouseCursorMode) {
        if (this.mouseCursorMode == mouseCursorMode) {
            return;
        }

        switch (mouseCursorMode) {
            case DISABLED:
                glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                break;
            case HIDDEN:
                glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                break;
            case NORMAL:
                glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                break;
        }

        this.mouseCursorMode = mouseCursorMode;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public int getHeight() {
        return windowHeight;
    }

    @Override
    public int getWidth() {
        return windowWidth;
    }

    @Override
    public double getPhysicalWidth() {
        return physicalWidth;
    }

    @Override
    public double getPhysicalHeight() {
        return physicalHeight;
    }

    @Override
    public int getMonitorWidth() {
        return monitorWidth;
    }

    @Override
    public int getMonitorHeight() {
        return monitorHeight;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void pollEvents() {
        glfwPollEvents();
    }

    public void clearAdditionalWindowHints() {
        additionalWindowHints.clear();
    }

    public void additionalWindowHint(int hint, int value) {
        additionalWindowHints.add(new WindowHint(hint, value));
    }

    public void setWindowAttribute(int attribute, int value) {
        glfwSetWindowAttrib(windowHandle, attribute, value);
    }

    public int getWindowAttribute(int attribute) {
        return glfwGetWindowAttrib(windowHandle, attribute);
    }

    public void focus() {
        glfwFocusWindow(windowHandle);
    }

    private KeyAction getKeyAction(int action) {
        switch (action) {
            case GLFW_PRESS:
                return KeyAction.DOWN;
            case GLFW_RELEASE:
                return KeyAction.UP;
            case GLFW_REPEAT:
                return KeyAction.DOWN_REPEAT;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    private PointerAction getPointerAction(int action) {
        switch (action) {
            case GLFW_PRESS:
                return PointerAction.DOWN;
            case GLFW_RELEASE:
                return PointerAction.UP;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    @Override
    public void setEventListener(WindowEventListener eventListener) {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwSetKeyCallback(windowHandle, new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                eventListener.onKeyAction(key, getKeyAction(action));
            }
        });

        glfwSetCharCallback(windowHandle, new GLFWCharCallback() {
            public void invoke(long window, int charInput) {
                eventListener.onCharInput((char) charInput);
            }
        });

        glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            public void invoke(long window, double posX, double posY) {
                cursorPosX = (float) posX;

                // The GLFW mouse coordinates are relative to the
                // upper left corner of the window with the Y-axis down.
                // The y-value is flipped in order to get the position
                // relative to the lower left corner, with the Y-axis up.
                cursorPosY = (float) (windowHeight - posY);

                eventListener.onPointerMove(0, cursorPosX, cursorPosY);
            }
        });

        glfwSetCursorEnterCallback(windowHandle, new GLFWCursorEnterCallback() {
            public void invoke(long window, boolean entered) {
                if (entered) {
                    eventListener.onPointerEnter(0);
                } else {
                    eventListener.onPointerLeave(0);
                }
            }
        });

        glfwSetMouseButtonCallback(windowHandle, new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                eventListener.onPointerAction(0, button, cursorPosX, cursorPosY, getPointerAction(action));
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            public void invoke(long window, double xOffset, double yOffset) {
                eventListener.onScroll(0, (float) xOffset, (float) yOffset);
            }
        });

        Lwjgl_Joystick.instance().initialize();

        glfwSetJoystickCallback(new GLFWJoystickCallback() {
            public void invoke(int jid, int event) {
                Lwjgl_Joystick joystick = Lwjgl_Joystick.instance();
                if (event == GLFW_CONNECTED)
                    joystick.onConnected(jid);
                else if (event == GLFW_DISCONNECTED)
                    joystick.onDisconnected(jid);
            }
        });
    }

    @Override
    public void dispose() {
        GL.setCapabilities(null);

        if (debugProc != null) {
            debugProc.free();
        }

        if (windowHandle != NULL) {
            glfwFreeCallbacks(windowHandle);
            glfwDestroyWindow(windowHandle);
            windowHandle = NULL;
        }

        Objects.requireNonNull(glfwSetJoystickCallback(null)).free();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        glfwTerminate();

        initialized = false;
    }

    private static class WindowHint {
        final int hint;
        final int value;

        WindowHint(int hint, int value) {
            this.hint = hint;
            this.value = value;
        }
    }
}