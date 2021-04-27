package com.gamelibrary2d.framework;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.updating.UpdateAction;

public interface GameLoop {

    void initialize(UpdateAction updateAction, Action disposeAction, Window window);

    void start(Action onExit);

    void stop();
}
