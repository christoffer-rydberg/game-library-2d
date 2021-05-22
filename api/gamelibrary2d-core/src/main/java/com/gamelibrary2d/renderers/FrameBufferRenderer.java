package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.DefaultFrameBuffer;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.glUtil.OpenGLState;

import static com.gamelibrary2d.framework.OpenGL.*;

public class FrameBufferRenderer {
    private final Rectangle area;
    private final FrameBuffer frameBuffer;

    public FrameBufferRenderer(Rectangle area, FrameBuffer frameBuffer) {
        this.area = area;
        this.frameBuffer = frameBuffer;
    }

    public static FrameBufferRenderer create(Rectangle area, Disposer disposer) {
        Texture texture = DefaultTexture.create((int) area.getWidth(), (int) area.getHeight(), disposer);
        FrameBuffer frameBuffer = DefaultFrameBuffer.create(texture, disposer);
        return new FrameBufferRenderer(area, frameBuffer);
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public Rectangle getArea() {
        return area;
    }

    public void render(Renderable renderable, float alpha) {
        render(renderable, alpha, true);
    }

    public void render(Renderable renderable, float alpha, boolean clear) {
        int previousFbo = frameBuffer.bind();
        try {
            if (clear) {
                frameBuffer.clear();
            }

            // Fix for incorrect alpha blending. See:
            // https://community.khronos.org/t/alpha-blending-issues-when-drawing-frame-buffer-into-default-buffer/73958/3
            OpenGL.instance().glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().clearMatrix();
            ModelMatrix.instance().translatef(-area.getLowerX(), -area.getLowerY(), 0);
            renderable.render(alpha);
            ModelMatrix.instance().popMatrix();
        } finally {
            OpenGLState.bindFrameBuffer(previousFbo);
        }
    }

    public boolean isVisible(float x, float y) {
        int previousFbo = frameBuffer.bind();
        try {
            return frameBuffer.isVisible(
                    (int) (x - area.getLowerX()),
                    (int) (y - area.getLowerY()));
        } finally {
            OpenGLState.bindFrameBuffer(previousFbo);
        }
    }
}
