package com.gamelibrary2d.renderers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Bounded;

public interface Renderer extends Bounded, Renderable {

    @Override
    void render(float alpha);

    ShaderParameters getParameters();
}