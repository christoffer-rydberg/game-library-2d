package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;

public interface Message {
    void serializeMessage(DataBuffer buffer);
}