package com.gamelibrary2d.demos.geometry;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.renderers.LineRenderer;
import com.gamelibrary2d.glUtil.PositionBuffer;

public class DemoFrame extends AbstractFrame {

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void initializeFrame(FrameInitializer initializer) {
        var lineLayer = new BasicLayer<Geometry>();

        var lineRenderer = new LineRenderer(4f);
        
        var lineTool = new GeometryTool(
                () -> new Geometry(PositionBuffer.create(this), lineRenderer),
                10f);

        lineTool.addGeometryCreatedListener(lineLayer::add);

        add(lineLayer);
        add(lineTool);
    }
}