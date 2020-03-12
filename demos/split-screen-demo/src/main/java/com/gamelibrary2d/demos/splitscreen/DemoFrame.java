package com.gamelibrary2d.demos.splitscreen;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.QuadsRenderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.splitscreen.*;
import com.gamelibrary2d.util.QuadShape;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DemoFrame extends AbstractFrame implements KeyAware {
    private final static Rectangle GAME_BOUNDS = new Rectangle(0, 0, 4000, 4000);
    private final static float SPLIT_LAYOUT_MARGIN = 1f;
    private final static Color SPLIT_COLOR = Color.WHITE;
    private final static Color BACKGROUND_COLOR = Color.BLACK;
    private final static int INITIAL_SPACECRAFT_COUNT = 5;
    private final DynamicLayer<Renderable> spacecraftLayer;

    private GameObject view;
    private Quad spaceCraftQuad;
    private Texture spaceCraftTexture;
    private List<SpaceCraft> spaceCrafts;

    DemoFrame(Game game) {
        super(game);
        spacecraftLayer = new DynamicLayer<>();
    }

    private SpaceCraft createSpaceCraft(Quad quad, Texture texture) {
        var random = RandomInstance.get();
        var renderer = new SurfaceRenderer(quad, texture);
        renderer.updateSettings(RenderSettings.COLOR_R,
                random.nextFloat() * 0.5f + 0.5f,
                random.nextFloat() * 0.5f + 0.5f,
                random.nextFloat() * 0.5f + 0.5f);

        var spaceCraft = new SpaceCraft(GAME_BOUNDS, renderer);
        spaceCraft.position().set(
                GAME_BOUNDS.xMin() + random.nextFloat() * GAME_BOUNDS.width(),
                GAME_BOUNDS.yMin() + random.nextFloat() * GAME_BOUNDS.height());

        return spaceCraft;
    }

    private BasicObject<Renderable> createStars(int count) {
        var random = RandomInstance.get();
        float[] positions = new float[count * 2];
        for (int i = 0; i < count; ++i) {
            var x = GAME_BOUNDS.xMin() + random.nextFloat() * GAME_BOUNDS.width();
            var y = GAME_BOUNDS.yMin() + random.nextFloat() * GAME_BOUNDS.height();
            var index = i * 2;
            positions[index] = x;
            positions[index + 1] = y;
        }

        var starPositions = PositionBuffer.create(positions, this);
        var starsRenderer = new QuadsRenderer(Rectangle.centered(8f, 8f));
        starsRenderer.setShape(QuadShape.RADIAL_GRADIENT);
        starsRenderer.setColor(Color.LIGHT_YELLOW);

        return new BasicObject<>(a -> starsRenderer.render(a, starPositions, 0, starPositions.capacity()));
    }

    private void prepareView(SpaceCraft spaceCraft, Rectangle viewArea) {
        var x = Math.min(
                GAME_BOUNDS.width() - viewArea.width() / 2,
                Math.max(spaceCraft.position().getX(), viewArea.width() / 2));
        var y = Math.min(
                GAME_BOUNDS.height() - viewArea.height() / 2,
                Math.max(spaceCraft.position().getY(), viewArea.height() / 2));
        spacecraftLayer.position().set(viewArea.width() / 2 - x, viewArea.height() / 2 - y);
    }

    private SplitLayout createSplitLayoutHelper(List<SpaceCraft> spaceCrafts, SplitOrientation orientation) {
        var size = spaceCrafts.size();

        if (size == 1) {
            var spaceCraft = spaceCrafts.get(0);
            spacecraftLayer.position().set(spaceCraft.position());
            return new SplitLayoutLeaf<>(spacecraftLayer, this::prepareView, spaceCraft);
        }

        var flippedOrientation = orientation == SplitOrientation.HORIZONTAL
                ? SplitOrientation.VERTICAL
                : SplitOrientation.HORIZONTAL;

        var layout1 = createSplitLayoutHelper(spaceCrafts.subList(0, size / 2), flippedOrientation);
        var layout2 = createSplitLayoutHelper(spaceCrafts.subList(size / 2, size), flippedOrientation);

        var layout = new SplitLayoutBranch(SPLIT_LAYOUT_MARGIN, orientation);
        layout.getLayouts().add(layout1);
        layout.getLayouts().add(layout2);

        return layout;
    }

    private SplitLayout createSplitLayout(List<SpaceCraft> spaceCrafts) {
        return createSplitLayoutHelper(spaceCrafts, SplitOrientation.HORIZONTAL);
    }

    private BasicObject createBackgroundColor() {
        var quad = Quad.create(GAME_BOUNDS, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(
                RenderSettings.COLOR_R,
                BACKGROUND_COLOR.getR(),
                BACKGROUND_COLOR.getG(),
                BACKGROUND_COLOR.getB(),
                BACKGROUND_COLOR.getA());
        return new BasicObject<>(renderer);
    }

    private Renderable createBackground() {
        var backgroundLayer = new BasicLayer<>();
        backgroundLayer.setAutoClearing(false);
        backgroundLayer.add(createBackgroundColor());
        backgroundLayer.add(createStars(Math.round(GAME_BOUNDS.area() * 0.0001f)));
        return backgroundLayer;
    }

    private void refreshSplitLayout(List<SpaceCraft> spaceCrafts) {
        var window = game().window();
        if (view != null) {
            remove(view);
        }

        if (spaceCrafts.size() > 0) {
            var viewArea = Rectangle.fromBottomLeft(window.width(), window.height());
            var splitLayer = new SplitLayer<>(createSplitLayout(spaceCrafts), viewArea, this);
            splitLayer.setTarget(spacecraftLayer);
            add(splitLayer);
            this.view = splitLayer;
        } else {
            add(spacecraftLayer);
            this.view = spacecraftLayer;
        }
    }

    private void addSpaceCraft() {
        var spaceCraft = createSpaceCraft(spaceCraftQuad, spaceCraftTexture);
        spaceCrafts.add(spaceCraft);
        spacecraftLayer.add(spaceCraft);
    }

    private void removeSpaceCraft() {
        if (spaceCrafts.size() > 0) {
            var spaceCraft = spaceCrafts.remove(spaceCrafts.size() - 1);
            spacecraftLayer.remove(spaceCraft);
        }
    }

    @Override
    protected void initializeFrame(FrameInitializer initializer) {
        try {
            spacecraftLayer.getBackground().add(createBackground());

            spaceCraftQuad = Quad.create(Rectangle.centered(64, 64), this);

            spaceCraftTexture = Texture.create(
                    getClass().getClassLoader().getResource("spacecraft.png"),
                    this);

            spaceCrafts = new ArrayList<>();
            for (int i = 0; i < INITIAL_SPACECRAFT_COUNT; ++i) {
                addSpaceCraft();
            }

            refreshSplitLayout(spaceCrafts);

            initializer.onBegin(() -> game().setBackgroundColor(SPLIT_COLOR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCharInput(char charInput) {

    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        if (key == Keyboard.instance().keyUp()) {
            addSpaceCraft();
            refreshSplitLayout(spaceCrafts);
        } else if (key == Keyboard.instance().keyDown()) {
            removeSpaceCraft();
            refreshSplitLayout(spaceCrafts);
        }
    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {

    }
}
