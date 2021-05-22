package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.resources.Texture;

public interface FrameBuffer extends Disposable {
    Texture getTexture();

    int getId();

    int readPixel(int x, int y);

    boolean isVisible(int x, int y);

    void clear();

    default int bind() {
        return OpenGLState.bindFrameBuffer(getId());
    }

    default void unbind() {
        OpenGLState.unbindFrameBuffer(getId());
    }
}