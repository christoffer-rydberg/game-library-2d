package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.GameFrameClient;
import com.gamelibrary2d.demos.networkgame.common.PlayerAcceleration;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.input.InputBinding;
import com.gamelibrary2d.input.InputController;

public class LocalPlayer extends AbstractPlayer {
    private final InputController controller;
    private final PlayerAcceleration playerAcceleration;

    public LocalPlayer(byte primaryType, GameFrameClient client, DataBuffer buffer) {
        super(primaryType, client, buffer);

        playerAcceleration = new PlayerAcceleration(client, getId());

        var leftInput = InputBinding.keyboard(Keyboard.instance().keyLeft());
        leftInput.onActive(() -> playerAcceleration.setLeftAcceleration(1f));
        leftInput.onReleased(() -> playerAcceleration.setLeftAcceleration(0f));

        var rightInput = InputBinding.keyboard(Keyboard.instance().keyRight());
        rightInput.onActive(() -> playerAcceleration.setRightAcceleration(1f));
        rightInput.onReleased(() -> playerAcceleration.setRightAcceleration(0f));

        var forwardInput = InputBinding.keyboard(Keyboard.instance().keyUp());
        forwardInput.onActive(() -> playerAcceleration.setAcceleration(1f));
        forwardInput.onReleased(() -> playerAcceleration.setAcceleration(0f));

        controller = new InputController();
        controller.getBindings().add(leftInput);
        controller.getBindings().add(rightInput);
        controller.getBindings().add(forwardInput);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        controller.update();
        playerAcceleration.updateServer();
    }
}
