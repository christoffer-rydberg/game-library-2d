package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.glUtil.ShaderProgram;

public interface Surface extends Disposable {

    Rectangle getBounds();

    void render(ShaderProgram shaderProgram);
}