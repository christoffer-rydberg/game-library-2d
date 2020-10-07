package com.gamelibrary2d.lightning;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.renderers.ShaderParameters;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.DefaultShader;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.TextureUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DefaultLightRenderer implements LightRenderer {

    private static final int MAXIMUM_LIGHT = 127;

    private static final int PARAMETER_ALPHA_MAP_COLS = ShaderParameters.MIN_LENGTH;

    private static final int PARAMETER_ALPHA_MAP_ROWS = ShaderParameters.MIN_LENGTH + 1;

    private final Point position = new Point();

    private final List<LightMap> lightMaps = new ArrayList<>();

    private final int windowWidth;

    private final int windowHeight;

    private int textureId;
    private SurfaceRenderer renderer;
    private ByteBuffer transparencyTexture;
    private int alphaMapCols;
    private int alphaMapRows;

    private boolean prepared;
    private int colOffset;
    private int colSpan;
    private int rowOffset;
    private int rowSpan;
    private float gameCellWidth;
    private float gameCellHeight;
    private int gameColSpan;
    private int gameRowSpan;

    /**
     * Disposes obsolete objects when the resolution changes.
     */
    private DefaultDisposer resolutionChangedDisposer;

    private DefaultLightRenderer(int windowWidth, int windowHeight, Disposer disposer) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        resolutionChangedDisposer = new DefaultDisposer(disposer);

        ShaderProgram shaderProgram = ShaderProgram.create(disposer);
        shaderProgram.attachShader(DefaultShader.fromFile("Shaders/LightMap.vertex", ShaderType.VERTEX, disposer));
        shaderProgram.attachShader(DefaultShader.fromFile("Shaders/LightMap.fragment", ShaderType.FRAGMENT, disposer));
        shaderProgram.bindFragDataLocation(0, "fragColor"); // Optional since the fragment shader only have one "out"
        // variable
        shaderProgram.initialize();
        shaderProgram.initializeMvp(windowWidth, windowHeight);
        shaderProgram.bind();

        // Generate and bind a texture id for the alpha map
        textureId = OpenGL.instance().glGenTextures();
        TextureUtil.bind(textureId);

        // Setup the ST coordinate system
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_S, OpenGL.GL_CLAMP_TO_EDGE);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_WRAP_T, OpenGL.GL_CLAMP_TO_EDGE);

        // Setup what to do when the texture has to be scaled
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MIN_FILTER, OpenGL.GL_NEAREST);
        OpenGL.instance().glTexParameteri(OpenGL.GL_TEXTURE_2D, OpenGL.GL_TEXTURE_MAG_FILTER, OpenGL.GL_NEAREST);

        var parameters = new ShaderParameters(new float[ShaderParameters.MIN_LENGTH + 2]);
        parameters.setRgba(0, 0, 0, 1f);
        renderer = new SurfaceRenderer(parameters);
        renderer.setShaderProgram(shaderProgram);
    }

    public static DefaultLightRenderer create(int windowWidth, int windowHeight, Disposer disposer) {
        return new DefaultLightRenderer(windowWidth, windowHeight, disposer);
    }

    public void clearLightMaps() {
        lightMaps.clear();
    }

    @Override
    public void addLightMap(LightMap lightMap) {
        lightMaps.add(lightMap);
    }

    @Override
    public void removeLightMap(LightMap lightMap) {
        lightMaps.remove(lightMap);
    }

    public int getCols() {
        return alphaMapCols;
    }

    public int getRows() {
        return alphaMapRows;
    }

    private void clearLightTexture() {
        for (int i = 0; i < transparencyTexture.limit(); ++i)
            transparencyTexture.put(i, (byte) 0);
        transparencyTexture.clear();
    }

    public void prepare(float gameCellWidth, float gameCellHeight) {
        prepare(gameCellWidth, gameCellHeight, 0, 0);
    }

    public void prepare(float gameCellWidth, float gameCellHeight, int colOffset, int rowOffset) {
        var colSpan = (int) Math.ceil(windowWidth / gameCellWidth);
        var rowSpan = (int) Math.ceil(windowHeight / gameCellHeight);
        prepare(gameCellWidth, gameCellHeight, colOffset, colSpan, rowOffset, rowSpan);
    }

    public void prepare(float gameCellWidth, float gameCellHeight, int colOffset, int colSpan, int rowOffset,
                        int rowSpan) {
        this.colOffset = colOffset;
        this.colSpan = colSpan;
        this.rowOffset = rowOffset;
        this.rowSpan = rowSpan;
        if (this.gameCellWidth != gameCellWidth || this.gameCellHeight != gameCellHeight || this.gameColSpan != colSpan
                || this.gameRowSpan != rowSpan) {
            reallocate(gameCellWidth, gameCellHeight, colSpan, rowSpan);
        } else {
            clearLightTexture();
        }

        position.set(colOffset * gameCellWidth, rowOffset * gameCellHeight);

        for (LightMap lightMap : lightMaps) {
            lightMap.prepare(alphaMapCols, alphaMapRows, colOffset, colSpan, rowOffset, rowSpan);
        }

        prepared = true;
    }

    public void reset() {
        if (!prepared) {
            throw new IllegalStateException("Must call prepare before reset");
        }
        clearLightTexture();
        for (LightMap lightMap : lightMaps) {
            lightMap.prepare(alphaMapCols, alphaMapRows, colOffset, colSpan, rowOffset, rowSpan);
        }
    }

    public void apply() {
        for (LightMap lightMap : lightMaps) {
            lightMap.apply(this);
        }
    }

    private void reallocate(float gameCellWidth, float gameCellHeight, int gameColSpan, int gameRowSpan) {
        final int frequency = 2;

        this.gameCellWidth = gameCellWidth;
        this.gameCellHeight = gameCellHeight;
        this.gameColSpan = gameColSpan;
        this.gameRowSpan = gameRowSpan;

        alphaMapCols = frequency * gameColSpan + 1;
        alphaMapRows = frequency * gameRowSpan + 1;

        // Dispose old resources.
        resolutionChangedDisposer.dispose();

        transparencyTexture = BufferUtils.createByteBuffer((alphaMapCols + 3) * (alphaMapRows + 3));

        renderer.setSurface(Quad.create(new Rectangle(0, 0, alphaMapCols * (gameCellWidth / frequency),
                alphaMapRows * (gameCellHeight / frequency)), resolutionChangedDisposer));
    }

    public void render(float alpha) {
        render(position, alpha);
    }

    private void render(Point position, float alpha) {
        // Bind alpha map texture
        TextureUtil.bind(textureId);

        // Upload texture data
        transparencyTexture.position(transparencyTexture.limit());
        transparencyTexture.flip();
        OpenGL.instance().glTexImage2D(OpenGL.GL_TEXTURE_2D, 0, OpenGL.GL_R8, alphaMapCols, alphaMapRows, 0,
                OpenGL.GL_RED, OpenGL.GL_UNSIGNED_BYTE, transparencyTexture);

        // Render
        ModelMatrix.instance().translatef(position.getX(), position.getY(), 0);
        renderer.getParameters().set(PARAMETER_ALPHA_MAP_COLS, alphaMapCols);
        renderer.getParameters().set(PARAMETER_ALPHA_MAP_ROWS, alphaMapRows);
        renderer.render(alpha);
    }

    public void addLight(int index, float light) {
        int previous = transparencyTexture.get(index) & 0xFF;
        transparencyTexture.put(index, (byte) Math.min(previous + getTransparencyValue(light), 255));
    }

    private float getTransparencyValue(float light) {
        return (light / MAXIMUM_LIGHT) * 255f;
    }
}