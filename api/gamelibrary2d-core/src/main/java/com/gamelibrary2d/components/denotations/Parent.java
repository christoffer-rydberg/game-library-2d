package com.gamelibrary2d.components.denotations;

import java.util.List;

public interface Parent<T> {
    List<T> getChildren();
}