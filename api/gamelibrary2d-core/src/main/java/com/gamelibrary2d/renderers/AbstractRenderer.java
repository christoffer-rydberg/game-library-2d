package com.gamelibrary2d.renderers;

public abstract class AbstractRenderer implements Renderer {
    private final RenderingParameters parameters;

    protected AbstractRenderer() {
        parameters = new RenderingParameters();
    }

    protected AbstractRenderer(RenderingParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public RenderingParameters getParameters() {
        return parameters;
    }
}