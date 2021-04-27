package com.example.androiddemo;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.sound.SoundPlayer;

public class BallTool implements Renderable, MouseAware {
    private final Line line;
    private final Renderable ballRenderer;
    private final BallCreatedListener ballCreatedListener;
    private final SoundPlayer soundPlayer;

    private int drawButton = -1;

    private BallTool(SoundPlayer soundPlayer, Line line, Renderable ballRenderer, BallCreatedListener ballCreatedListener) {
        this.soundPlayer = soundPlayer;
        this.line = line;
        this.ballRenderer = ballRenderer;
        this.ballCreatedListener = ballCreatedListener;
    }

    public static BallTool create(SoundPlayer soundPlayer, Disposer disposer, Renderable ball, BallCreatedListener ballCreatedListener) {
        return new BallTool(soundPlayer, Line.create(disposer), ball, ballCreatedListener);
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
    public boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (!isDrawing()) {
            drawButton = button;
            line.getStart().set(projectedX, projectedY);
            line.getEnd().set(projectedX, projectedY);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseMove(float x, float y, float projectedX, float projectedY) {
        if (isDrawing()) {
            line.getEnd().set(projectedX, projectedY);
            line.refresh();
            return true;
        }

        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (drawButton == button) {
            drawButton = -1;
            float direction = line.getStart().getDirectionDegrees(line.getEnd());
            float speed = 2 * line.getStart().getDistance(line.getEnd());
            Ball ball = new Ball(ballRenderer, line.getStart().getX(), line.getStart().getY());
            ball.setSpeedAndDirection(speed, direction);
            ballCreatedListener.onBallCreated(ball);
            soundPlayer.play("sounds/cow.ogg", 1f);
        }
    }

    public interface BallCreatedListener {
        void onBallCreated(Ball ball);
    }
}
