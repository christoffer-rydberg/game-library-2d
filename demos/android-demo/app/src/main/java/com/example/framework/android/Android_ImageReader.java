package com.example.framework.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.gamelibrary2d.framework.Image;
import com.gamelibrary2d.framework.ImageReader;

import java.io.IOException;
import java.io.InputStream;

public class Android_ImageReader implements ImageReader {
    private final BitmapParser bitmapParser = new BitmapParser();

    @Override
    public Image read(InputStream inputStream) throws IOException {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmapParser.parse(bitmap);
        } catch (Exception e) {
            throw new IOException("Failed to read image", e);
        }
    }
}
