package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.widgets.AbstractAggregatingWidget;
import com.gamelibrary2d.widgets.DefaultWidget;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Slider extends AbstractAggregatingWidget<Layer> {
    private final List<DragBeginListener> dragBeginListeners = new CopyOnWriteArrayList<>();
    private final List<DragStopListener> dragStopListeners = new CopyOnWriteArrayList<>();
    private final List<ValueChangedListener> valueChangedListeners = new CopyOnWriteArrayList<>();
    private GameObject handle;
    private SliderDirection direction;
    private float min;
    private float max;
    private float step;
    private int pointerId = -1;
    private int pointerButton = -1;
    private float dragOriginX;
    private float dragOriginY;

    public Slider(Renderable handle, SliderDirection direction, float min, float max, float step) {
        this.handle = createHandle(handle);
        this.direction = direction;
        this.min = min;
        this.max = max;
        this.step = step;
        Layer<Renderable> content = new BasicLayer<>();
        content.add(this.handle);
        setContent(content);
    }

    public Slider(Renderable handle, SliderDirection direction) {
        this(handle, direction, 0, 100, 1);
    }

    public Slider(Renderable handle) {
        this(handle, SliderDirection.HORIZONTAL);
    }

    public void addDragBeginListener(DragBeginListener listener) {
        dragBeginListeners.add(listener);
    }

    public void addDragStopListener(DragStopListener listener) {
        dragStopListeners.add(listener);
    }

    public void addValueChangedListener(ValueChangedListener listener) {
        valueChangedListeners.add(listener);
    }

    private int getValue() {
        return (int) ((direction == SliderDirection.HORIZONTAL
                ? handle.getPosition().getX()
                : handle.getPosition().getY()) / step);
    }

    public int setValue(int value, boolean publishEvent) {
        int actual = (int) Math.max(min, Math.min(max, value));
        switch (direction) {
            case HORIZONTAL:
                handle.setPosition(actual * step, 0);
                break;
            case VERTICAL:
                handle.setPosition(0, actual * step);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }

        if (publishEvent) {
            for (ValueChangedListener listener : valueChangedListeners) {
                listener.onValueChanged(actual);
            }
        }

        return actual;
    }

    public int setValue(int value) {
        return setValue(value, true);
    }

    private int getValueFromPosition(float x, float y) {
        switch (direction) {
            case HORIZONTAL:
                return (int) ((handle.getPosition().getX() + x - dragOriginX) / step);
            case VERTICAL:
                return (int) ((handle.getPosition().getY() + y - dragOriginY) / step);
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    private GameObject createHandle(Renderable renderable) {
        DefaultWidget<Renderable> handleObj = new DefaultWidget<>(renderable);
        handleObj.addPointerDownListener(this::onHandleClicked);
        handleObj.addPointerDragListener(this::onHandleDragged);
        handleObj.addPointerUpListener(this::onHandleReleased);
        return handleObj;
    }

    private void onHandleClicked(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId < 0) {
            pointerId = id;
            pointerButton = button;
            dragOriginX = projectedX;
            dragOriginY = projectedY;
            for (DragBeginListener listener : dragBeginListeners) {
                listener.onDragBegin(getValue());
            }
        }
    }

    private void onHandleReleased(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id && pointerButton == button) {
            pointerId = -1;
            pointerButton = -1;
            for (DragStopListener listener : dragStopListeners) {
                listener.onDragStop(getValue());
            }
        }
    }

    private void onHandleDragged(int id, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id) {
            setValue(getValueFromPosition(projectedX, projectedY));
        }
    }

    public enum SliderDirection {
        HORIZONTAL,
        VERTICAL
    }

    public interface DragBeginListener {
        void onDragBegin(int value);
    }

    public interface DragStopListener {
        void onDragStop(int value);
    }

    public interface ValueChangedListener {
        void onValueChanged(int value);
    }
}
