package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.network.ClientObject;

public interface DemoClientObject extends ClientObject {
    void setContent(Renderable content);

    byte getObjectIdentifier();
}
