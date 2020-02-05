package com.gamelibrary2d.renderers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;

public interface Renderer extends Bounded, Renderable {

    float getSetting(int index);

    void updateSettings(int index, float f);

    void updateSettings(int index, float f1, float f2);

    void updateSettings(int index, float f1, float f2, float f3);

    void updateSettings(int index, float f1, float f2, float f3, float f4);

    void updateSettings(int index, float[] settings, int offset, int length);

    @Override
    void render(float alpha);
}