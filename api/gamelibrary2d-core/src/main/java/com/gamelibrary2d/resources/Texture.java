package com.gamelibrary2d.resources;

import com.gamelibrary2d.framework.Image;
import com.gamelibrary2d.glUtil.OpenGLState;

public interface Texture {

    int getId();

    float getWidth();

    float getHeight();

    Image loadImage();

    default int bind() {
        return OpenGLState.bindTexture(getId());
    }

    default void unbind() {
        OpenGLState.unbindTexture(getId());
    }
}
