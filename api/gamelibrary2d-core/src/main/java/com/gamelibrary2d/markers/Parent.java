package com.gamelibrary2d.markers;

import java.util.List;

public interface Parent<T> {
    List<T> getChildren();
}