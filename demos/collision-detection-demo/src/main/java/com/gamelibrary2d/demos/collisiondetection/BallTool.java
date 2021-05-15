package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.PointerAware;

public class BallTool implements Renderable, PointerAware {
    private final Line line;
    private final Renderable ballRenderer;
    private final BallCreatedListener ballCreatedListener;

    private int pointerId = -1;
    private int pointerButton = -1;

    private BallTool(Line line, Renderable ballRenderer, BallCreatedListener ballCreatedListener) {
        this.line = line;
        this.ballRenderer = ballRenderer;
        this.ballCreatedListener = ballCreatedListener;
    }

    public static BallTool create(Disposer disposer, Renderable ball, BallCreatedListener ballCreatedListener) {
        return new BallTool(Line.create(disposer), ball, ballCreatedListener);
    }

    private boolean isDrawing() {
        return pointerId != -1;
    }

    @Override
    public void render(float alpha) {
        if (isDrawing()) {
            line.render(alpha);
            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().translatef(
                    line.getStart().getX(),
                    line.getStart().getY(),
                    0f);
            ballRenderer.render(alpha);
            ModelMatrix.instance().popMatrix();
        }
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (!isDrawing()) {
            pointerId = id;
            pointerButton = button;
            line.getStart().set(projectedX, projectedY);
            line.getEnd().set(projectedX, projectedY);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (isDrawing()) {
            line.getEnd().set(projectedX, projectedY);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id && pointerButton == button) {
            pointerId = -1;
            pointerButton = -1;
            float direction = line.getStart().getDirectionDegrees(line.getEnd());
            float speed = 2 * line.getStart().getDistance(line.getEnd());
            Ball ball = new Ball(ballRenderer, line.getStart().getX(), line.getStart().getY());
            ball.setSpeedAndDirection(speed, direction);
            ballCreatedListener.onBallCreated(ball);
        }
    }

    public interface BallCreatedListener {
        void onBallCreated(Ball ball);
    }
}
