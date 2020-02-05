package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;

public class FadingCursor<T extends Renderable> extends AbstractCursor<T> implements Updatable {

    private float visibilityDuration = 5f;

    private float defaultOpacity = 1f;

    private float fadeOutTime = 1f;

    private float fadeInTime = 0.5f;

    private float visibilityTimer;

    public FadingCursor(Game game, T content) {
        super(game, content);
    }

    public float getVisibilityDuration() {
        return visibilityDuration;
    }

    public void setVisibilityDuration(float visibilityDuration) {
        this.visibilityDuration = visibilityDuration;
        if (visibilityDuration <= 0) {
            visibilityTimer = -1;
            setOpacity(defaultOpacity);
        }
    }

    protected float getDefaultOpacity() {
        return defaultOpacity;
    }

    protected void setDefaultOpacity(float defaultOpacity) {
        this.defaultOpacity = defaultOpacity;
    }

    public float getFadeOutTime() {
        return fadeOutTime;
    }

    public void setFadeOutTime(float fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    public float getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(float faceInTime) {
        this.fadeInTime = faceInTime;
    }

    private void resetVisibilityTimer() {
        if (visibilityDuration <= 0) {
            visibilityTimer = 0;
        } else if (isFadingOut()) {
            visibilityTimer = visibilityDuration + fadeOutTime
                    + (isFadingOut() ? (1f - getFadeOutFactor()) * fadeInTime : 0);
        }
    }

    @Override
    protected void onInteracted() {
        setOpacity(defaultOpacity);
        resetVisibilityTimer();
    }

    @Override
    public final void update(float deltaTime) {
        if (isEnabled()) {
            onUpdate(deltaTime);
        }
    }

    protected void onUpdate(float deltaTime) {
        if (visibilityTimer > 0) {
            visibilityTimer -= deltaTime;
            if (isHidden()) {
                setOpacity(0);
                visibilityTimer = 0;
            } else if (isFadingIn()) {
                setOpacity(defaultOpacity * getFadeInFactor());
            } else if (isFadingOut()) {
                setOpacity(defaultOpacity * getFadeOutFactor());
            }
        }
    }

    private boolean isHidden() {
        return visibilityTimer <= 0;
    }

    private boolean isFadingIn() {
        return visibilityTimer > visibilityDuration;
    }

    private boolean isFadingOut() {
        return visibilityTimer < fadeOutTime;
    }

    private float getFadeInFactor() {
        return 1f - (visibilityTimer - visibilityDuration - fadeOutTime) / fadeInTime;
    }

    private float getFadeOutFactor() {
        return 1f - (fadeOutTime - visibilityTimer) / fadeOutTime;
    }
}