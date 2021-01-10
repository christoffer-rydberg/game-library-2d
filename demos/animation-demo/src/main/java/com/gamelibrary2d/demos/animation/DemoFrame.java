package com.gamelibrary2d.demos.animation;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.*;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.DefaultFont;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.util.io.FileChooser;
import com.gamelibrary2d.util.io.FileSelectionMode;
import com.gamelibrary2d.widgets.DefaultWidget;
import com.gamelibrary2d.widgets.Label;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class DemoFrame extends AbstractFrame {
    private static float DEFAULT_FRAME_DURATION = 0.05f;

    private final Game game;
    private AnimatedObject<AnimationRenderer> animatedObject;

    DemoFrame(Game game) {
        this.game = game;
    }

    private static List<Path> getFilePaths(Path folderPath, Pattern pattern) throws IOException {
        var filePaths = new ArrayList<Path>();
        try (var stream = Files.walk(folderPath, 1)) {
            for (var itr = stream.iterator(); itr.hasNext(); ) {
                var filePath = itr.next();
                if (pattern.matcher(filePath.toString()).find()) {
                    filePaths.add(filePath);
                }
            }
        }

        filePaths.sort(Comparator.comparing(a -> a.getFileName().toString()));

        return filePaths;
    }

    private static List<BufferedImage> loadImages(List<Path> filePaths) throws IOException {
        var images = new ArrayList<BufferedImage>(filePaths.size());
        for (var filePath : filePaths) {
            images.add(ImageIO.read(filePath.toUri().toURL()));
        }

        return images;
    }

    private static Rectangle getAnimationBounds(List<BufferedImage> images) {
        float width = 0, height = 0;

        for (var img : images) {
            width = Math.max(width, img.getWidth());
            height = Math.max(height, img.getHeight());
        }

        return Rectangle.create(width, height);
    }

    private static Animation loadAnimation(
            Path folderPath,
            Pattern pattern,
            float frameDuration,
            Rectangle scale,
            Point sizeConstraints,
            Disposer disposer) throws IOException {

        var images = loadImages(getFilePaths(folderPath, pattern));
        var bounds = getAnimationBounds(images);

        var frames = new ArrayList<AnimationFrame>(images.size());
        for (var img : images) {
            // TODO: Use scale and constraints to determine bounds.
            var frameBounds = Rectangle.create(img.getWidth(), img.getHeight());
            var texture = DefaultTexture.create(img, disposer);
            var surface = Quad.create(frameBounds, disposer);
            frames.add(new AnimationFrame(surface, texture, frameDuration));
        }

        return new Animation(frames, bounds);
    }

    private GameObject createLoadButton() {
        var font = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 48), this);

        var label = new Label();
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        label.setTextRenderer(new TextRenderer(font));
        label.setColor(Color.WHITE);
        label.setText("Click here to load an animation");

        var loadButton = new DefaultWidget<>();
        loadButton.setContent(label);
        loadButton.setBounds(label.calculateBounds());
        loadButton.addMouseButtonReleasedListener(this::onLoadButtonClicked);

        return loadButton;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        final float windowWidth = game.getWindow().getWidth();
        final float windowHeight = game.getWindow().getHeight();

        var loadButton = createLoadButton();
        loadButton.setPosition(windowWidth / 2, windowHeight - windowHeight / 6);

        animatedObject = new AnimatedObject<>();
        animatedObject.setPosition(windowWidth / 2, windowHeight / 2);

        add(animatedObject);
        add(loadButton);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private Animation loadAnimation(File file, Rectangle scale, Point sizeConstraints, Disposer disposer) throws IOException {
        if (file.isDirectory()) {
            return loadAnimation(
                    file.toPath(),
                    Pattern.compile("^*.(png|jpe?g)$"),
                    DEFAULT_FRAME_DURATION,
                    scale,
                    sizeConstraints,
                    disposer);
        } else {
            return AnimationFactory.create(
                    file.toURI().toURL(),
                    AnimationFormats.GIF,
                    scale,
                    sizeConstraints,
                    disposer);
        }
    }

    private void selectAnimation() throws IOException {
        var fileChooser = new FileChooser(System.getenv("TEMP") + "/animation_demo/file_chooser_path.txt");
        var file = fileChooser.browse(FileSelectionMode.FILES_AND_DIRECTORIES);
        if (file != null) {
            var disposer = new DefaultDisposer(this);

            var scale = Rectangle.create(1f, 1f);

            var sizeConstraints = new Point(
                    game.getWindow().getWidth(),
                    game.getWindow().getHeight());

            var animation = loadAnimation(file, scale, sizeConstraints, disposer);

            animatedObject.setRenderer(
                    new AnimationRenderer(animation, true, disposer)
            );
        }
    }

    private void onLoadButtonClicked(int button, int mods, float x, float y, float projectedX, float projectedY) {
        try {
            selectAnimation();
        } catch (IOException e) {
            e.printStackTrace();
            game.exit();
        }
    }
}
