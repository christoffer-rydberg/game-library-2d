package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.OpenGL;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Lwjgl_OpenGL implements OpenGL {

    private static Lwjgl_OpenGL instance;

    private Lwjgl_OpenGL() {
        instance = this;
    }

    public static Lwjgl_OpenGL instance() {
        return instance != null ? instance : new Lwjgl_OpenGL();
    }

    @Override
    public void glClear(int mask) {
        GL11.glClear(mask);
    }

    @Override
    public void glBegin(int i) {
        GL11.glBegin(i);
    }

    @Override
    public void glEnd() {
        GL11.glEnd();
    }

    @Override
    public void glVertex2f(float x, float y) {
        GL11.glVertex2f(x, y);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        GL11.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void glBufferSubData(int target, long offset, FloatBuffer data) {
        GL15.glBufferSubData(target, offset, data);
    }

    @Override
    public void glBufferSubData(int target, long offset, IntBuffer data) {
        GL15.glBufferSubData(target, offset, data);
    }

    @Override
    public int glGenBuffers() {
        return GL15.glGenBuffers();
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GL15.glBindBuffer(target, buffer);
    }

    @Override
    public void glBufferData(int target, long size, int usage) {
        GL15.glBufferData(target, size, usage);
    }

    @Override
    public void glBufferData(int target, IntBuffer data, int usage) {
        GL15.glBufferData(target, data, usage);
    }

    @Override
    public void glBufferData(int target, ByteBuffer data, int usage) {
        GL15.glBufferData(target, data, usage);
    }

    @Override
    public void glBufferData(int target, FloatBuffer data, int usage) {
        GL15.glBufferData(target, data, usage);
    }

    @Override
    public void glGetBufferSubData(int target, int offset, FloatBuffer data) {
        GL15.glGetBufferSubData(target, offset, data);
    }

    @Override
    public void glGetBufferSubData(int target, int offset, IntBuffer data) {
        GL15.glGetBufferSubData(target, offset, data);
    }

    @Override
    public int glGenVertexArrays() {
        return GL30.glGenVertexArrays();
    }

    @Override
    public void glBindVertexArray(int array) {
        GL30.glBindVertexArray(array);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        GL20.glEnableVertexAttribArray(index);
    }

    @Override
    public void glDeleteBuffers(int buffers) {
        GL15.glDeleteBuffers(buffers);
    }

    @Override
    public void glDeleteVertexArrays(int arrays) {
        GL30.glDeleteVertexArrays(arrays);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, long indices) {
        GL11.glDrawElements(mode, count, type, indices);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        GL11.glDrawArrays(mode, first, count);
    }

    @Override
    public String glGetShaderInfoLog(int id) {
        return GL20.glGetShaderInfoLog(id);
    }

    @Override
    public int glCreateShader(int type) {
        return GL20.glCreateShader(type);
    }

    @Override
    public void glShaderSource(int shader, CharSequence source) {
        GL20.glShaderSource(shader, source);
    }

    @Override
    public void glCompileShader(int shader) {
        GL20.glCompileShader(shader);
    }

    @Override
    public void glDeleteShader(int shader) {
        GL20.glDeleteShader(shader);
    }

    @Override
    public int glGetShaderi(int shader, int pname) {
        return GL20.glGetShaderi(shader, pname);
    }

    @Override
    public void glUniform2fv(int location, FloatBuffer value) {
        GL20.glUniform2fv(location, value);
    }

    @Override
    public void glUniform2f(int location, float v1, float v2) {
        GL20.glUniform2f(location, v1, v2);
    }

    @Override
    public void glUniform4fv(int location, FloatBuffer value) {
        GL20.glUniform4fv(location, value);
    }

    @Override
    public void glUniform1fv(int location, FloatBuffer value) {
        GL20.glUniform1fv(location, value);
    }

    @Override
    public void glUniform1f(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    @Override
    public void glUniform1i(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    @Override
    public int glCreateProgram() {
        return GL20.glCreateProgram();
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GL20.glAttachShader(program, shader);
    }

    @Override
    public void glBindFragDataLocation(int programId, int colorNumber, String name) {
        GL30.glBindFragDataLocation(programId, colorNumber, name);
    }

    @Override
    public void glDetachShader(int program, int shader) {
        GL20.glDetachShader(program, shader);
    }

    @Override
    public void glLinkProgram(int program) {
        GL20.glLinkProgram(program);
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        return GL20.glGetProgramInfoLog(program);
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return GL20.glGetUniformLocation(program, name);
    }

    @Override
    public void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    @Override
    public void glDeleteProgram(int program) {
        GL20.glDeleteProgram(program);
    }

    @Override
    public int glGetAttribLocation(int program, CharSequence name) {
        return GL20.glGetAttribLocation(program, name);
    }

    @Override
    public int glGetUniformLocation(int program, CharSequence name) {
        return GL20.glGetUniformLocation(program, name);
    }

    @Override
    public void glUseProgram(int program) {
        GL20.glUseProgram(program);
    }

    @Override
    public int glGetProgrami(int arg0, int arg1) {
        return GL20.glGetProgrami(arg0, arg1);
    }

    @Override
    public void glDeleteTextures(int arg0) {
        GL11.glDeleteTextures(arg0);
    }

    @Override
    public int glGenTextures() {
        return GL11.glGenTextures();
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        GL11.glPixelStorei(pname, param);
    }

    @Override
    public void glTexImage2D(int glTexture2d, int level, int internalformat, int width, int height, int border,
                             int format, int type, ByteBuffer pixels) {
        GL11.glTexImage2D(glTexture2d, level, internalformat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        GL11.glTexParameteri(target, pname, param);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GL11.glBindTexture(target, texture);
    }

    @Override
    public void glEnable(int target) {
        GL11.glEnable(target);
    }

    @Override
    public void glDisable(int target) {
        GL11.glDisable(target);
    }

    @Override
    public void glMemoryBarrier(int barriers) {
        GL42.glMemoryBarrier(barriers);
    }

    @Override
    public void glDispatchCompute(int numGroupsX, int numGroupsY, int numGroupsZ) {
        GL43.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        GL30.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void glHint(int target, int hint) {
        GL11.glHint(target, hint);
    }

    @Override
    public void glPointSize(float size) {
        GL11.glPointSize(size);
    }

    @Override
    public void glLineWidth(float width) {
        GL11.glLineWidth(width);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void glClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        GL30.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public int glGenFramebuffers() {
        return GL30.glGenFramebuffers();
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        GL30.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glDeleteFramebuffers(int framebuffer) {
        GL30.glDeleteFramebuffers(framebuffer);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        GL20.glReadPixels(x, y, width, height, format, type, pixels);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        GL30.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
    }
}