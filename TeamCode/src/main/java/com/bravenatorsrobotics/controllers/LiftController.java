package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.LiftComponent;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class LiftController extends AbstractController {

    // Telemetry
    private Telemetry telemetry;
    private final LiftComponent liftComponent;

    public LiftController(LiftComponent liftComponent, Telemetry telemetry) {
        this.liftComponent = liftComponent;
        this.telemetry = telemetry;
    }


    //Position Values
    public static double LEFT_REST_POSITION = 0.0;
    public static double RIGHT_REST_POSITION = 0.0;

    public static double LEFT_TOP_BASKET_POSITION = 1.0;
    public static double RIGHT_TOP_BASKET_POSITION = 1.0;

    public static double LEFT_BOTTOM_BASKET_POSITION = 0.0;
    public static double RIGHT_BOTTOM_BASKET_POSITION = 0.0;

    public static double LEFT_HIGH_BAR_POSITION = 0.42;
    public static double RIGHT_HIGH_BAR_POSITION = 0.42;

    public static double LEFT_LOW_BAR_POSITION = 0.28;
    public static double RIGHT_LOW_BAR_POSITION = 0.28;



    //How you call the positions out of this class
    public enum LiftPosition {

        REST(LEFT_REST_POSITION, RIGHT_REST_POSITION),
        TOP_BASKET(LEFT_TOP_BASKET_POSITION, RIGHT_TOP_BASKET_POSITION),
        BOTTOM_BASKET(LEFT_BOTTOM_BASKET_POSITION, RIGHT_BOTTOM_BASKET_POSITION),
        HIGH_BAR(LEFT_HIGH_BAR_POSITION, RIGHT_HIGH_BAR_POSITION),
        LOW_BAR(LEFT_LOW_BAR_POSITION, RIGHT_LOW_BAR_POSITION);

        public final double leftLiftPosition;
        public final double rightLiftPosition;

        LiftPosition(double leftLiftPosition, double rightLiftPosition) {
            this.leftLiftPosition = leftLiftPosition;
            this.rightLiftPosition = rightLiftPosition;
        }

    }

    //Set Max Motor Power (0-1)
    private double LEFT_LIFT_MAX_POWER = 1;
    private double RIGHT_LIFT_MAX_POWER = 1;

    //Function to manipulate max power outside of this class
    public void setLeftLiftMaxPower(double power) {
        this.LEFT_LIFT_MAX_POWER = power;
    }
    public void setRightLiftMaxPower(double power) {
        this.RIGHT_LIFT_MAX_POWER = power;
    }

    //Set Max Encoder Positions
    private static final int LEFT_LIFT_MAX_ENCODER_POSITION = 4000;
    private static final int RIGHT_LIFT_MAX_ENCODER_POSITION = 4000 ;

    private LiftController.LiftPosition targetLiftPosition = LiftController.LiftPosition.REST;


    //In all honesty I have no idea what this does
    public LiftController(LiftComponent liftComponent) {
        this.liftComponent = liftComponent;
    }


    @Override
    public void initialize() {
        this.liftComponent.resetSystemEncoders();
    }

    @Override
    public void update() {

        if (liftComponent.isTouchSensorPressed()) {
            this.liftComponent.resetSystemEncoders();
        }

        double targetLeftLiftPosition = targetLiftPosition.leftLiftPosition;
        double targetRightLiftPosition = targetLiftPosition.leftLiftPosition;


        this.liftComponent.setShoulderMotorPositionAsync((int) (targetLeftLiftPosition * LEFT_LIFT_MAX_ENCODER_POSITION), LEFT_LIFT_MAX_POWER);
        this.liftComponent.setElbowMotorPositionAsync((int) (targetRightLiftPosition * RIGHT_LIFT_MAX_ENCODER_POSITION), RIGHT_LIFT_MAX_POWER);

        printTelemetry();

    }

    public void setTargetLiftPosition(LiftController.LiftPosition liftPosition) {
        this.targetLiftPosition = liftPosition;
    }

    public LiftController.LiftPosition getTargetLiftPosition() {
        return this.targetLiftPosition;
    }

    public void printTelemetry() {

        telemetry.addData("Left Lift Motor", (double) liftComponent.lLiftMotor.getCurrentPosition() ); // add / LEFT_LIFT_MAX_ENCODER_POSITION);
        telemetry.addData("Right Lift Motor", (double) liftComponent.rLiftMotor.getCurrentPosition() ); //add / ELBOW_MAX_ENCODER_POSITION
        telemetry.update();

    }

    public void setLiftEncodersTo0 () { this.liftComponent.resetSystemEncoders(); }



}
