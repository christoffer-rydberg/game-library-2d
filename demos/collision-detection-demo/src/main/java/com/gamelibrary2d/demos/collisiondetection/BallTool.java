package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.MouseAware;

public class BallTool implements Renderable, MouseAware {
    private final Line line;
    private final Renderable ballRenderer;
    private final BallCreatedListener ballCreatedListener;

    private int drawButton = -1;

    private BallTool(Line line, Renderable ballRenderer, BallCreatedListener ballCreatedListener) {
        this.line = line;
        this.ballRenderer = ballRenderer;
        this.ballCreatedListener = ballCreatedListener;
    }

    public static BallTool create(Disposer disposer, Renderable ball, BallCreatedListener ballCreatedListener) {
        return new BallTool(Line.create(disposer), ball, ballCreatedListener);
    }

    private boolean isDrawing() {
        return drawButton != -1;
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
    public boolean mouseButtonDown(int button, int mods, float x, float y) {
        if (!isDrawing()) {
            drawButton = button;
            line.getStart().set(x, y);
            line.getEnd().set(x, y);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseMove(float x, float y) {
        if (isDrawing()) {
            line.getEnd().set(x, y);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y) {
        if (drawButton == button) {
            drawButton = -1;
            var direction = line.getStart().getDirectionDegrees(line.getEnd());
            var speed = 2 * line.getStart().getDistance(line.getEnd());
            var ball = new Ball(ballRenderer, line.getStart().getX(), line.getStart().getY());
            ball.setSpeedAndDirection(speed, direction);
            ballCreatedListener.onBallCreated(ball);
        }
    }

    public interface BallCreatedListener {
        void onBallCreated(Ball ball);
    }
}
