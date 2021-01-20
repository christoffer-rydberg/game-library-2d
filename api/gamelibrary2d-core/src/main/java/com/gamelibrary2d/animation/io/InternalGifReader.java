package com.gamelibrary2d.animation.io;

import com.gamelibrary2d.common.Rectangle;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.gif.DisposalMethod;
import org.apache.commons.imaging.formats.gif.GifImageMetadata;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class InternalGifReader implements AnimationReader {
    private static final Rectangle IMAGE_COORDINATES = new Rectangle(0, 0, 1, 1);

    private static AnimationFrameMetadata createAnimationFrame(BufferedImage img, float offsetX, float offsetY, float duration, DisposalMethod disposalMethod) {
        switch (disposalMethod) {
            case DO_NOT_DISPOSE:
                return new AnimationFrameMetadata(img, IMAGE_COORDINATES, offsetX, offsetY, duration, false, true);
            case RESTORE_TO_BACKGROUND:
                return new AnimationFrameMetadata(img, IMAGE_COORDINATES, offsetX, offsetY, duration, true, false);
            case UNSPECIFIED:
            case RESTORE_TO_PREVIOUS:
            case TO_BE_DEFINED_1:
            case TO_BE_DEFINED_2:
            case TO_BE_DEFINED_3:
            case TO_BE_DEFINED_4:
            default:
                return new AnimationFrameMetadata(img, IMAGE_COORDINATES, offsetX, offsetY, duration, false, false);
        }
    }

    private static AnimationMetadata loadInternal(InputStream stream) throws IOException, ImageReadException {
        var bytes = stream.readAllBytes();
        var images = Imaging.getAllBufferedImages(bytes);
        var metadata = (GifImageMetadata) Imaging.getMetadata(bytes);

        int frameCount = images.size();
        var animationFrames = new ArrayList<AnimationFrameMetadata>(frameCount);

        var backgroundRenderingRequired = false;
        for (int i = 0; i < frameCount; ++i) {
            var frameImage = images.get(i);
            var metadataItem = metadata.getItems().get(i);

            var xOffset = metadataItem.getLeftPosition();
            var yOffset = metadata.getHeight() - frameImage.getHeight() - metadataItem.getTopPosition();

            var disposalMethod = i == frameCount - 1 && !backgroundRenderingRequired
                    ? DisposalMethod.UNSPECIFIED
                    : metadataItem.getDisposalMethod();

            var frame = createAnimationFrame(
                    frameImage,
                    xOffset,
                    yOffset,
                    metadataItem.getDelay() / 100f,
                    disposalMethod);

            animationFrames.add(frame);

            backgroundRenderingRequired |= frame.getRenderToBackgroundHint();
        }

        return new AnimationMetadata(animationFrames);
    }

    public AnimationMetadata read(InputStream stream) throws IOException {
        try {
            return loadInternal(stream);
        } catch (ImageReadException e) {
            throw new IOException("Failed to read gif", e);
        }
    }
}
