package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.IntakeComponent;

/*
 * 1. Find and set the intake controller static finals for servo position
 *
 */
@Config
public class IntakeController extends AbstractController {

    public static final double INITIAL_PIVOT_X_POSITION   = 0.40;
    public static final double FULL_PIVOT_X_POSITION      = .725;

    public static final double INITIAL_PIVOT_Y_POSITION   = 1;
    public static final double AUTO_INTAKE_PIVOT_Y_POSITION = 0.45;
    public static final double AUTO_CAPTURE_PIVOT_Y_POSITION = 0.25;
    public static final double TELE_INTAKE_PIVOT_Y_POSITION = 0.17;
    public static final double TELE_CAPTURE_PIVOT_Y_POSITION = 0;

    public static final double INITIAL_TENSION_POSITION = 0.7;
    public static final double FULL_TENSION_POSITION    = 1;

    protected final IntakeComponent intakeComponent;

    public IntakeController(IntakeComponent intakeComponent) {

        this.intakeComponent = intakeComponent;

    }

    @Override
    public void initialize() {

        this.intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_X_POSITION);
        this.intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_Y_POSITION);
        this.intakeComponent.setTensionServoPosition(INITIAL_TENSION_POSITION);

    }

    @Override public void update() {}

    public void toggleTensionPosition() {

        if(intakeComponent.getTargetTensionServoPosition() == INITIAL_TENSION_POSITION)
            intakeComponent.setTensionServoPosition(FULL_TENSION_POSITION);
        else
            intakeComponent.setTensionServoPosition(INITIAL_TENSION_POSITION);

    }
    public void togglePivotXPosition() {

        if(intakeComponent.getTargetPivotServoXPosition() == INITIAL_PIVOT_X_POSITION)
            intakeComponent.setPivotServoXPosition(FULL_PIVOT_X_POSITION);
        else
            intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_X_POSITION);

    }
    public void togglePivotYPosition() {

        if(intakeComponent.getTargetPivotServoYPosition() == TELE_INTAKE_PIVOT_Y_POSITION)
            intakeComponent.setPivotServoYPosition(TELE_CAPTURE_PIVOT_Y_POSITION);
        else
            intakeComponent.setPivotServoYPosition(TELE_INTAKE_PIVOT_Y_POSITION);

    }
    public void PivotYRestPosition() {

        intakeComponent.setPivotServoYPosition(AUTO_CAPTURE_PIVOT_Y_POSITION);

    }
    public void AutoPivotYCapturePosition() {

        intakeComponent.setPivotServoYPosition(AUTO_INTAKE_PIVOT_Y_POSITION);

    }
    public void AutoPivotYIntakePosition(){

        intakeComponent.setPivotServoYPosition(AUTO_CAPTURE_PIVOT_Y_POSITION);

    }
    public void TelePivotYCapturePosition() {

        intakeComponent.setPivotServoYPosition(TELE_INTAKE_PIVOT_Y_POSITION);

    }
    public void TelePivotYIntakePosition(){

        intakeComponent.setPivotServoYPosition(TELE_CAPTURE_PIVOT_Y_POSITION);

    }

}