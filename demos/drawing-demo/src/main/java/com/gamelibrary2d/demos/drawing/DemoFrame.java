package com.gamelibrary2d.demos.drawing;

import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.renderers.LineRenderer;
import com.gamelibrary2d.renderers.ShaderParameters;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;

public class DemoFrame extends AbstractFrame {

    @Override
    protected void onInitialize(InitializationContext context) {
        var objLayer = new BasicLayer<>();

        var lineTool = new GeometryTool(
                Mouse.instance().mouseButton1(),
                () -> new Geometry(PositionBuffer.create(this), new LineRenderer(4f)),
                4f);

        lineTool.addGeometryCreatedListener(line -> {
            randomizeColor(line.getRenderer().getParameters());
            objLayer.add(line);
        });

        var quadTool = new QuadTool(
                Mouse.instance().mouseButton2(),
                r -> Quad.create(r, this));

        quadTool.addQuadCreatedListener(quad -> {
            var renderer = new SurfaceRenderer(quad);
            randomizeColor(renderer.getParameters());
            objLayer.add(renderer);
        });

        add(objLayer);
        add(lineTool);
        add(quadTool);
    }

    private void randomizeColor(ShaderParameters params) {
        var random = RandomInstance.get();
        params.setColor(
                random.nextFloat(),
                random.nextFloat(),
                random.nextFloat()
        );

    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}