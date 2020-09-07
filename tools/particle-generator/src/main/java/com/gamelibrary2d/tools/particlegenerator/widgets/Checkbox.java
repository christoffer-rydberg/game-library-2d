package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.util.BooleanProperty;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

public class Checkbox extends AbstractWidget {
    private final BooleanProperty checked;
    private final Action onChecked;
    private final Action onUnchecked;
    private final CheckboxRenderer renderer;
    private boolean cachedValue;

    public Checkbox(Box box, Font font, BooleanProperty checked) {
        this.checked = checked;
        this.renderer = new CheckboxRenderer(box, font);
        this.onChecked = null;
        this.onUnchecked = null;
        setContent(renderer);
    }

    public Checkbox(Box box, Font font, BooleanProperty checked, Action onChecked, Action onUnchecked) {
        this.checked = checked;
        this.renderer = new CheckboxRenderer(box, font);
        this.onChecked = onChecked;
        this.onUnchecked = onUnchecked;
        setContent(renderer);
    }

    private void updateCheckbox() {
        var value = checked.get();
        if (cachedValue != value) {
            cachedValue = value;
            if (value) {
                renderer.check();
                if (onChecked != null) {
                    onChecked.invoke();
                }
            } else {
                renderer.uncheck();
                if (onUnchecked != null) {
                    onUnchecked.invoke();
                }
            }
        }
    }

    @Override
    public void onRender(float alpha) {
        updateCheckbox();
        super.onRender(alpha);
    }

    @Override
    protected void onMouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonDown(button, mods, x, y, projectedX, projectedY);
        toggle();
    }

    public boolean isChecked() {
        return cachedValue;
    }

    public void toggle() {
        checked.set(!checked.get());
        updateCheckbox();
    }

    private class CheckboxRenderer implements Renderable, Bounded {
        private final Box box;
        private final Label label;

        CheckboxRenderer(Box box, Font font) {
            this.box = box;
            label = new Label(new TextRenderer(font));
            label.setHorizontalAlignment(HorizontalAlignment.CENTER);
            label.setVerticalAlignment(VerticalAlignment.CENTER);
        }

        public void check() {
            label.setText("V");
        }

        public void uncheck() {
            label.setText("");
        }

        @Override
        public void render(float alpha) {
            box.render(alpha);
            label.render(alpha);
        }

        @Override
        public Rectangle getBounds() {
            return box.getBounds();
        }
    }
}
