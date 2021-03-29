package com.example.framework.android;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Android_OpenGL implements OpenGL {
    private final static IntBuffer SINGLE_INTEGER = BufferUtils.createIntBuffer(1);
    private static Android_OpenGL instance;

    private final OpenGLVersion supportedVersion;

    private Android_OpenGL(OpenGLVersion supportedVersion) {
        this.supportedVersion = supportedVersion;
    }

    public static Android_OpenGL instance() {
        return instance;
    }

    static void createInstance(OpenGLVersion supportedVersion) {
        if (instance == null) {
            instance = new Android_OpenGL(supportedVersion);
        } else {
            throw new RuntimeException("OpenGL instance has already been created");
        }
    }

    private static void validateRemaining(Buffer buffer) {
        if (buffer != null && buffer.remaining() == 0) {
            throw new RuntimeException("Cannot upload buffer with no remaining elements");
        }
    }

    private static int getRemainingElements(Buffer buffer, int elementSize) {
        validateRemaining(buffer);
        return buffer.remaining() / elementSize;
    }

    private static int getRemainingBytes(ByteBuffer buffer) {
        validateRemaining(buffer);
        return buffer.remaining();
    }

    private static int getRemainingBytes(IntBuffer buffer) {
        validateRemaining(buffer);
        return buffer.remaining() * 4;
    }

    private static int getRemainingBytes(FloatBuffer buffer) {
        validateRemaining(buffer);
        return buffer.remaining() * 4;
    }

    @Override
    public void glClear(int mask) {
        GLES20.glClear(mask);
    }

    @Override
    public void glGetBufferSubData(int target, int offset, FloatBuffer data) {
        Buffer mappedBuffer = GLES30.glMapBufferRange(
                GLES30.GL_ARRAY_BUFFER,
                offset,
                getRemainingBytes(data),
                GLES30.GL_MAP_READ_BIT);

        if (mappedBuffer != null) {
            FloatBuffer transformedBuffer = ((ByteBuffer) mappedBuffer).asFloatBuffer();
            data.put(transformedBuffer);
        }
    }

    @Override
    public void glGetBufferSubData(int target, int offset, IntBuffer data) {
        Buffer mappedBuffer = GLES30.glMapBufferRange(
                GLES30.GL_ARRAY_BUFFER,
                offset,
                getRemainingBytes(data),
                GLES30.GL_MAP_READ_BIT);

        if (mappedBuffer != null) {
            IntBuffer transformedBuffer = ((ByteBuffer) mappedBuffer).asIntBuffer();
            data.put(transformedBuffer);
        }
    }

    @Override
    public void glMemoryBarrier(int barriers) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void glDispatchCompute(int numGroupsX, int numGroupsY, int numGroupsZ) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void glPointSize(float size) {
        GLES10.glPointSize(size);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        GLES20.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void glBufferSubData(int target, long offset, FloatBuffer data) {
        GLES20.glBufferSubData(target, (int) offset, getRemainingBytes(data), data);
    }

    @Override
    public void glBufferSubData(int target, long offset, IntBuffer data) {
        GLES20.glBufferSubData(target, (int) offset, getRemainingBytes(data), data);
    }

    @Override
    public int glGenBuffers() {
        SINGLE_INTEGER.clear();
        GLES20.glGenBuffers(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
    }

    @Override
    public void glBufferData(int target, long size, int usage) {
        GLES20.glBufferData(target, (int) size, null, usage);
    }

    @Override
    public void glBufferData(int target, ByteBuffer data, int usage) {
        GLES20.glBufferData(target, getRemainingBytes(data), data, usage);
    }

    @Override
    public void glBufferData(int target, FloatBuffer data, int usage) {
        GLES20.glBufferData(target, getRemainingBytes(data), data, usage);
    }

    @Override
    public void glBufferData(int target, IntBuffer data, int usage) {
        GLES20.glBufferData(target, getRemainingBytes(data), data, usage);
    }

    @Override
    public int glGenVertexArrays() {
        SINGLE_INTEGER.clear();
        GLES30.glGenVertexArrays(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glBindVertexArray(int array) {
        GLES30.glBindVertexArray(array);
    }

    @Override
    public void glDeleteVertexArrays(int arrays) {
        SINGLE_INTEGER.clear();
        SINGLE_INTEGER.put(arrays);
        SINGLE_INTEGER.flip();
        GLES30.glDeleteVertexArrays(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GLES20.glVertexAttribPointer(index, size, type, normalized, stride, (int) pointer);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);
    }

    @Override
    public void glDeleteBuffers(int buffers) {
        SINGLE_INTEGER.clear();
        SINGLE_INTEGER.put(buffers);
        SINGLE_INTEGER.flip();
        GLES20.glDeleteBuffers(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, long indices) {
        GLES20.glDrawElements(mode, count, type, (int) indices);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        GLES20.glDrawArrays(mode, first, count);
    }

    @Override
    public String glGetShaderInfoLog(int id) {
        return GLES20.glGetShaderInfoLog(id);
    }

    @Override
    public int glCreateShader(int type) {
        return GLES20.glCreateShader(type);
    }

    @Override
    public void glShaderSource(int shader, CharSequence source) {
        GLES20.glShaderSource(shader, source.toString());
    }

    @Override
    public void glCompileShader(int shader) {
        GLES20.glCompileShader(shader);
    }

    @Override
    public void glDeleteShader(int shader) {
        GLES20.glDeleteShader(shader);
    }

    @Override
    public int glGetShaderi(int shader, int pname) {
        SINGLE_INTEGER.clear();
        GLES20.glGetShaderiv(shader, pname, SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glUniform2f(int location, float v0, float v1) {
        GLES20.glUniform2f(location, v0, v1);
    }

    @Override
    public void glUniform1fv(int location, FloatBuffer value) {
        GLES20.glUniform1fv(location, getRemainingElements(value, 1), value);
    }

    @Override
    public void glUniform2fv(int location, FloatBuffer value) {
        GLES20.glUniform1fv(location, getRemainingElements(value, 2), value);
    }

    @Override
    public void glUniform4fv(int location, FloatBuffer value) {
        GLES20.glUniform4fv(location, getRemainingElements(value, 4), value);
    }

    @Override
    public void glUniform1f(int location, float value) {
        GLES20.glUniform1f(location, value);
    }

    @Override
    public void glUniform1i(int location, int value) {
        GLES20.glUniform1i(location, value);
    }

    @Override
    public int glCreateProgram() {
        return GLES20.glCreateProgram();
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
    }

    @Override
    public void glDetachShader(int program, int shader) {
        GLES20.glDetachShader(program, shader);
    }

    @Override
    public void glLinkProgram(int program) {
        GLES20.glLinkProgram(program);
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        return GLES20.glGetProgramInfoLog(program);
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    @Override
    public void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix4fv(location, getRemainingElements(value, 4 * 4), transpose, value);
    }

    @Override
    public void glDeleteProgram(int program) {
        GLES20.glDeleteProgram(program);
    }

    @Override
    public int glGetAttribLocation(int program, CharSequence name) {
        return GLES20.glGetAttribLocation(program, name.toString());
    }

    @Override
    public int glGetUniformLocation(int program, CharSequence name) {
        return GLES20.glGetUniformLocation(program, name.toString());
    }

    @Override
    public void glUseProgram(int program) {
        GLES20.glUseProgram(program);
    }

    @Override
    public int glGetProgrami(int program, int pname) {
        SINGLE_INTEGER.clear();
        GLES20.glGetProgramiv(program, pname, SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glDeleteTextures(int arg0) {
        SINGLE_INTEGER.clear();
        SINGLE_INTEGER.put(arg0);
        SINGLE_INTEGER.flip();
        GLES20.glDeleteTextures(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
    }

    @Override
    public int glGenTextures() {
        SINGLE_INTEGER.clear();
        GLES20.glGenTextures(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        GLES20.glPixelStorei(pname, param);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                             int format, int type, ByteBuffer data) {
        GLES20.glTexImage2D(target, level, format, width, height, 0, format, type, data);
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        GLES20.glTexParameteri(target, pname, param);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GLES20.glBindTexture(target, texture);
    }

    @Override
    public void glEnable(int target) {
        GLES20.glEnable(target);
    }

    @Override
    public void glDisable(int target) {
        GLES20.glDisable(target);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        GLES30.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void glHint(int target, int hint) {
        GLES20.glHint(target, hint);
    }

    @Override
    public void glLineWidth(float width) {
        GLES20.glLineWidth(width);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    public void glClearColor(float r, float g, float b, float a) {
        GLES20.glClearColor(r, b, b, a);
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        GLES20.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public int glGenFramebuffers() {
        SINGLE_INTEGER.clear();
        GLES20.glGenFramebuffers(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
        return SINGLE_INTEGER.get(0);
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        GLES20.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glDeleteFramebuffers(int framebuffer) {
        SINGLE_INTEGER.clear();
        SINGLE_INTEGER.put(framebuffer);
        SINGLE_INTEGER.flip();
        GLES20.glGenTextures(SINGLE_INTEGER.limit(), SINGLE_INTEGER);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer data) {
        GLES20.glReadPixels(x, y, width, height, format, type, data);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        GLES20.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
    }

    @Override
    public OpenGLVersion getSupportedVersion() {
        return supportedVersion;
    }
}