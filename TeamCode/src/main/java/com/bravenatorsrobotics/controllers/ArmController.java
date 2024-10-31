package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.ArmComponent;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class ArmController extends AbstractController {

    public static double SAFE_SHOULDER_POSITION = 1.0;
    public static double SAFE_ELBOW_POSITION = 0.0;

    private static final double MAX_SHOULDER_POWER = 0.5;
    private static final int SHOULDER_MAX = 1245;

    private static final double MAX_ELBOW_POWER = 0.5;
    private static final int ELBOW_MAX = 1335;

    private double targetElbowPosition = 0;
    private double targetShoulderPosition = 0;

    private final ArmComponent armComponent;

    public ArmController(ArmComponent armComponent) {
        this.armComponent = armComponent;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void update() {

        double adjustedTargetElbowPosition = targetElbowPosition;
        double adjustedTargetShoulderPosition = targetShoulderPosition;

        if (targetElbowPosition > SAFE_ELBOW_POSITION && this.armComponent.shoulderMotor.getCurrentPosition() < SAFE_SHOULDER_POSITION) {
            adjustedTargetElbowPosition = SAFE_ELBOW_POSITION;
        }

        this.armComponent.setShoulderMotorPositionAsync((int) (adjustedTargetShoulderPosition * SHOULDER_MAX), MAX_SHOULDER_POWER);
        this.armComponent.setElbowMotorPositionAsync((int) (adjustedTargetElbowPosition * ELBOW_MAX), MAX_ELBOW_POWER);

    }

    /**
     * @param position [0, 1] inclusive
     */
    public void setShoulderPosition(double position) {

        this.targetShoulderPosition = position;

//        int targetMotorPosition = (int) (position * (double) SHOULDER_MAX);
//        this.armComponent.setShoulderMotorPositionAsync(targetMotorPosition, MAX_SHOULDER_POWER);

    }

    /**
     * @param position [0, 1] inclusive
     */
    public void setElbowPosition(double position) {

        this.targetElbowPosition = position;

//        int targetMotorPosition = (int) (position * (double) ELBOW_MAX);
//
//        this.armComponent.setElbowMotorPositionAsync(targetMotorPosition, MAX_ELBOW_POWER);

    }

    public void printTelemetry(Telemetry telemetry) {

        telemetry.addData("Shoulder Motor", (double) armComponent.shoulderMotor.getCurrentPosition() / SHOULDER_MAX);
        telemetry.addData("Elbow Motor", (double) armComponent.elbowMotor.getCurrentPosition() / ELBOW_MAX);
        telemetry.update();

    }

}
