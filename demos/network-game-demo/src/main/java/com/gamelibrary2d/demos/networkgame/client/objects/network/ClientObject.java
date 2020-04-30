package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.GameObject;

public interface ClientObject extends GameObject {

    int getId();

    byte getObjectIdentifier();

    Point getParticleHotspot();

    void setGoalPosition(float x, float y);

    void setGoalDirection(float direction);

    float getDirection();

    void setContent(Renderable content);

    void setUpdateAction(Updatable updateAction);

    void setDestroyAction(ParameterizedAction<ClientObject> destroyAction);

    void destroy();
}


