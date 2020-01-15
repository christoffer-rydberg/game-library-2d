package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.functional.ParameterizedAction;

import java.util.concurrent.Future;

public interface Connectable {

    Future<Void> connect();

    void connect(Action onSuccess, ParameterizedAction<Throwable> onFail);
}