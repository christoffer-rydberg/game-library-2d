package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.framework.CallbackHandler;
import com.gamelibrary2d.framework.MouseCursorMode;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

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

    private final Rectangle bounds;

    private final boolean fullScreen;

    private final List<WindowHint> additionalWindowHints = new ArrayList<>();

    private long windowHandle;

    private int windowWidth;

    private int windowHeight;

    private String title;

    private boolean initialized;

    private boolean created;

    private MouseCursorMode mouseCursorMode = MouseCursorMode.NORMAL;

    private Callback debugProc;

    protected GlfwWindow(String title, int width, int height, boolean fullScreen) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(0, 0, width, height);
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
    public void show() {
        glfwShowWindow(windowHandle);
    }

    @Override
    public void hide() {
        glfwHideWindow(windowHandle);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    @Override
    public void render(Renderable renderable, float alpha) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        if (renderable != null)
            renderable.render(alpha);
        glfwSwapBuffers(windowHandle);
    }

    @Override
    public MouseCursorMode getMouseCursorMode() {
        return mouseCursorMode;
    }

    @Override
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

    @Override
    public int height() {
        return windowHeight;
    }

    @Override
    public int width() {
        return windowWidth;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void pollEvents() {
        glfwPollEvents();
    }

    @Override
    public void initialize() {

        if (!initialized) {

            if (!glfwInit())
                throw new GameLibrary2DRuntimeException("Unable to initialize GLFW");

            glfwDefaultWindowHints();

            initialized = true;
        }
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

    @Override
    public void create() {
        if (!initialized) {
            throw new GameLibrary2DRuntimeException("Not initialized");
        }

        if (!created) {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode vidmode = glfwGetVideoMode(monitor);

            if (fullScreen) {
                int actualWidth = width <= 0 ? vidmode.width() : Math.min(width, vidmode.width());
                int actualHeight = height <= 0 ? vidmode.height() : Math.min(height, vidmode.height());
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
                        onCreate(title, vidmode.width() - 1, vidmode.height(), NULL);
                    } else {
                        onCreate(title, vidmode.width(), vidmode.height(), NULL);
                    }
                } else {
                    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
                    onCreate(title, width, height, NULL);

                    // Center window
                    int xPos = Math.max(0, (vidmode.width() - width) / 2);
                    int yPos = Math.max(30, (vidmode.height() - height) / 2);
                    glfwSetWindowPos(windowHandle, xPos, yPos);
                }
            }

            focus();

            created = true;
        }
    }

    private void onCreate(String title, int width, int height, long monitor) {

        for (var hint : additionalWindowHints)
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
    }

    @Override
    public void createCallBacks(CallbackHandler game) {

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwSetKeyCallback(windowHandle, new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                game.onKeyCallback(key, scancode, action, mods);
            }
        });

        glfwSetCharCallback(windowHandle, new GLFWCharCallback() {
            public void invoke(long window, int charInput) {
                game.onCharCallback((char) charInput);
            }
        });

        glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                // The GLFW mouse coordinates are relative to the
                // upper left corner of the window with the Y-axis down.
                // The y-value is flipped in order to get the position
                // relative to the lower left corner, with the Y-axis up.
                game.onCursorPosCallback(xpos, windowHeight - ypos);
            }
        });

        glfwSetCursorEnterCallback(windowHandle, new GLFWCursorEnterCallback() {
            public void invoke(long window, boolean entered) {
                game.onCursorEnterCallback(entered);
            }
        });

        glfwSetMouseButtonCallback(windowHandle, new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                game.onMouseButtonCallback(button, action, mods);
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            public void invoke(long window, double xoffset, double yoffset) {
                game.onScrollCallback(xoffset, yoffset);
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
        created = false;
    }

    @Override
    public long getWindowHandle() {
        return windowHandle;
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