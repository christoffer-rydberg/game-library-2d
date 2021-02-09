package com.gamelibrary2d.imaging;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.framework.Image;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.gif.DisposalMethod;
import org.apache.commons.imaging.formats.gif.GifImageMetadata;
import org.apache.commons.imaging.formats.gif.GifImageMetadataItem;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class InternalGifReader implements AnimationReader {
    private static final Rectangle IMAGE_COORDINATES = new Rectangle(0, 0, 1, 1);

    private static ImageAnimationFrame createAnimationFrame(Image img, float offsetX, float offsetY, float duration, DisposalMethod disposalMethod) {
        switch (disposalMethod) {
            case DO_NOT_DISPOSE:
                return new ImageAnimationFrame(img, IMAGE_COORDINATES, offsetX, offsetY, duration, false, true);
            case RESTORE_TO_BACKGROUND:
                return new ImageAnimationFrame(img, IMAGE_COORDINATES, offsetX, offsetY, duration, true, false);
            case UNSPECIFIED:
            case RESTORE_TO_PREVIOUS:
            case TO_BE_DEFINED_1:
            case TO_BE_DEFINED_2:
            case TO_BE_DEFINED_3:
            case TO_BE_DEFINED_4:
            default:
                return new ImageAnimationFrame(img, IMAGE_COORDINATES, offsetX, offsetY, duration, false, false);
        }
    }

    private static ImageAnimation loadInternal(InputStream stream) throws IOException, ImageReadException {
        byte[] bytes = Read.byteArray(stream);
        List<BufferedImage> images = Imaging.getAllBufferedImages(bytes);
        GifImageMetadata metadata = (GifImageMetadata) Imaging.getMetadata(bytes);

        int frameCount = images.size();
        List<ImageAnimationFrame> animationFrames = new ArrayList<>(frameCount);

        BufferedImageParser bufferedImageParser = new BufferedImageParser();

        boolean backgroundRenderingRequired = false;
        for (int i = 0; i < frameCount; ++i) {
            BufferedImage frameImage = images.get(i);
            GifImageMetadataItem metadataItem = metadata.getItems().get(i);

            int xOffset = metadataItem.getLeftPosition();
            int yOffset = metadata.getHeight() - frameImage.getHeight() - metadataItem.getTopPosition();

            DisposalMethod disposalMethod = i == frameCount - 1 && !backgroundRenderingRequired
                    ? DisposalMethod.UNSPECIFIED
                    : metadataItem.getDisposalMethod();

            ImageAnimationFrame frame = createAnimationFrame(
                    bufferedImageParser.parse(frameImage),
                    xOffset,
                    yOffset,
                    metadataItem.getDelay() / 100f,
                    disposalMethod);

            animationFrames.add(frame);

            backgroundRenderingRequired |= frame.getRenderToBackgroundHint();
        }

        return new ImageAnimation(animationFrames);
    }

    public ImageAnimation read(InputStream stream) throws IOException {
        try {
            return loadInternal(stream);
        } catch (ImageReadException e) {
            throw new IOException("Failed to read gif", e);
        }
    }
}
