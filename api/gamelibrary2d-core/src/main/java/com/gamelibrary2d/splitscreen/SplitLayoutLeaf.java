package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.disposal.ResourceContainer;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.BitmapRenderer;

public class SplitLayoutLeaf<T> implements SplitLayout {
    private final InternalTargetSettings internalTargetSettings;
    private final T param;
    private final PrepareUpdateAction<T> prepareUpdate;
    private final PrepareRenderAction<T> prepareRender;
    private final ResourceContainer<BitmapRenderer> areaRenderer = new ResourceContainer<>();

    private GameObject target;

    /**
     * Creates a new instance of {@link SplitLayoutLeaf}.
     *
     * @param target        The target view of the {@link SplitLayer}.
     * @param prepareRender Preparation action performed before rendering.
     * @param param         Parameter used in preparation actions.
     */
    public SplitLayoutLeaf(GameObject target,
                           PrepareRenderAction<T> prepareRender,
                           T param) {
        this(target, null, prepareRender, param);
    }

    /**
     * Creates a new instance of {@link SplitLayoutLeaf}.
     *
     * @param target        The target view of the {@link SplitLayer}.
     * @param prepareUpdate Preparation action performed before updating.
     * @param param         Parameter used in preparation actions.
     */
    public SplitLayoutLeaf(GameObject target,
                           PrepareUpdateAction<T> prepareUpdate,
                           T param) {
        this(target, prepareUpdate, null, param);
    }

    /**
     * Creates a new instance of {@link SplitLayoutLeaf}.
     *
     * @param target        The target view of the {@link SplitLayer}.
     * @param prepareUpdate Preparation action performed before updating.
     * @param prepareRender Preparation action performed before rendering.
     * @param param         Parameter used in preparation actions.
     */
    public SplitLayoutLeaf(GameObject target,
                           PrepareUpdateAction<T> prepareUpdate,
                           PrepareRenderAction<T> prepareRender,
                           T param) {
        this.target = target;
        this.param = param;
        this.prepareUpdate = prepareUpdate;
        this.prepareRender = prepareRender;
        this.internalTargetSettings = new InternalTargetSettings();
        updateSettingsFromTarget();
    }

    @Override
    public void update(GameObject target, Rectangle viewArea, float deltaTime, Disposer disposer) {
        this.target = target;
        if (!areaRenderer.hasResource() || !areaRenderer.getResource().getArea().equals(viewArea)) {
            areaRenderer.createResource(d -> BitmapRenderer.create(viewArea, d), disposer);
        }
        updateTargetFromSettings();
        if (prepareUpdate != null)
            prepareUpdate.invoke(param, viewArea, deltaTime);
        updateSettingsFromTarget();
    }

    @Override
    public void render(float alpha) {
        updateTargetFromSettings();
        var areaRenderer = this.areaRenderer.getResource();
        if (prepareRender != null)
            prepareRender.invoke(param, areaRenderer.getArea());
        areaRenderer.render(this::renderAction);
        areaRenderer.renderFrameBuffer(alpha);
        updateSettingsFromTarget();
    }

    private void renderAction() {
        ModelMatrix.instance().pushMatrix();
        var renderArea = areaRenderer.getResource().getArea();
        ModelMatrix.instance().translatef(renderArea.xMin(), renderArea.yMin(), 0);
        target.render(1f);
        ModelMatrix.instance().popMatrix();
    }

    private void updateTargetFromSettings() {
        target.getPosition().set(internalTargetSettings.position());
        target.getScaleAndRotationCenter().set(internalTargetSettings.getScaleAndRotationCenter());
        target.getScale().set(internalTargetSettings.getScale());
        target.setRotation(internalTargetSettings.getRotation());
    }

    private void updateSettingsFromTarget() {
        internalTargetSettings.position().set(target.getPosition());
        internalTargetSettings.getScaleAndRotationCenter().set(target.getScaleAndRotationCenter());
        internalTargetSettings.getScale().set(target.getScale());
        internalTargetSettings.setRotation(target.getRotation());
    }
}
