package com.gamelibrary2d.framework;

import java.io.IOException;
import java.io.InputStream;

public interface ImageReader {
    Image read(InputStream stream) throws IOException;
}
