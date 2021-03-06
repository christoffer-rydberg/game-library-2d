package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.components.containers.AbstractPanel;
import com.gamelibrary2d.components.denotations.Parent;
import com.gamelibrary2d.components.objects.GameObject;
import com.gamelibrary2d.particle.parameters.PositionParameters;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.properties.BooleanProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.GenericProperty;

public class PositionParametersPanel extends AbstractPanel<GameObject> implements Parent<GameObject> {

    private final ParticleSystemModel particleSystem;

    public PositionParametersPanel(ParticleSystemModel particleSystem) {
        this.particleSystem = particleSystem;
        PanelUtil.stack(this, spawnArea(), 0f);
        PanelUtil.stack(this, spawnAreaWidth());
        PanelUtil.stack(this, spawnAreaHeight());
        PanelUtil.stack(this, spawnAngle());
        PanelUtil.stack(this, localCenter());
    }

    private PositionParameters params() {
        return particleSystem.getSettings().getPositionParameters();
    }

    private GameObject localCenter() {
        return new PanelElement.Boolean(
                "Local Center",
                new BooleanProperty(() -> params().isLocalCenter(), v -> params().setLocalCenter(v))
        );
    }

    private GameObject spawnArea() {
        return new PanelElement.Enum<>(
                "Spawn Area",
                PositionParameters.SpawnArea.class,
                new GenericProperty<>(
                        () -> params().getSpawnArea(),
                        v -> params().setSpawnArea(v))
        );
    }

    private GameObject spawnAreaWidth() {
        return new PanelElement.Float(
                "Spawn Area Width",
                new FloatProperty(() -> params().getSpawnAreaWidth(), v -> params().setSpawnAreaWidth(v)),
                new FloatProperty(() -> params().getSpawnAreaWidthVar(), v -> params().setSpawnAreaWidthVar(v))
        );
    }

    private GameObject spawnAreaHeight() {
        return new PanelElement.Float(
                "Spawn Area Height",
                new FloatProperty(() -> params().getSpawnAreaHeight(), v -> params().setSpawnAreaHeight(v)),
                new FloatProperty(() -> params().getSpawnAreaHeightVar(), v -> params().setSpawnAreaHeightVar(v))
        );
    }

    private GameObject spawnAngle() {
        return new PanelElement.Float(
                "Spawn Angle",
                new FloatProperty(() -> params().getSpawnAngle(), v -> params().setSpawnAngle(v)),
                new FloatProperty(() -> params().getSpawnAngleVar(), v -> params().setSpawnAngleVar(v))
        );
    }
}