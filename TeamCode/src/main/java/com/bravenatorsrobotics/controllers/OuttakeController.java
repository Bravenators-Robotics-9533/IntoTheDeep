package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.OuttakeComponent;

@Config
public class OuttakeController extends AbstractController {

    public static final double PASS_OFF_CLOSED = 0;
    public static final double PASS_OFF_OPEN = 1;

    public static final double WALL_CLAW_CLOSED = 0;
    public static final double WALL_CLAW_OPEN = 1;

    private final OuttakeComponent outtakeComponent;

    public OuttakeController(OuttakeComponent outtakeComponent) {
        this.outtakeComponent = outtakeComponent;
    }


    @Override
    public void initialize() {
        // Initialize the outtake servo in the closed position
        this.outtakeComponent.setPassOffServoPosition(PASS_OFF_OPEN);
        this.outtakeComponent.setWallClawServoPosition(WALL_CLAW_CLOSED);
    }

    @Override
    public void update() {
        // No periodic updates required for this controller, as outtake position is managed via direct methods
    }

    /**
     * Opens the outtake servo.
     */
    public void passOffServoOpen() {
        outtakeComponent.setPassOffServoPosition(PASS_OFF_OPEN);
    }

    /**
     * Toggles the outtake position between open and closed.
     */
    public void togglePassOffServoPosition() {
        double currentPosition = outtakeComponent.getTargetOuttakeServoPosition();
        if (currentPosition == PASS_OFF_CLOSED) {
            outtakeComponent.setPassOffServoPosition(PASS_OFF_OPEN);
        } else {
            outtakeComponent.setPassOffServoPosition(PASS_OFF_CLOSED);
        }
    }

    public void toggleWallClawServoPosition() {
        double currentPosition = outtakeComponent.getTargetWallClawServoPosition();
        if (currentPosition == WALL_CLAW_CLOSED) {
            outtakeComponent.setWallClawServoPosition(WALL_CLAW_OPEN);
        } else {
            outtakeComponent.setWallClawServoPosition(WALL_CLAW_OPEN);
        }
    }

    /**
     * Sets the outtake servo to the closed position.
     */
    public void passOffServoClosed() {
        outtakeComponent.setPassOffServoPosition(PASS_OFF_CLOSED);
    }
}