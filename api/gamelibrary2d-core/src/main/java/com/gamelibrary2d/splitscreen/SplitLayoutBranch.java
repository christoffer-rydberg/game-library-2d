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

    public void update(GameObject target, Rectangle viewArea, float deltaTime, Disposer disposer) {
        float childWidth = orientation == SplitOrientation.VERTICAL
                ? viewArea.getWidth()
                : viewArea.getWidth() / layouts.size() - margin;

        float childHeight = orientation == SplitOrientation.HORIZONTAL
                ? viewArea.getHeight()
                : viewArea.getHeight() / layouts.size() - margin;

        float dx = orientation == SplitOrientation.VERTICAL ? 0 : childWidth + margin;
        float dy = orientation == SplitOrientation.HORIZONTAL ? 0 : childHeight + margin;

        for (int i = 0; i < layouts.size(); ++i) {
            float xMin = viewArea.getLowerX() + dx * i;
            float yMin = viewArea.getLowerY() + dy * i;

            if (i == layouts.size() - 1) {
                childWidth += orientation == SplitOrientation.VERTICAL ? 0 : 1;
                childHeight += orientation == SplitOrientation.HORIZONTAL ? 0 : 1;
            }

            Rectangle childArea = new Rectangle(
                    xMin,
                    yMin,
                    xMin + childWidth,
                    yMin + childHeight);

            layouts.get(i).update(target, childArea, deltaTime, disposer);
        }
    }

    @Override
    public void render(float alpha) {
        for (int i = 0; i < layouts.size(); ++i) {
            layouts.get(i).render(alpha);
        }
    }
}
