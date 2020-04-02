package com.gamelibrary2d.demos.geometry;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.renderers.LineRenderer;

public class DemoFrame extends AbstractFrame {

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void onInitialize() {
        var lineLayer = new BasicLayer<Geometry>();

        var lineRenderer = new LineRenderer(4f);

        var lineTool = new GeometryTool(
                () -> new Geometry(PositionBuffer.create(this), lineRenderer),
                10f);

        lineTool.addGeometryCreatedListener(lineLayer::add);

        add(lineLayer);
        add(lineTool);
    }

    @Override
    protected void onLoad(LoadingContext context) {

    }

    @Override
    protected void onLoaded(LoadingContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}