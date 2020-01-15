package com.gamelibrary2d.network;

import com.gamelibrary2d.common.updating.Updatable;
import com.gamelibrary2d.objects.GameObject;

public interface ClientObject extends GameObject, Updatable {

    int getId();

    void setGoalPosition(float x, float y);

    void setGoalRotation(float rotation);
}