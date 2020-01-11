package com.gamelibrary2d.updates;

import com.gamelibrary2d.common.Point;

/**
 * Represents a game object that can be updated by an {@link AttributeUpdate}.
 *
 * @author Christoffer Rydberg
 */
public interface UpdateObject {

    Point getPosition();

    Point getScale();

    float getOpacity();

    void setOpacity(float alpha);

    float getRotation();

    void setRotation(float rotation);
}