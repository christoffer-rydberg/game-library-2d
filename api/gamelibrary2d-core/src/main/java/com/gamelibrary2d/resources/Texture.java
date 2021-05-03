package com.gamelibrary2d.resources;

import com.gamelibrary2d.framework.Image;

public interface Texture {

    int getId();

    float getWidth();

    float getHeight();

    Image loadImage();

    default void bind() {
        TextureUtil.bind(getId());
    }

    default void unbind() {
        TextureUtil.unbind(getId());
    }

}
