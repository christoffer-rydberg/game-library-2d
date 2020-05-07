package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.resources.Shader;
import com.gamelibrary2d.util.RenderSettings;

import java.nio.FloatBuffer;

public class ShaderProgram implements Disposable {

    private final static String SETTINGS_ATTRIBUTE = "settings";

    private static int activeProgram;

    private static ShaderProgram defaultShaderProgram;
    private static ShaderProgram defaultParticleUpdaterProgram;
    private static ShaderProgram pointParticleShaderProgram;
    private static ShaderProgram quadParticleShaderProgram;
    private static ShaderProgram pointShaderProgram;
    private static ShaderProgram quadShaderProgram;
    private final FloatBuffer settings = BufferUtils.createFloatBuffer(RenderSettings.MAXIMUM_SETTINGS_SIZE);
    private boolean initialized;
    private int programId;
    private int uniModel;
    private int settingsLocation;

    private ShaderProgram(Disposer disposer) {
        programId = OpenGL.instance().glCreateProgram();
        disposer.registerDisposal(this);
    }

    public static ShaderProgram create(Disposer disposer) {
        return new ShaderProgram(disposer);
    }

    public static ShaderProgram getDefaultParticleUpdaterProgram() {
        return defaultParticleUpdaterProgram;
    }

    public static void setDefaultParticleUpdaterProgram(ShaderProgram defaultParticleUpdaterProgram) {
        ShaderProgram.defaultParticleUpdaterProgram = defaultParticleUpdaterProgram;
    }

    public static ShaderProgram getDefaultShaderProgram() {
        return defaultShaderProgram;
    }

    public static void setDefaultShaderProgram(ShaderProgram shaderProgram) {
        defaultShaderProgram = shaderProgram;
    }

    public static ShaderProgram getPointParticleShaderProgram() {
        return pointParticleShaderProgram;
    }

    public static void setPointParticleShaderProgram(ShaderProgram shaderProgram) {
        pointParticleShaderProgram = shaderProgram;
    }

    public static ShaderProgram getQuadParticleShaderProgram() {
        return quadParticleShaderProgram;
    }

    public static void setQuadParticleShaderProgram(ShaderProgram shaderProgram) {
        quadParticleShaderProgram = shaderProgram;
    }

    public static ShaderProgram getPointShaderProgram() {
        return pointShaderProgram;
    }

    public static void setPointShaderProgram(ShaderProgram shaderProgram) {
        pointShaderProgram = shaderProgram;
    }

    public static ShaderProgram getQuadShaderProgram() {
        return quadShaderProgram;
    }

    public static void setQuadShaderProgram(ShaderProgram shaderProgram) {
        quadShaderProgram = shaderProgram;
    }

    public static void bind(int program) {
        if (!inUse(program)) {
            OpenGL.instance().glUseProgram(program);
            activeProgram = program;
        }
    }

    public static void unbind(int program) {
        if (inUse(program)) {
            OpenGL.instance().glUseProgram(0);
            activeProgram = 0;
        }
    }

    public static boolean inUse(int program) {
        return activeProgram == program;
    }

    public static int getActiveProgram() {
        return activeProgram;
    }

    public void initialize() {

        if (initialized)
            return;

        OpenGL.instance().glLinkProgram(programId);

        // Check that the program was linked successfully.
        int status = OpenGL.instance().glGetProgrami(programId, OpenGL.GL_LINK_STATUS);
        if (status != OpenGL.GL_TRUE) {
            throw new IllegalStateException(OpenGL.instance().glGetProgramInfoLog(programId));
        }

        initialized = true;
    }

    public void initializeMvp(int windowWidth, int windowHeight) {
        if (!initialized) {
            throw new IllegalStateException("The shader program has not been initialized.");
        }

        int boundProgram = activeProgram;

        bind();

        // Set the model matrix
        MatrixBuffer model = new MatrixBuffer();
        uniModel = OpenGL.instance().glGetUniformLocation(programId, "model");
        OpenGL.instance().glUniformMatrix4fv(uniModel, false, model.getFloatBuffer());

        // Set the view matrix
        MatrixBuffer view = new MatrixBuffer();
        int uniView = OpenGL.instance().glGetUniformLocation(programId, "view");
        OpenGL.instance().glUniformMatrix4fv(uniView, false, view.getFloatBuffer());

        // Set the projection matrix
        MatrixBuffer projection = MatrixBuffer.orthographic(0, windowWidth, 0, windowHeight, 1f, -1f);
        int uniProjection = OpenGL.instance().glGetUniformLocation(programId, "projection");
        OpenGL.instance().glUniformMatrix4fv(uniProjection, false, projection.getFloatBuffer());

        settingsLocation = OpenGL.instance().glGetUniformLocation(programId, SETTINGS_ATTRIBUTE);

        bind(boundProgram);
    }

    @Override
    public void dispose() {
        unbind(); // Important for cleanup, delete is not enough.
        OpenGL.instance().glDeleteProgram(programId);
        initialized = false;
    }

    public int getProgramId() {
        return programId;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void attachShader(Shader shader) {
        OpenGL.instance().glAttachShader(programId, shader.getId());
    }

    public void detachShader(Shader shader) {
        OpenGL.instance().glDetachShader(programId, shader.getId());
    }

    public void bindFragDataLocation(int location, String name) {
        OpenGL.instance().glBindFragDataLocation(programId, location, name);
    }

    public int getAttributeLocation(CharSequence name) {
        return OpenGL.instance().glGetAttribLocation(programId, name);
    }

    public int getUniformLocation(CharSequence name) {
        return OpenGL.instance().glGetUniformLocation(programId, name);
    }

    public float getSetting(int index) {
        return settings.get(index);
    }

    public boolean updateSetting(int index, float value) {
        if (value == getSetting(index))
            return false;
        settings.put(index, value);
        return true;
    }

    public void updateSettings(float[] settings, int offset, int length) {
        this.settings.clear();
        this.settings.put(settings, offset, length);
        this.settings.flip();
    }

    public void applySettings() {
        OpenGL.instance().glUniform1fv(settingsLocation, this.settings);
    }

    public void updateModelMatrix(ModelMatrix modelMatrix) {
        OpenGL.instance().glUniformMatrix4fv(uniModel, false, modelMatrix.getFloatBuffer());
    }

    public void bind() {
        bind(programId);
    }

    public void unbind() {
        unbind(programId);
    }

    public boolean inUse() {
        return inUse(programId);
    }
}