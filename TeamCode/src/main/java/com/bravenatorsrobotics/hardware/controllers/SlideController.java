package com.bravenatorsrobotics.hardware.controllers;

import com.bravenatorsrobotics.hardware.components.SlideComponent;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SlideController {

    public enum State {
        MANUAL,
        AUTO
    }

    private static final double MAX_SLIDE_POWER = 1.0; // Default slide power

    private final SlideComponent slideComponent;
    private final Telemetry telemetry;

    private State state = State.MANUAL;

    private double manualSlidePower = 0.0;

    public SlideController(SlideComponent slideComponent, Telemetry telemetry) {
        this.slideComponent = slideComponent;
        this.telemetry = telemetry;
    }

    public void initialize() {}

    /**
     * Updates the slide movement based on operator input.
     */
    public void update() {

        if(this.state == State.MANUAL) {
            // Apply manual control
            slideComponent.setManualPower(manualSlidePower * MAX_SLIDE_POWER);
        }

        telemetry.addData("Slide Position", this.slideComponent.getCurrentPosition());

    }

    public void setState(State state) { this.state = state; }
    public State getState() { return this.state; }

    public void setManualSlidePower(double manualSlidePower) { this.manualSlidePower = manualSlidePower; }

}
