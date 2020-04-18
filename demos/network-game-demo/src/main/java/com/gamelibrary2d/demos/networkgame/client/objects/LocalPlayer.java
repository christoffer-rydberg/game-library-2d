package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;
import com.gamelibrary2d.demos.networkgame.common.PlayerRotation;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.input.InputBinding;
import com.gamelibrary2d.input.InputController;

public class LocalPlayer extends AbstractClientObject {
    private final InputController controller;
    private final PlayerRotation rotation;

    public LocalPlayer(byte objectIdentifier, DemoFrameClient client, DataBuffer buffer) {
        super(objectIdentifier, client, buffer);

        rotation = new PlayerRotation(client, getId());

        var leftInput = InputBinding.keyboard(Keyboard.instance().keyLeft());
        leftInput.onActive(() -> rotation.setRotatingLeft(true));
        leftInput.onRelease(() -> rotation.setRotatingLeft(false));

        var rightInput = InputBinding.keyboard(Keyboard.instance().keyRight());
        rightInput.onActive(() -> rotation.setRotatingRight(true));
        rightInput.onRelease(() -> rotation.setRotatingRight(false));

        controller = new InputController();
        controller.getBindings().add(leftInput);
        controller.getBindings().add(rightInput);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        controller.update();
        rotation.updateServer();
    }
}
