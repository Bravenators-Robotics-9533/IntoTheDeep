package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.Teleop;
import com.bravenatorsrobotics.components.IntakeComponent;

/*
 * 1. Find and set the intake controller static finals for servo position
 *
 */
@Config
public class IntakeController extends AbstractController {

    public static final double INITIAL_PIVOT_POSITION   = 0;
    public static final double FULL_PIVOT_POSITION      = 0.49;

    public static final double INITIAL_TENSION_POSITION = 0;
    public static final double FULL_TENSION_POSITION    = 0.5;

    protected final IntakeComponent intakeComponent;

    public IntakeController(IntakeComponent intakeComponent) {

        this.intakeComponent = intakeComponent;

    }

    @Override
    public void initialize() {

        this.intakeComponent.setPivotServoPosition(INITIAL_PIVOT_POSITION);
        this.intakeComponent.setTensionServoPosition(INITIAL_TENSION_POSITION);

    }

    @Override public void update() {}

    public void pivotToInitialPivot() { this.intakeComponent.setPivotServoPosition(INITIAL_PIVOT_POSITION); }
    public void pivotToFullPivot() { this.intakeComponent.setPivotServoPosition(FULL_PIVOT_POSITION); }

    public void tensionToInitialTension() { this.intakeComponent.setTensionServoPosition(INITIAL_TENSION_POSITION); }
    public void tensionToFullTension() { this.intakeComponent.setTensionServoPosition(FULL_TENSION_POSITION); }

}
