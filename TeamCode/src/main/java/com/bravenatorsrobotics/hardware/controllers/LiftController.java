package com.bravenatorsrobotics.hardware.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.components.LiftComponent;

import org.firstinspires.ftc.robotcore.external.Telemetry;

// TODO: Move lift by velocity

@Config
public class LiftController extends AbstractController {

    // Telemetry
    private final Telemetry telemetry;
    private final LiftComponent liftComponent;

    public LiftController(LiftComponent liftComponent, Telemetry telemetry) {
        this.liftComponent = liftComponent;
        this.telemetry = telemetry;
    }

    private static final int LIFT_MAX_ENCODER_POSITION = 4000;
    private static final double LIFT_MAX_POWER = 1.0;

    // Position Values
    public static double REST_POSITION = 0.0;
    public static double TOP_BASKET_POSITION = 1.0;
    public static double BOTTOM_BASKET_POSITION = 0.0;
    public static double HIGH_BAR_POSITION = 0.42;
    public static double LOW_BAR_POSITION = 0.28;

    //How you call the positions out of this class
    public enum LiftPosition {

        REST(REST_POSITION),
        TOP_BASKET(TOP_BASKET_POSITION),
        BOTTOM_BASKET(BOTTOM_BASKET_POSITION),
        HIGH_BAR(HIGH_BAR_POSITION),
        LOW_BAR(LOW_BAR_POSITION);

        public final double liftPosition;

        LiftPosition(double liftPosition) {
            this.liftPosition = liftPosition;
        }

    }

    private LiftController.LiftPosition targetLiftPosition = LiftController.LiftPosition.REST;

    @Override
    public void initialize() {
        this.liftComponent.resetSystemEncoders();
    }

    @Override
    public void update() {

        this.liftComponent.setLiftPositionAsync((int) (targetLiftPosition.liftPosition * LIFT_MAX_ENCODER_POSITION), LIFT_MAX_POWER);
        this.printTelemetry();

    }

    public void setTargetLiftPosition(LiftController.LiftPosition liftPosition) {
        this.targetLiftPosition = liftPosition;
    }

    public void printTelemetry() {

        telemetry.addData("Left Target Position", targetLiftPosition.liftPosition * LIFT_MAX_ENCODER_POSITION);
        telemetry.addData("Left Lift Motor", (double) liftComponent.lLiftMotor.getCurrentPosition() ); // add / LEFT_LIFT_MAX_ENCODER_POSITION);
        telemetry.addData("Right Lift Motor", (double) liftComponent.rLiftMotor.getCurrentPosition() ); //add / ELBOW_MAX_ENCODER_POSITION
        telemetry.update();

    }

}
