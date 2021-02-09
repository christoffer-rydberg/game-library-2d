package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultShader implements Shader, Disposable {

    private final int id;
    private boolean disposed;

    private DefaultShader(CharSequence source, int type) {
        id = OpenGL.instance().glCreateShader(type);
        OpenGL.instance().glShaderSource(id, source);
        OpenGL.instance().glCompileShader(id);
        checkCompileStatus();
    }

    public static DefaultShader fromFile(String path, ShaderType shaderType, Disposer disposer) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(DefaultShader.class.getClassLoader().getResourceAsStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load a shader file!"
                    + System.lineSeparator() + ex.getMessage());
        }

        return fromString(builder.toString(), shaderType, disposer);
    }

    public static DefaultShader fromString(String src, ShaderType shaderType, Disposer disposer) {
        DefaultShader shader = createShader(src, shaderType);
        disposer.registerDisposal(shader);
        return shader;
    }

    private static DefaultShader createShader(String src, ShaderType shaderType) {
        switch (shaderType) {
            case COMPUTE:
                return new DefaultShader(src, OpenGL.GL_COMPUTE_SHADER);
            case FRAGMENT:
                return new DefaultShader(src, OpenGL.GL_FRAGMENT_SHADER);
            case GEOMETRY:
                return new DefaultShader(src, OpenGL.GL_GEOMETRY_SHADER);
            case VERTEX:
                return new DefaultShader(src, OpenGL.GL_VERTEX_SHADER);
            default:
                throw new IllegalStateException("Unexpected value: " + shaderType);
        }
    }

    private void checkCompileStatus() {
        int status = OpenGL.instance().glGetShaderi(id, OpenGL.GL_COMPILE_STATUS);
        if (status != OpenGL.GL_TRUE) {
            throw new IllegalStateException(OpenGL.instance().glGetShaderInfoLog(id));
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