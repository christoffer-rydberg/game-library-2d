package com.gamelibrary2d.imaging;

import java.io.IOException;
import java.io.InputStream;

public interface ImageReader {
    Image read(InputStream stream) throws IOException;
}
