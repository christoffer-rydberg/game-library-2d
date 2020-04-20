package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.GameObject;

public interface ClientObject extends GameObject {

    int getId();

    byte getObjectIdentifier();

    void setContent(Renderable content);

    void setGoalPosition(float x, float y);

    void setGoalDirection(float direction);

    void setUpdateAction(Updatable updateAction);
}


