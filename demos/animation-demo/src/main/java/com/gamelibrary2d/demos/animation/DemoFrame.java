package com.gamelibrary2d.demos.animation;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.imaging.AnimationLoader;
import com.gamelibrary2d.imaging.ImageAnimation;
import com.gamelibrary2d.imaging.StandardAnimationFormats;
import com.gamelibrary2d.objects.AnimatedGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Animation;
import com.gamelibrary2d.resources.DefaultFont;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.util.io.FileChooser;
import com.gamelibrary2d.util.io.FileSelectionMode;
import com.gamelibrary2d.widgets.DefaultWidget;
import com.gamelibrary2d.widgets.Label;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class DemoFrame extends AbstractFrame {
    private static final float DEFAULT_FRAME_DURATION = 0.05f;

    private final Game game;
    private final Disposer animationDisposer = new DefaultDisposer(this);
    private Future<ImageAnimation> loadingAnimation;
    private AnimatedGameObject<AnimationRenderer> animatedObject;

    DemoFrame(Game game) {
        this.game = game;
    }

    private GameObject createLoadButton() {
        Font font = DefaultFont.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 48), this);

        Label label = new Label();
        label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        label.setTextRenderer(new TextRenderer(font));
        label.setColor(Color.WHITE);
        label.setText("Click here to load an animation");

        DefaultWidget<Label> loadButton = new DefaultWidget<>();
        loadButton.setContent(label);
        loadButton.setBounds(label.calculateBounds());
        loadButton.addMouseButtonReleasedListener(this::onLoadButtonClicked);

        return loadButton;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        final float windowWidth = game.getWindow().getWidth();
        final float windowHeight = game.getWindow().getHeight();

        GameObject loadButton = createLoadButton();
        loadButton.setPosition(windowWidth / 2, windowHeight - windowHeight / 6);

        animatedObject = new AnimatedGameObject<>();
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

    private ImageAnimation loadAnimation(File file) throws IOException {
        if (file.isDirectory()) {
            return AnimationLoader.load(
                    file.toPath(),
                    Pattern.compile("^*.(png|jpe?g)$"),
                    DEFAULT_FRAME_DURATION);
        } else {
            return AnimationLoader.load(file.toURI().toURL(), StandardAnimationFormats.GIF);
        }
    }

    private Future<ImageAnimation> loadAnimationAsync(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadAnimation(file);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    private Future<ImageAnimation> selectAnimation() throws IOException {
        FileChooser fileChooser = new FileChooser(System.getenv("TEMP") + "/animation_demo/file_chooser_path.txt");
        File file = fileChooser.browse(FileSelectionMode.FILES_AND_DIRECTORIES);
        return file != null ? loadAnimationAsync(file) : null;
    }

    private void onException(Exception e) {
        e.printStackTrace();
        game.exit();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (loadingAnimation != null) {
            if (loadingAnimation.isDone()) {
                try {
                    animationDisposer.dispose();

                    ImageAnimation imageAnimation = loadingAnimation.get();

                    Animation animation = Animation.fromImageAnimation(
                            imageAnimation,
                            imageAnimation.getBounds()
                                    .resize(Rectangle.create(1f, 1f))
                                    .restrict(game.getWindow().getWidth(), game.getWindow().getHeight()),
                            animationDisposer);

                    animatedObject.setContent(new AnimationRenderer(animation, true, animationDisposer));

                    loadingAnimation = null;
                } catch (InterruptedException | ExecutionException e) {
                    onException(e);
                }
            } else if (loadingAnimation.isCancelled()) {
                loadingAnimation = null;
            }
        }

        super.onUpdate(deltaTime);
    }

    private void onLoadButtonClicked(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (loadingAnimation == null) {
            try {
                loadingAnimation = selectAnimation();
            } catch (IOException e) {
                onException(e);
            }
        }
    }
}
