package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;

import java.io.IOException;

public interface DataReader {

    void read(DataBuffer buffer) throws IOException;

}
