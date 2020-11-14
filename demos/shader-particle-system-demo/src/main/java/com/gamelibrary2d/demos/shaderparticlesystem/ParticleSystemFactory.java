package com.gamelibrary2d.demos.shaderparticlesystem;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleShape;
import com.gamelibrary2d.particle.systems.CustomParticleSystem;
import com.gamelibrary2d.resources.DefaultShader;
import com.gamelibrary2d.util.BlendMode;
import com.gamelibrary2d.util.PointSmoothing;

import java.awt.image.BufferedImage;

class ParticleSystemFactory {
    private final static float DISTANCE_FACTOR = 50;
    private final static float PARTICLE_DURATION = 4f;
    private final static float PARTICLE_DURATION_VAR = 1f;
    private final static float DELAY_BETWEEN_CYCLES = 1f;
    private final static float CYCLE_TIME = PARTICLE_DURATION + PARTICLE_DURATION_VAR + DELAY_BETWEEN_CYCLES;

    static CustomParticleSystem create(BufferedImage img, BufferedImage goal,
                                       float posX, float posY, Disposer disposer) {
        float[] pos = createPositionArray(
                posX - img.getWidth() / 2f,
                posY - img.getHeight() / 2f,
                img.getWidth(),
                img.getHeight());

        float[] initialColors = createColorArray(img);
        float[] goalColors = createColorArray(goal);

        float[] state = createStateArray(pos, initialColors);

        float[] update = createUpdateArray(
                img.getWidth() * img.getHeight(),
                pos,
                initialColors,
                goalColors);

        ShaderProgram updateProgram = ShaderProgram.create(disposer);
        updateProgram.attachShader(DefaultShader.fromFile("shaders/ParticleUpdater.compute", ShaderType.COMPUTE, disposer));
        updateProgram.initialize();
        setUniforms(updateProgram);

        EfficientParticleRenderer renderer = new EfficientParticleRenderer();
        renderer.setParticleShape(ParticleShape.POINT);
        renderer.setBlendMode(BlendMode.NONE);
        renderer.setPointSmoothing(PointSmoothing.NONE);
        renderer.setBounds(new Rectangle(0, 0, 1f, 1f));

        return CustomParticleSystem.create(state, update, 8, updateProgram, renderer, disposer);
    }

    private static void setUniforms(ShaderProgram computeProgram) {
        OpenGL openGL = OpenGL.instance();
        computeProgram.bind();
        int uniformCycleTime = computeProgram.getUniformLocation("cycleTime");
        int uniformDistanceFactor = computeProgram.getUniformLocation("distanceFactor");
        openGL.glUniform1f(uniformCycleTime, CYCLE_TIME);
        openGL.glUniform1f(uniformDistanceFactor, DISTANCE_FACTOR);
        computeProgram.unbind();
    }

    private static float[] createPositionArray(float x, float y, int width, int height) {
        int index = 0;
        float[] positionArray = new float[width * height * 2];
        for (int cellY = height - 1; cellY >= 0; --cellY) {
            for (int cellX = 0; cellX < width; ++cellX) {
                positionArray[index] = x + cellX;
                positionArray[index + 1] = y + cellY;
                index += 2;
            }
        }
        return positionArray;
    }

    private static float[] createColorArray(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        int colorArrayIndex = 0;
        float[] colorArray = new float[width * height * 4];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int pixel = img.getRGB(x, y);
                colorArray[colorArrayIndex++] = ((float) ((pixel >> 16) & 0xff)) / 255f; // R
                colorArray[colorArrayIndex++] = ((float) ((pixel >> 8) & 0xff)) / 255f; // G
                colorArray[colorArrayIndex++] = ((float) ((pixel) & 0xff)) / 255f; // B
                colorArray[colorArrayIndex++] = ((float) ((pixel >> 24) & 0xff)) / 255f; // A
            }
        }

        return colorArray;
    }

    private static float[] createStateArray(float[] positionArray, float[] colorArray) {
        int posIndex = 0;
        int colorIndex = 0;
        int stateIndex = 0;
        float[] state = new float[positionArray.length * 2 + colorArray.length];
        while (stateIndex < state.length) {
            // Position
            state[stateIndex++] = positionArray[posIndex++];
            state[stateIndex++] = positionArray[posIndex++];
            state[stateIndex++] = 0;
            state[stateIndex++] = 0;

            // Color (r, g, b, a)
            for (int i = 0; i < 4; ++i)
                state[stateIndex++] = colorArray[colorIndex++];
        }

        return state;
    }

    private static float[] createUpdateArray(int particleCount, float[] initialPos, float[] initialColors, float[] goalColors) {
        int updateIndex = 0;
        int initialPosIndex = 0;
        int initialColorIndex = 0;
        int goalColorIndex = 0;

        float[] update = new float[particleCount * 12];

        while (updateIndex < update.length) {
            // Initial position (x, y)
            for (int i = 0; i < 2; ++i)
                update[updateIndex++] = initialPos[initialPosIndex++];

            // Initial position (z)
            update[updateIndex++] = (float) (RandomInstance.get().nextDouble() * Math.PI * 2);

            // Duration
            update[updateIndex++] = PARTICLE_DURATION + PARTICLE_DURATION_VAR * random11();

            // Original color
            for (int i = 0; i < 4; ++i)
                update[updateIndex++] = initialColors[initialColorIndex++];

            // Goal color
            for (int i = 0; i < 4; ++i)
                update[updateIndex++] = goalColors[goalColorIndex++];
        }

        return update;
    }

    private static float random11() {
        return 2 * RandomInstance.get().nextFloat() - 1;
    }
}