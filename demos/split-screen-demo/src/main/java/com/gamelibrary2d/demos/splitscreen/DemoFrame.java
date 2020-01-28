package com.gamelibrary2d.demos.splitscreen;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.*;
import com.gamelibrary2d.objects.ArrayObject;
import com.gamelibrary2d.objects.BasicGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.QuadArrayRenderer;
import com.gamelibrary2d.renderers.QuadShape;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.rendering.RenderSettings;
import com.gamelibrary2d.resources.PositionArray;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.splitscreen.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

class DemoFrame extends AbstractFrame {
    private final Rectangle gameBounds = new Rectangle(0, 0, 4000, 4000);
    private Renderable background;
    private SplitLayer<GameObject> splitLayer;
    private DynamicLayer<Renderable> viewLayer;
    private ArrayList<SpaceCraft> spaceCrafts;

    DemoFrame(Game game) {
        super(game);
    }

    private URL getUrl(String resource) {
        return getClass().getClassLoader().getResource(resource);
    }

    private ArrayList<SpaceCraft> createSpaceCrafts(int count) throws IOException {
        var random = RandomInstance.get();
        var spaceCrafts = new ArrayList<SpaceCraft>(count);
        var spaceCraftQuad = Quad.create(Rectangle.centered(64, 64), this);
        var spaceCraftTexture = Texture.create(getUrl("spacecraft.png"), this);
        for (int i = 0; i < count; ++i) {
            var renderer = new SurfaceRenderer(spaceCraftQuad, spaceCraftTexture);
            renderer.updateSettings(RenderSettings.COLOR_R,
                    random.nextFloat() * 0.5f + 0.5f,
                    random.nextFloat() * 0.5f + 0.5f,
                    random.nextFloat() * 0.5f + 0.5f);
            var spaceCraft = new SpaceCraft(gameBounds, renderer);
            spaceCraft.getPosition().set(
                    gameBounds.getXMin() + random.nextFloat() * gameBounds.getWidth(),
                    gameBounds.getYMin() + random.nextFloat() * gameBounds.getHeight());
            spaceCrafts.add(spaceCraft);
        }
        return spaceCrafts;
    }

    private ArrayObject<PositionArray> createStars(int count) {
        var random = RandomInstance.get();
        float[] positions = new float[count * 2];
        for (int i = 0; i < count; ++i) {
            var x = gameBounds.getXMin() + random.nextFloat() * gameBounds.getWidth();
            var y = gameBounds.getYMin() + random.nextFloat() * gameBounds.getHeight();
            var index = i * 2;
            positions[index] = x;
            positions[index + 1] = y;
        }

        var starPositions = PositionArray.create(positions, this);
        var starsRenderer = new QuadArrayRenderer(Rectangle.centered(8f, 8f));
        starsRenderer.setShape(QuadShape.RADIAL_GRADIENT);
        starsRenderer.setColor(Color.LIGHT_YELLOW);
        return new ArrayObject<>(starPositions, starsRenderer);
    }

    private SplitLayout createSplitLayout(float margin) {
        var root = new SplitLayoutBranch(margin, SplitOrientation.HORIZONTAL);

        var leftSide = new SplitLayoutBranch(margin, SplitOrientation.VERTICAL);
        leftSide.getLayouts().add(createSplitLayoutForSpaceCraft(0));
        leftSide.getLayouts().add(createSplitLayoutForSpaceCraft(1));

        var rightSide = createSplitLayoutForSpaceCraft(2);

        root.getLayouts().add(leftSide);
        root.getLayouts().add(rightSide);

        return root;
    }

    private void prepareView(SpaceCraft spaceCraft, Rectangle viewArea) {
        float x = spaceCraft.getPosition().getX();
        float y = spaceCraft.getPosition().getY();
        x = Math.min(gameBounds.getWidth() - viewArea.getWidth() / 2, Math.max(x, viewArea.getWidth() / 2));
        y = Math.min(gameBounds.getHeight() - viewArea.getHeight() / 2, Math.max(y, viewArea.getHeight() / 2));
        viewLayer.getPosition().set(viewArea.getWidth() / 2 - x, viewArea.getHeight() / 2 - y);
    }

    private SplitLayout createSplitLayoutForSpaceCraft(int spaceCraftId) {
        var spaceCraft = spaceCrafts.get(spaceCraftId);
        viewLayer.getPosition().set(spaceCraft.getPosition());
        return new SplitLayoutLeaf<>(viewLayer, this::prepareView, spaceCraft);
    }

    private BasicGameObject createBackgroundColor(Color color) {
        var quad = Quad.create(gameBounds, this);
        var renderer = new SurfaceRenderer(quad);
        renderer.updateSettings(RenderSettings.COLOR_R, color.getR(), color.getG(), color.getB(), color.getA());
        return new BasicGameObject(renderer);
    }

    private Renderable createBackground() {
        var backgroundLayer = new BasicLayer<>();
        backgroundLayer.setAutoClearing(false);
        backgroundLayer.add(createBackgroundColor(Color.BLACK));
        backgroundLayer.add(createStars(Math.round(gameBounds.getArea() * 0.0001f)));
        return backgroundLayer;
    }

    @Override
    protected void onPrepare() {
        try {
            var window = getGame().getWindow();
            viewLayer = new DynamicLayer<>();
            background = createBackground();
            spaceCrafts = createSpaceCrafts(3);
            var viewArea = Rectangle.fromBottomLeft(window.getWidth(), window.getHeight());
            splitLayer = new SplitLayer<>(createSplitLayout(1f), viewArea, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad() {
        viewLayer.getBackground().add(background);
        for (var spaceCraft : spaceCrafts)
            viewLayer.add(spaceCraft);
        splitLayer.setTarget(viewLayer);
        add(splitLayer);
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBegin() {
        getGame().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onEnd() {

    }

    @Override
    protected void onReset() {

    }

    @Override
    protected void onDispose() {

    }
}
