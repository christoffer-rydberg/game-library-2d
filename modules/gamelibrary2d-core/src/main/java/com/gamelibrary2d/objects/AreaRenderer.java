package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.glUtil.FrameBuffer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

import static com.gamelibrary2d.framework.OpenGL.*;

/**
 * Renders to an intermediate {@link FrameBuffer frame buffer} before rendering to the {@link Window window's} primary buffer.
 * The area to render is restricted by a specified {@link Rectangle rectangle}. Another benefit is that the intermediate buffer
 * can be rendered to without applying an alpha factor. This prevents blending of {@link GameObject objects} that would
 * occur if rendering objects separately with applied alpha. To get the desired opacity, the alpha factor can
 * be applied when rendering the intermediate buffer to the primary buffer.
 */
public class AreaRenderer {
    private final Rectangle area;
    private final FrameBuffer frameBuffer;
    private final SurfaceRenderer frameBufferRenderer;

    private AreaRenderer(Rectangle area, Disposer disposer) {
        var texture = Texture.create((int) area.getWidth(), (int) area.getHeight(), disposer);
        this.area = area;
        this.frameBuffer = FrameBuffer.create(texture, disposer);
        this.frameBufferRenderer = new SurfaceRenderer(Quad.create(area, disposer), frameBuffer.getTexture());
    }

    /**
     * Creates a new instance of {@link AreaRenderer}.
     *
     * @param area     The render area.
     * @param disposer The disposer.
     */
    public static AreaRenderer create(Rectangle area, Disposer disposer) {
        return new AreaRenderer(area, disposer);
    }

    /**
     * The render area.
     */
    public Rectangle getArea() {
        return area;
    }

    /**
     * Renders to an intermediate {@link FrameBuffer} using the specified render action.
     * The buffer is automatically cleared before rendering.
     *
     * @param renderAction The render action.
     */
    public void render(Action renderAction) {
        render(renderAction, true);
    }

    /**
     * Renders to an intermediate {@link FrameBuffer} using the specified render action.
     *
     * @param renderAction The render action.
     * @param clear        Determines if the buffer is cleared before rendering.
     */
    public void render(Action renderAction, boolean clear) {
        var offsetX = frameBufferRenderer.getBounds().getXMin();
        var offsetY = frameBufferRenderer.getBounds().getYMin();
        ModelMatrix.instance().pushMatrix();
        ModelMatrix.instance().translatef(-offsetX, -offsetY, 0);
        frameBuffer.bind();
        if (clear) {
            frameBuffer.clear();
        }
        
        // Fix for incorrect alpha blending. See:
        // https://community.khronos.org/t/alpha-blending-issues-when-drawing-frame-buffer-into-default-buffer/73958/3
        OpenGL.instance().glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        renderAction.invoke();
        frameBuffer.unbind(true);
        ModelMatrix.instance().popMatrix();
    }

    /**
     * Renders the intermediate {@link FrameBuffer}.
     *
     * @param alpha The alpha factor.
     */
    public void renderFrameBuffer(float alpha) {
        frameBufferRenderer.render(alpha);
    }
}
