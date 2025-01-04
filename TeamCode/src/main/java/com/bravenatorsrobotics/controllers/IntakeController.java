package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.qualcomm.robotcore.util.Range;

/*
 * 1. Find and set the intake controller static finals for servo position
 *
 */
@Config
public class IntakeController extends AbstractController {

    private boolean isPivotXManualOverride = false; // Flag for manual override

    public static final double INITIAL_PIVOT_X_POSITION = 0.5; //Fix Value
    public static final double FULL_PIVOT_X_POSITION = 1; //Fix Value

    public static final double PASS_OFF_PIVOT_Y_POSITION = 0;
    public static final double INTAKE_PIVOT_Y_POSITION = 0.6 ;
    public static final double CAPTURE_PIVOT_Y_POSITION = 1;

    public static final double INITIAL_TENSION_L_POSITION = 0;
    public static final double INITIAL_TENSION_R_POSITION = 1;
    public static final double FULL_TENSION_L_POSITION = 1;
    public static final double FULL_TENSION_R_POSITION = 0;
    public static final double TENSION_SERVO_L_OFF = 0.5;
    public static final double TENSION_SERVO_R_OFF = 0.5;


    protected final IntakeComponent intakeComponent;


    public IntakeController(IntakeComponent intakeComponent) {

        this.intakeComponent = intakeComponent;

    }

    @Override
    public void initialize() {

        this.intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_X_POSITION);
        this.intakeComponent.setPivotServoYPosition(PASS_OFF_PIVOT_Y_POSITION);
        this.intakeComponent.setTensionServoLPosition(TENSION_SERVO_L_OFF);
        this.intakeComponent.setTensionServoRPosition(TENSION_SERVO_R_OFF);

    }

    @Override
    public void update() {

        double distance = intakeComponent.getDistanceInCm();
        String color = intakeComponent.detectBlockColor();
            if (distance < 1.25 && (intakeComponent.getTargetTensionServoLPosition()==INITIAL_TENSION_L_POSITION && (intakeComponent.getTargetTensionServoRPosition()==INITIAL_TENSION_R_POSITION))) {
                 // Replace with your desired threshold
                    tensionServosOff();
            }
    }


    public void tensionServosOff() {
        intakeComponent.setTensionServoLPosition(TENSION_SERVO_L_OFF);
        intakeComponent.setTensionServoRPosition(TENSION_SERVO_R_OFF);
    }

    public void toggleTensionPosition() {

        if ((intakeComponent.getTargetTensionServoLPosition() == INITIAL_TENSION_L_POSITION) && (intakeComponent.getTargetTensionServoRPosition() == INITIAL_TENSION_R_POSITION)) {
            intakeComponent.setTensionServoLPosition(FULL_TENSION_L_POSITION);
            intakeComponent.setTensionServoRPosition(FULL_TENSION_R_POSITION);
        } else {
            intakeComponent.setTensionServoLPosition(INITIAL_TENSION_L_POSITION);
            intakeComponent.setTensionServoRPosition(INITIAL_TENSION_R_POSITION);
        }

    }


    public void tension() {
        intakeComponent.setTensionServoLPosition(INITIAL_TENSION_L_POSITION);
        intakeComponent.setTensionServoRPosition(INITIAL_TENSION_R_POSITION);
    }

    public void release() {
        intakeComponent.setTensionServoLPosition(FULL_TENSION_L_POSITION);
        intakeComponent.setTensionServoRPosition(FULL_TENSION_R_POSITION);
    }


    public void togglePivotXPosition() {
        isPivotXManualOverride = true; // Enable manual override
        if (intakeComponent.getTargetPivotServoXPosition() == INITIAL_PIVOT_X_POSITION) {
            intakeComponent.setPivotServoXPosition(FULL_PIVOT_X_POSITION);
        } else {
            intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_X_POSITION);
        }
    }


    public void togglePivotYPosition() {

        if (intakeComponent.getTargetPivotServoYPosition() == INTAKE_PIVOT_Y_POSITION)
            intakeComponent.setPivotServoYPosition(CAPTURE_PIVOT_Y_POSITION);
        else
            intakeComponent.setPivotServoYPosition(INTAKE_PIVOT_Y_POSITION);

    }

    public void passOffPivotYPosition() {

        intakeComponent.setPivotServoYPosition(PASS_OFF_PIVOT_Y_POSITION);

    }


    public void capturePivotYPosition() {

        intakeComponent.setPivotServoYPosition(CAPTURE_PIVOT_Y_POSITION);

    }

    public void intakePivotYPosition() {

        intakeComponent.setPivotServoYPosition(INTAKE_PIVOT_Y_POSITION);

    }

    public void initialPivotXPosition(){

        intakeComponent.setPivotServoXPosition(INITIAL_PIVOT_X_POSITION);

    }

    // Method for joystick dynamic control
    public void updatePivotXPositionFromJoystick(double joystickValue) {
        if (!isPivotXManualOverride) { // Only update if not in manual override
            double position = Range.clip(
                    INITIAL_PIVOT_X_POSITION + joystickValue * (FULL_PIVOT_X_POSITION - INITIAL_PIVOT_X_POSITION),
                    INITIAL_PIVOT_X_POSITION,
                    FULL_PIVOT_X_POSITION
            );
            intakeComponent.setPivotServoXPosition(position);
        }
    }

    // Method to reset manual override
    public void resetPivotXManualOverride() {
        isPivotXManualOverride = false;
    }


}

