package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

public class AnimationSheet {

    private final AnimationFrame[][] frames;

    private final int cols;

    private final int rows;

    public AnimationSheet(int cols, int rows) {
        this.frames = new AnimationFrame[cols][rows];
        this.cols = cols;
        this.rows = rows;
    }
    
    public static AnimationSheet create(Texture texture, Rectangle bounds, AnimationSheetMetadata metadata, Disposer disposer) {
        int cols = metadata.getCols();
        int rows = metadata.getRows();

        var spriteSheet = new AnimationSheet(cols, rows);

        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
                var textureBounds = metadata.get(col, row);
                Quad quad = Quad.create(bounds, textureBounds, disposer);
                spriteSheet.setFrame(col, row, new AnimationFrame(quad, texture));
            }
        }

        return spriteSheet;
    }

    public static AnimationSheet create(Texture texture, Rectangle bounds, boolean relativeBounds, int cols, int rows,
                                        Disposer disposer) {

        var spriteSheet = new AnimationSheet(cols, rows);

        float width = texture.getWidth();
        float height = texture.getHeight();

        float frameWidth = width / cols;
        float frameHeight = height / rows;

        bounds = relativeBounds
                ? new Rectangle(frameWidth * bounds.xMin(), frameHeight * bounds.yMin(),
                frameWidth * bounds.xMax(), frameHeight * bounds.yMax())
                : bounds;

        for (int col = 0; col < cols; ++col) {
            for (int row = 0; row < rows; ++row) {
                var textureBounds = createTextureBounds(width, height, col, row, frameWidth, frameHeight);
                Quad quad = Quad.create(bounds, textureBounds, disposer);
                spriteSheet.setFrame(col, row, new AnimationFrame(quad, texture));
            }
        }

        return spriteSheet;
    }

    /**
     * Creates texture bounds for a desired sprite within a sprite sheet texture.
     *
     * @param textureWidth  The width of the texture in pixels.
     * @param textureHeight The height of the texture in pixels.
     * @param spriteColumn  The sprite sheet column of the desired sprite.
     * @param spriteRow     The sprite sheet row of the desired sprite.
     * @param spriteWidth   The width of each sprite in the sprite sheet.
     * @param spriteHeight  The height of each sprite in the sprite sheet.
     */
    private static Rectangle createTextureBounds(float textureWidth, float textureHeight, int spriteColumn,
                                                 int spriteRow, float spriteWidth, float spriteHeight) {

        return new Rectangle((spriteColumn * spriteWidth) / textureWidth, (spriteRow * spriteHeight) / textureHeight,
                ((spriteColumn + 1) * spriteWidth) / textureWidth, ((spriteRow + 1) * spriteHeight) / textureHeight);
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public AnimationFrame getFrame(int col, int row) {
        return frames[col][row];
    }

    public void setFrame(int col, int row, AnimationFrame frame) {
        frames[col][row] = frame;
    }
}