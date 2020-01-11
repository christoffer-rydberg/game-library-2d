package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class SplitLayoutBranch implements SplitLayout {
    private final float margin;
    private final SplitOrientation orientation;
    private final ArrayList<SplitLayout> layouts;

    public SplitLayoutBranch(float margin, SplitOrientation orientation) {
        this.margin = margin;
        this.orientation = orientation;
        layouts = new ArrayList<>(2);
    }

    public List<SplitLayout> getLayouts() {
        return layouts;
    }

    @Override
    public void update(GameObject target, Rectangle viewArea, float deltaTime, Disposer disposer) {
        float width = viewArea.getWidth();
        float height = viewArea.getHeight();
        float childWidth = orientation == SplitOrientation.VERTICAL ? width : width / layouts.size();
        float childHeight = orientation == SplitOrientation.HORIZONTAL ? height : height / layouts.size();
        float dx = orientation == SplitOrientation.VERTICAL ? 0 : childWidth + margin;
        float dy = orientation == SplitOrientation.HORIZONTAL ? 0 : childHeight + margin;
        for (int i = 0; i < layouts.size(); ++i) {
            float xMin = viewArea.getXMin() + dx * i;
            float xMax = xMin + childWidth;
            float yMin = viewArea.getYMin() + dy * i;
            float yMax = yMin + childHeight;
            layouts.get(i).update(target, new Rectangle(xMin, yMin, xMax, yMax), deltaTime, disposer);
        }
    }

    @Override
    public void render(float alpha) {
        for (int i = 0; i < layouts.size(); ++i) {
            layouts.get(i).render(alpha);
        }
    }
}
