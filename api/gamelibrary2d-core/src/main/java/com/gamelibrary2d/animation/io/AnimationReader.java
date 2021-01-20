package com.gamelibrary2d.animation.io;

import java.io.IOException;
import java.io.InputStream;

public interface AnimationReader {
    AnimationMetadata read(InputStream stream) throws IOException;
}
