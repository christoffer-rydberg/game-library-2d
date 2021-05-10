package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.GameFrameClient;
import com.gamelibrary2d.demos.networkgame.client.input.Controller;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerFactory;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;
import com.gamelibrary2d.demos.networkgame.common.PlayerAcceleration;

public class LocalPlayer extends AbstractPlayer {
    private final Controller controller;
    private final PlayerAcceleration playerAcceleration;

    public LocalPlayer(ControllerFactory controllerFactory, byte primaryType, GameFrameClient client, DataBuffer buffer) {
        super(primaryType, client, buffer);

        controller = controllerFactory.create();
        playerAcceleration = new PlayerAcceleration(client, getId());

        controller.addBinding(
                ControllerInputId.LEFT,
                playerAcceleration::setLeftAcceleration);

        controller.addBinding(
                ControllerInputId.RIGHT,
                playerAcceleration::setRightAcceleration);

        controller.addBinding(
                ControllerInputId.UP,
                playerAcceleration::setAcceleration);
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        controller.update(deltaTime);
        playerAcceleration.updateServer();
    }
}
