package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shader implements Disposable {

    private final int id;
    private boolean disposed;

    private Shader(CharSequence source, int type) {
        id = OpenGL.instance().glCreateShader(type);
        OpenGL.instance().glShaderSource(id, source);
        OpenGL.instance().glCompileShader(id);
        checkCompileStatus();
    }

    public static Shader fromFile(String path, ShaderType shaderType, Disposer disposer) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Shader.class.getClassLoader().getResourceAsStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new GameLibrary2DRuntimeException("Failed to load a shader file!"
                    + System.lineSeparator() + ex.getMessage());
        }

        return fromString(builder.toString(), shaderType, disposer);
    }

    public static Shader fromString(String src, ShaderType shaderType, Disposer disposer) {
        Shader shader = createShader(src, shaderType);
        disposer.registerDisposal(shader);
        return shader;
    }

    private static Shader createShader(String src, ShaderType shaderType) {
        switch (shaderType) {
            case COMPUTE:
                return new Shader(src, OpenGL.GL_COMPUTE_SHADER);
            case FRAGMENT:
                return new Shader(src, OpenGL.GL_FRAGMENT_SHADER);
            case GEOMETRY:
                return new Shader(src, OpenGL.GL_GEOMETRY_SHADER);
            case VERTEX:
                return new Shader(src, OpenGL.GL_VERTEX_SHADER);
            default:
                throw new GameLibrary2DRuntimeException("Argument out of range");
        }
    }

    private void checkCompileStatus() {
        int status = OpenGL.instance().glGetShaderi(id, OpenGL.GL_COMPILE_STATUS);
        if (status != OpenGL.GL_TRUE) {
            throw new GameLibrary2DRuntimeException(OpenGL.instance().glGetShaderInfoLog(id));
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public void dispose() {
        if (!disposed) return;
        OpenGL.instance().glDeleteShader(id);
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }
}