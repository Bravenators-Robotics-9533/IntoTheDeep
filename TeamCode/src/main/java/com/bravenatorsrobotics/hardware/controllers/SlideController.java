package com.bravenatorsrobotics.hardware.controllers;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.bravenatorsrobotics.hardware.components.SlideComponent;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class SlideController {

    public static double P = 0.1;
    private static final double MAX_SLIDE_POWER = 1.0; // Default slide power
    private static final int SLIDE_TOLERANCE = 5;

    private final SlideComponent slideComponent;
    private final Telemetry telemetry;

    private double targetPosition = 0;

    public SlideController(SlideComponent slideComponent, Telemetry telemetry) {
        this.slideComponent = slideComponent;
        this.telemetry = telemetry;
    }

    public void initialize() {}

    /**
     * Updates the slide movement based on operator input.
     */
    public void update() {

        this.slideComponent.setSlidePositionAsync(this.targetPosition, MAX_SLIDE_POWER);

        telemetry.addData("Slide Position", this.slideComponent.getCurrentPosition());

    }

    public void setManualSlidePower(double manualSlidePower) {
        this.targetPosition += P * manualSlidePower;
        this.targetPosition = Range.clip(this.targetPosition, 0.0, 1.0);
    }

    /**
     * Sets and moves slide position async by value
     *
     * @param position range between 0.0 and 1.0
     */
    public void setSlidePosition(double position) {

        this.targetPosition = Range.clip(position, 0.0, 1.0);

    }

    public SlideComponent getSlideComponent() { return this.slideComponent; }

    public boolean isBusy() { return Math.abs(this.targetPosition - this.slideComponent.getCurrentPosition()) <= SLIDE_TOLERANCE; }

    public class SlideOutAction implements Action {

        private final double position;

        public SlideOutAction(double position) {
            this.position = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            targetPosition = this.position;

            update();

            return slideComponent.isBusy();

        }

    }

    public SlideOutAction slideOutAction(double position) { return new SlideOutAction(position); }

    public void resetPosition() {



    }

}
