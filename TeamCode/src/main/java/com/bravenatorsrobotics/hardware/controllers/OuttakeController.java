package com.bravenatorsrobotics.hardware.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.components.OuttakeComponent;

@Config
public class OuttakeController extends AbstractController {

    public static final double PASS_OFF_PIVOT_INITIAL = 0;
    public static final double PASS_OFF_PIVOT_SCORE = 0.8;

    public static final double PASS_OFF_CLAW_CLOSED = 0;
    public static final double PASS_OFF_CLAW_OPEN = 0.65;

    public static final double WALL_CLAW_CLOSED = 0.25;
    public static final double WALL_CLAW_OPEN = 0.82;

    private final OuttakeComponent outtakeComponent;

    public OuttakeController(OuttakeComponent outtakeComponent) {
        this.outtakeComponent = outtakeComponent;
    }

    @Override
    public void initialize() {

        this.outtakeComponent.setPassOffPivotServoPosition(PASS_OFF_PIVOT_INITIAL);
        this.outtakeComponent.setPassOffClawServoPosition(PASS_OFF_CLAW_OPEN);
        this.outtakeComponent.setWallClawServoPosition(WALL_CLAW_CLOSED);

    }

    @Override
    public void update() {

    }

    public void togglePassOffClaw() {

        double currentPosition = outtakeComponent.getTargetPassOffClawServoPosition();

        if (currentPosition == PASS_OFF_CLAW_CLOSED) {
            outtakeComponent.setPassOffClawServoPosition(PASS_OFF_CLAW_OPEN);
        } else {
            outtakeComponent.setPassOffClawServoPosition(PASS_OFF_CLAW_CLOSED);
        }

    }

    public void toggleWallClawServoPosition() {

        if (outtakeComponent.getTargetWallClawServoPosition() == WALL_CLAW_CLOSED) {
            outtakeComponent.setWallClawServoPosition(WALL_CLAW_OPEN);
        } else {
            outtakeComponent.setWallClawServoPosition(WALL_CLAW_CLOSED);
        }

    }

    public void setPassOffClawClosed() { outtakeComponent.setPassOffClawServoPosition(PASS_OFF_CLAW_CLOSED); }
    public void setPassOffClawOpen() { outtakeComponent.setPassOffClawServoPosition(PASS_OFF_CLAW_OPEN); }

    public void setPassOffPivotInitial() { outtakeComponent.setPassOffPivotServoPosition(PASS_OFF_PIVOT_INITIAL); }
    public void setPassOffPivotScore() { outtakeComponent.setPassOffPivotServoPosition(PASS_OFF_PIVOT_SCORE); }


}