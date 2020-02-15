package com.gamelibrary2d.demos.splitscreen;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.objects.BasicObject;
import com.gamelibrary2d.renderers.QuadArrayRenderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.PositionArray;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.splitscreen.*;
import com.gamelibrary2d.util.QuadShape;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DemoFrame extends AbstractFrame {
    private final static Rectangle GAME_BOUNDS = new Rectangle(0, 0, 4000, 4000);
    private final static float SPLIT_LAYOUT_MARGIN = 1f;
    private final static Color SPLIT_COLOR = Color.WHITE;
    private final static Color BACKGROUND_COLOR = Color.BLACK;
    private final static int SPACE_CRAFT_COUNT = 3;

    private final DynamicLayer<Renderable> viewLayer;

    DemoFrame(Game game) {
        super(game);
        viewLayer = new DynamicLayer<>();
    }

    private List<SpaceCraft> createSpaceCrafts() throws IOException {
        var random = RandomInstance.get();
        var spaceCrafts = new ArrayList<SpaceCraft>(SPACE_CRAFT_COUNT);
        var spaceCraftQuad = Quad.create(Rectangle.centered(64, 64), this);
        var spaceCraftTexture = Texture.create(
                getClass().getClassLoader().getResource("spacecraft.png"),
                this);
        for (int i = 0; i < SPACE_CRAFT_COUNT; ++i) {
            var renderer = new SurfaceRenderer(spaceCraftQuad, spaceCraftTexture);
            renderer.updateSettings(RenderSettings.COLOR_R,
                    random.nextFloat() * 0.5f + 0.5f,
                    random.nextFloat() * 0.5f + 0.5f,
                    random.nextFloat() * 0.5f + 0.5f);
            var spaceCraft = new SpaceCraft(GAME_BOUNDS, renderer);
            spaceCraft.position().set(
                    GAME_BOUNDS.xMin() + random.nextFloat() * GAME_BOUNDS.width(),
                    GAME_BOUNDS.yMin() + random.nextFloat() * GAME_BOUNDS.height());
            spaceCrafts.add(spaceCraft);
        }
        return spaceCrafts;
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

        var starPositions = PositionArray.create(positions, this);
        var starsRenderer = new QuadArrayRenderer(Rectangle.centered(8f, 8f));
        starsRenderer.setShape(QuadShape.RADIAL_GRADIENT);
        starsRenderer.setColor(Color.LIGHT_YELLOW);

        return new BasicObject<>(a -> starsRenderer.render(a, starPositions));
    }

    private void prepareView(SpaceCraft spaceCraft, Rectangle viewArea) {
        float x = spaceCraft.position().getX();
        float y = spaceCraft.position().getY();
        x = Math.min(GAME_BOUNDS.width() - viewArea.width() / 2, Math.max(x, viewArea.width() / 2));
        y = Math.min(GAME_BOUNDS.height() - viewArea.height() / 2, Math.max(y, viewArea.height() / 2));
        viewLayer.position().set(viewArea.width() / 2 - x, viewArea.height() / 2 - y);
    }
    
    private SplitLayout createSplitLayoutHelper(List<SpaceCraft> spaceCrafts, SplitOrientation orientation) {
        var size = spaceCrafts.size();

        if (size == 1) {
            var spaceCraft = spaceCrafts.get(0);
            viewLayer.position().set(spaceCraft.position());
            return new SplitLayoutLeaf<>(viewLayer, this::prepareView, spaceCraft);
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

    @Override
    protected void onPrepare() {
        try {
            var window = getGame().getWindow();
            viewLayer.getBackground().add(createBackground());

            List<SpaceCraft> spaceCrafts = createSpaceCrafts();
            for (var spaceCraft : spaceCrafts) {
                viewLayer.add(spaceCraft);
            }

            var viewArea = Rectangle.fromBottomLeft(window.getWidth(), window.getHeight());
            var splitLayer = new SplitLayer<>(createSplitLayout(spaceCrafts), viewArea, this);
            splitLayer.setTarget(viewLayer);
            add(splitLayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBegin() {
        getGame().setBackgroundColor(SPLIT_COLOR);
    }

    @Override
    public void onEnd() {

    }
}
