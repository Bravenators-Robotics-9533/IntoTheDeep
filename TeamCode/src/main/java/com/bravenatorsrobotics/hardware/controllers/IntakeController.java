package com.bravenatorsrobotics.hardware.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.components.IntakeComponent;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class IntakeController extends AbstractController {

    public static double MIN_SAMPLE_POSITION = 13;
    public static double MANUAL_PIVOT_MULTIPLIER = 0.02;

    public static double PASS_OFF_PIVOT_POSITION = 0.5;
    public static double MAX_PIVOT_POSITION = 1;

    public static double PASS_OFF_FLIP_POSITION = 0;
    public static double STANDBY_FLIP_POSITION = 0.55;
    public static double INTAKE_FLIP_POSITION = 1;

    public static double INTAKE_TENSION_POSITION = 0;
    public static double STOP_TENSION_POSITION = 0.5;
    public static double EXPEL_TENSION_POSITION = 1.0;

    private final IntakeComponent intakeComponent;
    private final Telemetry telemetry;

    public IntakeController(IntakeComponent intakeComponent, Telemetry telemetry) {

        this.intakeComponent = intakeComponent;
        this.telemetry = telemetry;

    }

    @Override
    public void initialize() {

        this.intakeComponent.setPivotServoPosition(PASS_OFF_PIVOT_POSITION);
        this.intakeComponent.setFlipServoPosition(PASS_OFF_FLIP_POSITION);
        this.stopIntake();

    }

    @Override
    public void update() {

        if (intakeComponent.getDistanceInMM() < MIN_SAMPLE_POSITION && this.intakeComponent.getTensionServoPosition() == INTAKE_TENSION_POSITION) {
            this.stopIntake();
        }

        this.telemetry.addData("Target Flip Position", this.intakeComponent.getTargetFlipServoPosition());
        this.telemetry.addData("Sample Distance MM", this.intakeComponent.getDistanceInMM());

    }

    public void intakeSample() { this.intakeComponent.setTensionServoPositions(INTAKE_TENSION_POSITION); }
    public void stopIntake() { this.intakeComponent.setTensionServoPositions(STOP_TENSION_POSITION); }

    public boolean isIntakeActive() { return this.intakeComponent.getTensionServoPosition() == INTAKE_TENSION_POSITION; }

    public void expelSample() { this.intakeComponent.setTensionServoPositions(EXPEL_TENSION_POSITION); }

    public boolean isExpellingSample() {return this.intakeComponent.getTensionServoPosition() == EXPEL_TENSION_POSITION; }

    public void toggleSnapPivotPosition() {

        if (this.intakeComponent.getTargetPivotServoPosition() == PASS_OFF_PIVOT_POSITION) {
            this.intakeComponent.setPivotServoPosition(MAX_PIVOT_POSITION);
        } else {
            this.intakeComponent.setPivotServoPosition(PASS_OFF_PIVOT_POSITION);
        }

    }

    public void toggleFlipPosition() {

        if(this.intakeComponent.getTargetFlipServoPosition() == PASS_OFF_FLIP_POSITION) {
            this.intakeComponent.setFlipServoPosition(STANDBY_FLIP_POSITION);
            this.stopIntake();
            return;
        }

        if (this.intakeComponent.getTargetFlipServoPosition() != INTAKE_FLIP_POSITION) {
            this.intakeComponent.setFlipServoPosition(INTAKE_FLIP_POSITION);
            this.intakeSample();
        } else {
            this.intakeComponent.setFlipServoPosition(STANDBY_FLIP_POSITION);
            this.stopIntake();
        }

    }

    public boolean isFlipPositionInPassOff() { return this.intakeComponent.getTargetFlipServoPosition() == PASS_OFF_FLIP_POSITION; }
    public boolean isFlipPositionInIntake() { return this.intakeComponent.getTargetFlipServoPosition() == INTAKE_FLIP_POSITION; }

    public void setFlipPositionToPassOff() { intakeComponent.setFlipServoPosition(PASS_OFF_FLIP_POSITION); }
    public void setFlipPositionToStandby() { intakeComponent.setFlipServoPosition(STANDBY_FLIP_POSITION); }

    public void setPivotPositionToPassOff() { intakeComponent.setPivotServoPosition(PASS_OFF_PIVOT_POSITION); }

    // Method for joystick dynamic control
    public void setManualPivotOffsetPosition(double joystickValue) {

        double position = Range.clip(
                this.intakeComponent.getTargetPivotServoPosition() + (Math.pow(joystickValue, 3) * MANUAL_PIVOT_MULTIPLIER),
                PASS_OFF_PIVOT_POSITION,
                MAX_PIVOT_POSITION
        );

        intakeComponent.setPivotServoPosition(position);

    }


}

