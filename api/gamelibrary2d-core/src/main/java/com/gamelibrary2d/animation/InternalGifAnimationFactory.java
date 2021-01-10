package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.gif.DisposalMethod;
import org.apache.commons.imaging.formats.gif.GifImageMetadata;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class InternalGifAnimationFactory {
    private static AnimationFrame createAnimationFrame(Quad quad, Texture texture, float duration, DisposalMethod disposalMethod) {
        switch (disposalMethod) {
            case DO_NOT_DISPOSE:
                return new AnimationFrame(quad, texture, duration, false, true);
            case RESTORE_TO_BACKGROUND:
                return new AnimationFrame(quad, texture, duration, true, false);
            case UNSPECIFIED:
            case RESTORE_TO_PREVIOUS:
            case TO_BE_DEFINED_1:
            case TO_BE_DEFINED_2:
            case TO_BE_DEFINED_3:
            case TO_BE_DEFINED_4:
            default:
                return new AnimationFrame(quad, texture, duration, false, false);
        }
    }

    private static Animation createHelper(List<BufferedImage> images, GifImageMetadata metadata,
                                          Rectangle scale, Disposer disposer) {
        int frameCount = images.size();
        var animationFrames = new ArrayList<AnimationFrame>(frameCount);

        var scaleX = scale.getWidth();
        var scaleY = scale.getHeight();

        var imageWidth = metadata.getWidth();
        var imageHeight = metadata.getHeight();

        boolean backgroundRenderingRequired = false;
        for (int i = 0; i < frameCount; ++i) {
            BufferedImage frameImage = images.get(i);
            var metadataItem = metadata.getItems().get(i);

            Texture frameTexture = DefaultTexture.create(frameImage, disposer);
            float frameWidth = frameTexture.getWidth();
            float frameHeight = frameTexture.getHeight();

            var xOffset = metadataItem.getLeftPosition();
            var yOffset = imageHeight - frameHeight - metadataItem.getTopPosition();
            var frameBounds = new Rectangle(0, 0, frameWidth * scaleX, frameHeight * scaleY);
            frameBounds = frameBounds.move(imageWidth * scale.getLowerX(), imageHeight * scale.getLowerY());
            frameBounds = frameBounds.move(xOffset * scaleX, yOffset * scaleY);

            // Ignore disposal method for last frame if no other frame require background buffer rendering
            var disposalMethod = i == frameCount - 1 && !backgroundRenderingRequired ? DisposalMethod.UNSPECIFIED : metadataItem.getDisposalMethod();

            var frame = createAnimationFrame(Quad.create(frameBounds, disposer), frameTexture, metadataItem.getDelay() / 100f, disposalMethod);
            backgroundRenderingRequired |= frame.getRenderToBackgroundHint();
            animationFrames.add(frame);
        }

        return new Animation(animationFrames, scale.resize(imageWidth, imageHeight), imageWidth, imageHeight);
    }

    static Animation create(byte[] bytes, Func<Point, Rectangle> resizeFunc, Disposer disposer)
            throws IOException, ImageReadException {
        var images = Imaging.getAllBufferedImages(bytes);
        var metadata = (GifImageMetadata) Imaging.getMetadata(bytes);
        var scale = resizeFunc.invoke(new Point(metadata.getWidth(), metadata.getHeight()));
        return createHelper(images, metadata, scale, disposer);
    }
}
