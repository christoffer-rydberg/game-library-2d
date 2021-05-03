package com.example.androiddemo;

import android.content.res.AssetManager;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AndroidResourceManager implements ResourceManager {
    private final AssetManager assets;

    public AndroidResourceManager(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    public InputStream open(String resource) throws IOException {
        return assets.open(resource);
    }

    @Override
    public List<String> list(String path) throws IOException {
        String[] fileNames = assets.list(path);
        ArrayList<String> result = new ArrayList<>(fileNames.length);
        for (String fileName : fileNames) {
            result.add(Paths.get(path, fileName).toString());
        }

        return result;
    }
}
