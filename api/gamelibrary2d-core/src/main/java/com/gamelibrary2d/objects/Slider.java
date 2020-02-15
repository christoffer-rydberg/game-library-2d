package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Slider extends AbstractMouseRoutingObject<Layer<Renderable>> {
    private final List<DragBeginListener> dragBeginListeners = new CopyOnWriteArrayList<>();
    private final List<DragStopListener> dragStopListeners = new CopyOnWriteArrayList<>();
    private final List<ValueChangedListener> valueChangedListeners = new CopyOnWriteArrayList<>();
    private GameObject handle;
    private SliderDirection direction;
    private float min;
    private float max;
    private float step;
    private int dragButton = -1;
    private float dragOriginX;
    private float dragOriginY;

    public Slider(Renderable handle, SliderDirection direction, float min, float max, float step) {
        this.handle = createHandle(handle);
        this.direction = direction;
        this.min = min;
        this.max = max;
        this.step = step;
        var content = new BasicLayer<>();
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
                ? handle.position().getX()
                : handle.position().getY()) / step);
    }

    public int setValue(int value, boolean publishEvent) {
        var actual = (int) Math.max(min, Math.min(max, value));
        switch (direction) {
            case HORIZONTAL:
                handle.position().set(actual * step, 0);
                break;
            case VERTICAL:
                handle.position().set(0, actual * step);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }

        if (publishEvent) {
            for (var listener : valueChangedListeners) {
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
                return (int) ((handle.position().getX() + x - dragOriginX) / step);
            case VERTICAL:
                return (int) ((handle.position().getY() + y - dragOriginY) / step);
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    private GameObject createHandle(Renderable renderable) {
        var handleObj = new ObservableObject<>(renderable);
        handleObj.addMouseButtonDownListener(this::onHandleClicked);
        handleObj.addMouseDragListener(this::onHandleDragged);
        handleObj.addMouseButtonReleaseListener(this::onHandleReleased);
        return handleObj;
    }

    private void onHandleClicked(GameObject object, int button, int mods, float x, float y) {
        if (dragButton < 0) {
            dragButton = button;
            dragOriginX = x;
            dragOriginY = y;
            for (var listener : dragBeginListeners) {
                listener.onDragBegin(getValue());
            }
        }
    }

    private void onHandleReleased(GameObject object, int button, int mods, float v, float v1) {
        if (dragButton == button) {
            dragButton = -1;
            for (var listener : dragStopListeners) {
                listener.onDragStop(getValue());
            }
        }
    }

    private void onHandleDragged(GameObject object, float x, float y, boolean drag) {
        if (dragButton >= 0) {
            setValue(getValueFromPosition(x, y));
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
