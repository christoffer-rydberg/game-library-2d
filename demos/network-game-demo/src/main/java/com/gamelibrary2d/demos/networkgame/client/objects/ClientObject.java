package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.GameObject;

public interface ClientObject extends GameObject {

    int getId();

    byte getObjectIdentifier();

    void setContent(Renderable content);

    void setGoalPosition(float x, float y);

    void setGoalRotation(float rotation);

    void setUpdateAction(UpdateAction action);

    interface UpdateAction {
        void invoke(ClientObject obj, float deltaTime);
    }
}


