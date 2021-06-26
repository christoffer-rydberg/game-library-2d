package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.renderers.ShaderParameters;
import com.gamelibrary2d.resources.Shader;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram implements Disposable {

    private final static String PARAMETERS_ATTRIBUTE = "parameters";
    private final static int LOCATION_NOT_FOUND = Integer.MIN_VALUE;
    private static int activeProgram;
    private static ShaderProgram defaultShaderProgram;
    private static ShaderProgram defaultParticleUpdaterProgram;
    private static ShaderProgram pointParticleShaderProgram;
    private static ShaderProgram quadParticleShaderProgram;
    private static ShaderProgram pointShaderProgram;
    private static ShaderProgram quadShaderProgram;
    private final FloatBuffer parameters = BufferUtils.createFloatBuffer(ShaderParameters.MAX_LENGTH);
    private boolean initialized;
    private int programId;
    private int uniModel;
    private int parametersLocation;
    private Map<CharSequence, Integer> uniformLocations = new HashMap<>();
    private Map<CharSequence, Integer> attributeLocations = new HashMap<>();

    private ShaderProgram(Disposer disposer) {
        programId = OpenGL.instance().glCreateProgram();
        disposer.registerDisposal(this);
    }

    public static ShaderProgram create(Disposer disposer) {
        return new ShaderProgram(disposer);
    }

    public static ShaderProgram getDefaultParticleUpdaterProgram() {
        if (defaultParticleUpdaterProgram == null) {
            String openGlVersion = Runtime.getFramework().getOpenGL().getSupportedVersion().toString();
            throw new RuntimeException(String.format("%s does not support support compute shaders", openGlVersion));
        } else {
            return defaultParticleUpdaterProgram;
        }
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
        if (pointParticleShaderProgram == null) {
            String openGlVersion = Runtime.getFramework().getOpenGL().getSupportedVersion().toString();
            throw new RuntimeException(String.format("%s does not support support geometry shaders", openGlVersion));
        } else {
            return pointParticleShaderProgram;
        }
    }

    public static void setPointParticleShaderProgram(ShaderProgram shaderProgram) {
        pointParticleShaderProgram = shaderProgram;
    }

    public static ShaderProgram getQuadParticleShaderProgram() {
        if (quadParticleShaderProgram == null) {
            String openGlVersion = Runtime.getFramework().getOpenGL().getSupportedVersion().toString();
            throw new RuntimeException(String.format("%s does not support support geometry shaders", openGlVersion));
        } else {
            return quadParticleShaderProgram;
        }
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
            String programInfoLog = OpenGL.instance().glGetProgramInfoLog(programId);
            throw new IllegalStateException(programInfoLog);
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

        parametersLocation = OpenGL.instance().glGetUniformLocation(programId, PARAMETERS_ATTRIBUTE);

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

    public int getAttributeLocation(CharSequence name) {
        int location = attributeLocations.getOrDefault(name, LOCATION_NOT_FOUND);
        if (location == LOCATION_NOT_FOUND) {
            location = OpenGL.instance().glGetAttribLocation(programId, name);
            attributeLocations.put(name, location);
        }

        return location;
    }

    public int getUniformLocation(CharSequence name) {
        int location = uniformLocations.getOrDefault(name, LOCATION_NOT_FOUND);
        if (location == LOCATION_NOT_FOUND) {
            location = OpenGL.instance().glGetUniformLocation(programId, name);
            uniformLocations.put(name, location);
        }

        return location;
    }

    public float getParameter(int index) {
        return parameters.get(index);
    }

    public boolean setParameter(int index, float value) {
        if (value == getParameter(index))
            return false;
        parameters.put(index, value);
        return true;
    }

    public void setParameters(float[] parameters, int offset, int length) {
        this.parameters.clear();
        this.parameters.put(parameters, offset, length);
        this.parameters.flip();
    }

    public void applyParameters() {
        OpenGL.instance().glUniform1fv(parametersLocation, this.parameters);
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