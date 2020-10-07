package com.gamelibrary2d.demos.geometry;

import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.renderers.LineRenderer;

public class DemoFrame extends AbstractFrame {

    @Override
    protected void onInitialize(InitializationContext context) {
        var lineLayer = new BasicLayer<>();

        var lineRenderer = new LineRenderer(4f);

        var drawingTool = new DrawingTool(
                () -> new Geometry(PositionBuffer.create(this), lineRenderer),
                10f);

        drawingTool.addGeometryCreatedListener(lineLayer::add);

        add(lineLayer);
        add(drawingTool);
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