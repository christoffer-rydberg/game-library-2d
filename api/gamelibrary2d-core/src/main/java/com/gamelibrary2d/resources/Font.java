package com.gamelibrary2d.resources;

import com.gamelibrary2d.glUtil.ShaderProgram;

public interface Font {

    int getAscent();

    int getDescent();

    int getHeight();

    float getTextWidth(String text, int offset, int len);

    void render(ShaderProgram shaderProgram, String text, int start, int end);
}
