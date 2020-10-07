package com.gamelibrary2d.resources;

public interface Texture {

    int getId();

    float getWidth();

    float getHeight();

    default void bind() {
        TextureUtil.bind(getId());
    }

    default void unbind() {
        TextureUtil.unbind(getId());
    }

}
