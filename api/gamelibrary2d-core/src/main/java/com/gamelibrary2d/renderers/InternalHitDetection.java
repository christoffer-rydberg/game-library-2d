package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;

class InternalHitDetection {

    // TODO: Only works if surface is a Quad that covers the entire texture.
    public static boolean isVisible(Surface surface, Texture texture, float x, float y) {
        if (surface == null || !surface.getBounds().isInside(x, y))
            return false;

        if (texture == null)
            return true;

        Rectangle bounds = surface.getBounds();
        float deltaX = bounds.getXMin();
        float deltaY = bounds.getYMin();
        int pixelX = (int) (x - deltaX);
        int pixelY = (int) (y - deltaY);
        float factorX = texture.getImageWidth() / bounds.getWidth();
        float factorY = texture.getImageHeight() / bounds.getHeight();
        int transformedPixelX = (int) (factorX * (float) pixelX);
        int transformedPixelY = (int) (factorY * (float) pixelY);
        return texture.isVisible(transformedPixelX, transformedPixelY);
    }
}
