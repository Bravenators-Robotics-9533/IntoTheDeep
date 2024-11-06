package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.IntakeComponent;

/*
 * 1. Find and set the intake controller static finals for servo position
 *
 */
@Config
public class IntakeController extends AbstractController {

    public static double INITIAL_PIVOT_X_POSITION   = 0.40;
    public static double FULL_PIVOT_X_POSITION      = 1;

    public static double INITIAL_PIVOT_Y_POSITION   = 1;
    public static double INTAKE_PIVOT_Y_POSITION = 0.25;
    public static double CAPTURE_PIVOT_Y_POSITION = 0.0;

    public static double INITIAL_TENSION_POSITION = 0.75;
    public static double FULL_TENSION_POSITION    = 1;

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

    public void tension() {
        intakeComponent.setTensionServoPosition(FULL_TENSION_POSITION);
    }

    public void release() {
        intakeComponent.setTensionServoPosition(INITIAL_TENSION_POSITION);
    }

    public void togglePivotYPosition() {

        if(intakeComponent.getTargetPivotServoYPosition() == INTAKE_PIVOT_Y_POSITION)
            intakeComponent.setPivotServoYPosition(CAPTURE_PIVOT_Y_POSITION);
        else
            intakeComponent.setPivotServoYPosition(INTAKE_PIVOT_Y_POSITION);

    }
    public void RestPivotYPosition() {

        intakeComponent.setPivotServoYPosition(INTAKE_PIVOT_Y_POSITION);

    }
    public void CapturePivotYPosition() {

        intakeComponent.setPivotServoYPosition(INTAKE_PIVOT_Y_POSITION);

    }
    public void IntakePivotPosition(){

        intakeComponent.setPivotServoYPosition(CAPTURE_PIVOT_Y_POSITION);

    }

}
