package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.OuttakeComponent;

@Config
public class OuttakeController extends AbstractController {

    public static final double OUTTAKE_CLOSED = 0;
    public static final double OUTTAKE_OPEN = 1;

    private final OuttakeComponent outtakeComponent;

    public OuttakeController(OuttakeComponent outtakeComponent) {
        this.outtakeComponent = outtakeComponent;
    }

    @Override
    public void initialize() {
        // Initialize the outtake servo in the closed position
        this.outtakeComponent.setOuttakeServoPosition(OUTTAKE_CLOSED);
    }

    @Override
    public void update() {
        // No periodic updates required for this controller, as outtake position is managed via direct methods
    }

    /**
     * Opens the outtake servo.
     */
    public void outtakeServoOpen() {
        outtakeComponent.setOuttakeServoPosition(OUTTAKE_OPEN);
    }

    /**
     * Toggles the outtake position between open and closed.
     */
    public void toggleOuttakePosition() {
        double currentPosition = outtakeComponent.getTargetOuttakeServoPosition();
        if (currentPosition == OUTTAKE_CLOSED) {
            outtakeComponent.setOuttakeServoPosition(OUTTAKE_OPEN);
        } else {
            outtakeComponent.setOuttakeServoPosition(OUTTAKE_CLOSED);
        }
    }

    /**
     * Sets the outtake servo to the closed position.
     */
    public void outtakeServoClosed() {
        outtakeComponent.setOuttakeServoPosition(OUTTAKE_CLOSED);
    }
}