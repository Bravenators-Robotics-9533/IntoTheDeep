package com.bravenatorsrobotics.hardware.controllers;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.bravenatorsrobotics.hardware.components.LiftComponent;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    public static double HIGH_BAR_POSITION = 0.45;
    public static double LOW_BAR_POSITION = 0.28;

    public static double HIGH_BAR_RELEASE_POSITION = 0.3;

    //How you call the positions out of this class
    public enum LiftPosition {

        REST(REST_POSITION),
        TOP_BASKET(TOP_BASKET_POSITION),
        BOTTOM_BASKET(BOTTOM_BASKET_POSITION),
        HIGH_BAR(HIGH_BAR_POSITION),
        HIGH_BAR_RELEASE(HIGH_BAR_RELEASE_POSITION),
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
        telemetry.addData("Left Lift Motor", (double) liftComponent.lLiftMotor.getCurrentPosition() );
        telemetry.addData("Right Lift Motor", (double) liftComponent.rLiftMotor.getCurrentPosition() );

    }

    public class LiftToHighBarAction implements Action {

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            targetLiftPosition = LiftPosition.HIGH_BAR;
            update();

            return liftComponent.isBusy();

        }

    }

    public class LiftToHighBarReleasePosition implements Action {

        private static final double TIMEOUT_SECONDS = 0.4;

        private boolean isInitialized = false;
        private final ElapsedTime timer = new ElapsedTime();

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            if(!isInitialized) {
                timer.reset();
                isInitialized = true;
            }

            targetLiftPosition = LiftPosition.HIGH_BAR_RELEASE;
            update();

            return timer.seconds() < TIMEOUT_SECONDS;

        }

    }

    public class LiftToRestAction implements Action {

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            targetLiftPosition = LiftPosition.REST;
            update();

            return liftComponent.isBusy();

        }

    }

    public Action liftToHighBarAction() { return new LiftToHighBarAction(); }
    public Action liftToHighBarReleasePosition() { return new LiftToHighBarReleasePosition(); }
    public Action liftToRestAction() { return new LiftToRestAction(); }

}
