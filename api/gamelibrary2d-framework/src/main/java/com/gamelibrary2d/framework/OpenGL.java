package com.gamelibrary2d.framework;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface OpenGL {
    int GL_TRUE = 1;
    int GL_TRIANGLES = 4;
    int GL_POINTS = 0;
    int GL_LINE_STRIP = 3;
    int GL_UNSIGNED_BYTE = 5121;
    int GL_FLOAT = 5126;
    int GL_ARRAY_BUFFER = 34962;
    int GL_ELEMENT_ARRAY_BUFFER = 34963;
    int GL_STATIC_DRAW = 35044;
    int GL_DYNAMIC_DRAW = 35048;
    int GL_VERTEX_SHADER = 35633;
    int GL_FRAGMENT_SHADER = 35632;
    int GL_COMPUTE_SHADER = 37305;
    int GL_GEOMETRY_SHADER = 36313;
    int GL_COMPILE_STATUS = 35713;
    int GL_LINK_STATUS = 35714;
    int GL_TEXTURE_2D = 3553;
    int GL_PACK_ALIGNMENT = 3333;
    int GL_UNPACK_ALIGNMENT = 3317;
    int GL_RGBA8 = 32856;
    int GL_TEXTURE_WRAP_S = 10242;
    int GL_TEXTURE_WRAP_T = 10243;

    int GL_CLAMP_TO_EDGE = 33071;
    int GL_TEXTURE_MIN_FILTER = 10241;
    int GL_TEXTURE_MAG_FILTER = 10240;
    int GL_NEAREST = 9728;
    int GL_LINEAR = 9729;
    int GL_RGBA = 6408;
    int GL_RED = 6403;
    int GL_R8 = 33321;
    int GL_DEPTH_TEST = 2929;

    int GL_ATOMIC_COUNTER_BUFFER = 37568;
    int GL_SHADER_STORAGE_BUFFER = 37074;

    int GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT = 1;
    int GL_SHADER_STORAGE_BARRIER_BIT = 8192;
    int GL_POINT_SMOOTH = 2832;
    int GL_POINT_SMOOTH_HINT = 3153;

    int GL_FASTEST = 4353;
    int GL_NICEST = 4354;

    int GL_BLEND = 3042;
    int GL_ZERO = 0;
    int GL_ONE = 1;
    int GL_SRC_ALPHA = 770;
    int GL_ONE_MINUS_SRC_ALPHA = 771;
    int GL_COLOR_BUFFER_BIT = 16384;
    int GL_RENDERBUFFER = 36161;
    int GL_RGB = 6407;
    int GL_RGB8 = 32849;
    int GL_FRAMEBUFFER = 36160;
    int GL_COLOR_ATTACHMENT0 = 36064;
    int GL_QUADS = 7;

    static OpenGL instance() {
        return Runtime.getFramework().getOpenGL();
    }

    void glBlendFunc(int sfactor, int dfactor);

    int glGenBuffers();

    void glClear(int mask);

    void glBufferSubData(int target, long offset, FloatBuffer data);

    void glBufferSubData(int target, long offset, IntBuffer data);

    void glBindBuffer(int target, int buffer);

    void glBufferData(int target, long size, int usage);

    void glBufferData(int target, IntBuffer data, int usage);

    void glBufferData(int target, ByteBuffer data, int usage);

    void glBufferData(int target, FloatBuffer data, int usage);

    void glGetBufferSubData(int target, int offset, FloatBuffer data);

    void glGetBufferSubData(int target, int offset, IntBuffer data);

    int glGenVertexArrays();

    void glBindVertexArray(int array);

    void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer);

    void glEnableVertexAttribArray(int index);

    void glDeleteBuffers(int buffers);

    void glDeleteVertexArrays(int arrays);

    void glDrawElements(int mode, int count, int type, long indices);

    void glDrawArrays(int mode, int first, int count);

    String glGetShaderInfoLog(int id);

    int glCreateShader(int type);

    void glShaderSource(int shader, CharSequence source);

    void glCompileShader(int shader);

    void glDeleteShader(int shader);

    int glGetShaderi(int shader, int pname);

    void glUniform2f(int location, float v1, float v2);

    void glUniform2fv(int location, FloatBuffer buffer);

    void glUniform4fv(int location, FloatBuffer buffer);

    void glUniform1fv(int location, FloatBuffer buffer);

    void glUniform1f(int location, float value);

    void glUniform1i(int location, int value);

    int glCreateProgram();

    void glAttachShader(int program, int shader);

    void glDetachShader(int program, int shader);

    void glLinkProgram(int program);

    String glGetProgramInfoLog(int program);

    int glGetUniformLocation(int program, String name);

    void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value);

    void glDeleteProgram(int program);

    int glGetAttribLocation(int program, CharSequence name);

    int glGetUniformLocation(int program, CharSequence name);

    void glUseProgram(int program);

    int glGetProgrami(int arg0, int arg1);

    void glDeleteTextures(int arg0);

    int glGenTextures();

    void glPixelStorei(int pname, int param);

    void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format,
                      int type, ByteBuffer pixels);

    void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels);

    void glTexParameteri(int target, int pname, int param);

    void glBindTexture(int target, int texture);

    void glEnable(int target);

    void glDisable(int target);

    void glMemoryBarrier(int barriers);

    void glDispatchCompute(int numGroupsX, int numGroupsY, int numGroupsZ);

    void glBindBufferBase(int target, int index, int buffer);

    void glHint(int target, int hint);

    void glPointSize(float size);

    void glLineWidth(float width);

    void glViewport(int x, int y, int width, int height);

    void glClearColor(float r, float g, float b, float a);

    void glBindRenderbuffer(int target, int renderbuffer);

    int glGenFramebuffers();

    void glBindFramebuffer(int target, int framebuffer);

    void glDeleteFramebuffers(int framebuffer);

    void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels);

    void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level);

    void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha);

    OpenGLVersion getSupportedVersion();

    enum OpenGLVersion {
        OPENGL_ES_3,
        OPENGL_ES_3_1,
        OPENGL_ES_3_2,
        OPENGL_CORE_430
    }
}