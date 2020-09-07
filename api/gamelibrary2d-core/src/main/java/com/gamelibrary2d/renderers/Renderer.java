package com.gamelibrary2d.renderers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;

public interface Renderer extends Bounded, Renderable {

    @Override
    void render(float alpha);

    RenderingParameters getParameters();
}