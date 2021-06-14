package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.DurationEffect;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.InstantEffect;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.objects.GameObject;

public interface ClientObject extends GameObject {

    int getId();

    byte getPrimaryType();

    byte getSecondaryType();

    Point getParticleHotspot();

    void setGoalPosition(float x, float y);

    void setGoalRotation(float rotation);

    void setGoalDirection(float direction);

    float getDirection();

    void setContent(Renderable content);

    void setUpdateEffect(DurationEffect updateEffect);

    void setDestroyedEffect(InstantEffect destroyedEffect);

    void destroy();

    void setAccelerating(boolean accelerating);
}


