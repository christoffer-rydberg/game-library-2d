package com.gamelibrary2d.network.common.client;

import java.util.concurrent.Future;

public interface Reconnectable {
    Future<Void> reconnect();
}