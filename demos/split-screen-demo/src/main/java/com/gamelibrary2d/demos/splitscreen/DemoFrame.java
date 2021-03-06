package com.gamelibrary2d.demos.splitscreen;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomGenerator;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.components.frames.AbstractFrame;
import com.gamelibrary2d.components.frames.InitializationContext;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.components.containers.BasicLayer;
import com.gamelibrary2d.components.containers.DefaultLayerObject;
import com.gamelibrary2d.components.containers.Layer;
import com.gamelibrary2d.components.denotations.KeyAware;
import com.gamelibrary2d.components.objects.DefaultGameObject;
import com.gamelibrary2d.components.objects.GameObject;
import com.gamelibrary2d.renderers.QuadsRenderer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.splitscreen.*;
import com.gamelibrary2d.resources.QuadShape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DemoFrame extends AbstractFrame implements KeyAware {
    private final static Rectangle GAME_BOUNDS = new Rectangle(0, 0, 4000, 4000);
    private final static float SPLIT_LAYOUT_MARGIN = 1f;
    private final static Color SPLIT_COLOR = Color.WHITE;
    private final static Color BACKGROUND_COLOR = Color.BLACK;
    private final static int INITIAL_SPACECRAFT_COUNT = 5;
    private final DefaultLayerObject<Renderable> spacecraftLayer;
    private final Game game;

    private GameObject view;
    private Quad spaceCraftQuad;
    private Texture spaceCraftTexture;
    private List<SpaceCraft> spaceCrafts;

    DemoFrame(Game game) {
        this.game = game;
        spacecraftLayer = new DefaultLayerObject<>();
    }

    private SpaceCraft createSpaceCraft(Quad quad, Texture texture) {
        RandomGenerator random = RandomInstance.get();
        Renderer renderer = new SurfaceRenderer<>(quad, texture);
        renderer.getParameters().setColor(
                random.nextFloat() * 0.5f + 0.5f,
                random.nextFloat() * 0.5f + 0.5f,
                random.nextFloat() * 0.5f + 0.5f);

        SpaceCraft spaceCraft = new SpaceCraft(GAME_BOUNDS, renderer);
        spaceCraft.setPosition(
                GAME_BOUNDS.getLowerX() + random.nextFloat() * GAME_BOUNDS.getWidth(),
                GAME_BOUNDS.getLowerY() + random.nextFloat() * GAME_BOUNDS.getHeight());

        return spaceCraft;
    }

    private Renderable createStars(int count) {
        RandomGenerator random = RandomInstance.get();
        float[] positions = new float[count * 2];
        for (int i = 0; i < count; ++i) {
            float x = GAME_BOUNDS.getLowerX() + random.nextFloat() * GAME_BOUNDS.getWidth();
            float y = GAME_BOUNDS.getLowerY() + random.nextFloat() * GAME_BOUNDS.getHeight();
            int index = i * 2;
            positions[index] = x;
            positions[index + 1] = y;
        }

        PositionBuffer starPositions = PositionBuffer.create(positions, this);
        QuadsRenderer starsRenderer = new QuadsRenderer(Rectangle.create(8f, 8f));
        starsRenderer.setShape(QuadShape.RADIAL_GRADIENT);
        starsRenderer.getParameters().setColor(Color.LIGHT_YELLOW);

        return a -> starsRenderer.render(a, starPositions, 0, starPositions.getCapacity());
    }

    private void prepareView(SpaceCraft spaceCraft, Rectangle viewArea) {
        float x = Math.min(
                GAME_BOUNDS.getWidth() - viewArea.getWidth() / 2,
                Math.max(spaceCraft.getPosition().getX(), viewArea.getWidth() / 2));
        float y = Math.min(
                GAME_BOUNDS.getHeight() - viewArea.getHeight() / 2,
                Math.max(spaceCraft.getPosition().getY(), viewArea.getHeight() / 2));
        spacecraftLayer.setPosition(viewArea.getWidth() / 2 - x, viewArea.getHeight() / 2 - y);
    }

    private SplitLayout createSplitLayoutHelper(List<SpaceCraft> spaceCrafts, SplitOrientation orientation) {
        int size = spaceCrafts.size();

        if (size == 1) {
            SpaceCraft spaceCraft = spaceCrafts.get(0);
            spacecraftLayer.setPosition(spaceCraft.getPosition());
            return new SplitLayoutLeaf<>(spacecraftLayer, this::prepareView, spaceCraft, this);
        }

        SplitOrientation flippedOrientation = orientation == SplitOrientation.HORIZONTAL
                ? SplitOrientation.VERTICAL
                : SplitOrientation.HORIZONTAL;

        SplitLayout layout1 = createSplitLayoutHelper(spaceCrafts.subList(0, size / 2), flippedOrientation);
        SplitLayout layout2 = createSplitLayoutHelper(spaceCrafts.subList(size / 2, size), flippedOrientation);

        SplitLayoutBranch layout = new SplitLayoutBranch(SPLIT_LAYOUT_MARGIN, orientation);
        layout.getLayouts().add(layout1);
        layout.getLayouts().add(layout2);

        return layout;
    }

    private SplitLayout createSplitLayout(List<SpaceCraft> spaceCrafts) {
        return createSplitLayoutHelper(spaceCrafts, SplitOrientation.HORIZONTAL);
    }

    private DefaultGameObject createBackgroundColor() {
        Surface quad = Quad.create(GAME_BOUNDS, this);
        Renderer renderer = new SurfaceRenderer<>(quad);
        renderer.getParameters().setColor(
                BACKGROUND_COLOR.getR(),
                BACKGROUND_COLOR.getG(),
                BACKGROUND_COLOR.getB(),
                BACKGROUND_COLOR.getA());

        return new DefaultGameObject<>(renderer);
    }

    private Renderable createBackground() {
        Layer<Renderable> backgroundLayer = new BasicLayer<>();
        backgroundLayer.setAutoClearing(false);
        backgroundLayer.add(createBackgroundColor());
        backgroundLayer.add(createStars(Math.round(GAME_BOUNDS.getArea() * 0.0001f)));
        return backgroundLayer;
    }

    private void refreshSplitLayout(List<SpaceCraft> spaceCrafts) {
        Window window = game.getWindow();
        if (view != null) {
            remove(view);
        }

        if (spaceCrafts.size() > 0) {
            Rectangle viewArea = new Rectangle(0, 0, window.getWidth(), window.getHeight());
            SplitLayer<GameObject> splitLayer = new SplitLayer<>(createSplitLayout(spaceCrafts), viewArea);
            splitLayer.setTarget(spacecraftLayer);
            add(splitLayer);
            this.view = splitLayer;
        } else {
            add(spacecraftLayer);
            this.view = spacecraftLayer;
        }
    }

    private void addSpaceCraft() {
        SpaceCraft spaceCraft = createSpaceCraft(spaceCraftQuad, spaceCraftTexture);
        spaceCrafts.add(spaceCraft);
        spacecraftLayer.add(spaceCraft);
    }

    private void removeSpaceCraft() {
        if (spaceCrafts.size() > 0) {
            SpaceCraft spaceCraft = spaceCrafts.remove(spaceCrafts.size() - 1);
            spacecraftLayer.remove(spaceCraft);
        }
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        try {
            spacecraftLayer.getBackground().add(createBackground());

            spaceCraftQuad = Quad.create(Rectangle.create(64, 64), this);

            spaceCraftTexture = DefaultTexture.create(
                    getClass().getResource("/spacecraft.png"),
                    this);

            spaceCrafts = new ArrayList<>();
            for (int i = 0; i < INITIAL_SPACECRAFT_COUNT; ++i) {
                addSpaceCraft();
            }

            refreshSplitLayout(spaceCrafts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {
        game.setBackgroundColor(SPLIT_COLOR);
    }

    @Override
    protected void onEnd() {

    }

    @Override
    public void keyDown(int key, boolean repeat) {
        if (key == Keyboard.instance().keyUp()) {
            addSpaceCraft();
            refreshSplitLayout(spaceCrafts);
        } else if (key == Keyboard.instance().keyDown()) {
            removeSpaceCraft();
            refreshSplitLayout(spaceCrafts);
        }
    }

    @Override
    public void keyUp(int key) {

    }
}
