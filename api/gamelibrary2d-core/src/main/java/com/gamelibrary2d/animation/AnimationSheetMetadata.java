package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Rectangle;

public class AnimationSheetMetadata {

    private int cols;

    private int rows;
    
    private Rectangle[][] bounds;

    public AnimationSheetMetadata(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        bounds = new Rectangle[cols][rows];
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void add(int col, int row, Rectangle bounds) {
        this.bounds[col][row] = bounds;
    }

    public Rectangle get(int col, int row) {
        return bounds[col][row];
    }
}
