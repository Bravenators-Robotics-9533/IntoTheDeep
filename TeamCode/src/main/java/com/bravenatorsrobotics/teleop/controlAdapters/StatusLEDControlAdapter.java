package com.bravenatorsrobotics.teleop.controlAdapters;

import com.qualcomm.robotcore.hardware.Gamepad;

public class StatusLEDControlAdapter implements IControlAdapter {

    public enum State {
        DEFAULT,
        INDICATE_RELEASE_POSITION
    }

    private final Gamepad gamepad1;
    private final Gamepad gamepad2;

    private State state = State.DEFAULT;
    private boolean onStateChange = true;

    public StatusLEDControlAdapter(Gamepad gamepad1, Gamepad gamepad2) {

        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

    }

    @Override public void initialize() {}

    @Override
    public void update() {

        if(onStateChange) {

            switch (state) {

                case DEFAULT:
                    this.gamepad1.setLedColor(0, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);
                    this.gamepad2.setLedColor(255, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);

                    break;

                case INDICATE_RELEASE_POSITION:
                    this.gamepad1.rumble(100);
                    this.gamepad2.rumble(100);

                    this.gamepad1.setLedColor(180, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);
                    this.gamepad2.setLedColor(180, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);

                    break;

            }

            this.onStateChange = false;

        }

    }

    public void setState(State state) {
        this.state = state;
        this.onStateChange = true;
    }

    public State getState() { return this.state; }

}
