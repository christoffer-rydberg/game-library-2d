package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.components.denotations.Bounded;
import com.gamelibrary2d.renderers.LineRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.tools.particlegenerator.properties.BooleanProperty;
import com.gamelibrary2d.resources.HorizontalTextAlignment;
import com.gamelibrary2d.resources.VerticalTextAlignment;
import com.gamelibrary2d.components.widgets.AbstractWidget;
import com.gamelibrary2d.components.widgets.Label;

public class Checkbox extends AbstractWidget {
    private final BooleanProperty checked;
    private final Action onChecked;
    private final Action onUnchecked;
    private final CheckboxRenderer renderer;
    private boolean cachedValue;

    public Checkbox(Box box, LineRenderer lineRenderer, Font font, BooleanProperty checked) {
        this.checked = checked;
        this.renderer = new CheckboxRenderer(box, lineRenderer, font);
        this.onChecked = null;
        this.onUnchecked = null;
        setContent(renderer);
    }

    public Checkbox(Box box, LineRenderer lineRenderer, Font font, BooleanProperty checked, Action onChecked, Action onUnchecked) {
        this.checked = checked;
        this.renderer = new CheckboxRenderer(box, lineRenderer, font);
        this.onChecked = onChecked;
        this.onUnchecked = onUnchecked;
        setContent(renderer);
    }

    private void updateCheckbox() {
        boolean value = checked.get();
        if (cachedValue != value) {
            cachedValue = value;
            if (value) {
                renderer.check();
                if (onChecked != null) {
                    onChecked.perform();
                }
            } else {
                renderer.uncheck();
                if (onUnchecked != null) {
                    onUnchecked.perform();
                }
            }
        }
    }

    @Override
    public void onRenderUnprojected(float alpha) {
        updateCheckbox();
        super.onRenderUnprojected(alpha);
    }

    @Override
    protected void onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerDown(id, button, x, y, projectedX, projectedY);
        toggle();
    }

    public void toggle() {
        checked.set(!checked.get());
        updateCheckbox();
    }

    private class CheckboxRenderer implements Renderable, Bounded {
        private final Box box;
        private final LineRenderer renderer;
        private final Label label;

        CheckboxRenderer(Box box, LineRenderer renderer, Font font) {
            this.box = box;
            this.renderer = renderer;
            label = new Label(new TextRenderer(font));
            label.setAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.CENTER);
        }

        public void check() {
            label.setText("V");
            label.getTextRenderer().getParameters().setColor(Color.GREEN);
        }

        public void uncheck() {
            label.setText("");
        }

        @Override
        public void render(float alpha) {
            box.render(renderer, alpha);
            float centerX = box.getBounds().getCenterX();
            float centerY = box.getBounds().getCenterY();
            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().translatef(centerX, centerY, 0f);
            label.render(alpha);
            ModelMatrix.instance().popMatrix();
        }

        @Override
        public Rectangle getBounds() {
            return box.getBounds();
        }
    }
}
