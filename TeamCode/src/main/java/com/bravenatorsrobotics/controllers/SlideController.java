package com.bravenatorsrobotics.controllers;

import com.bravenatorsrobotics.components.SlideComponent;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SlideController {

    private final SlideComponent slideComponent;
    private final Telemetry telemetry;
    private static final double SLIDE_POWER = 1.0; // Default slide power
    private int targetPosition; // Target encoder position

    public SlideController(SlideComponent slideComponent, Telemetry telemetry) {
        this.slideComponent = slideComponent;
        this.telemetry = telemetry;
        this.targetPosition = slideComponent.getCurrentPosition();
    }

    /**
     * Initializes the slide controller.
     */
    public void initialize() {
        // Ensure the slide is stopped and encoder values are reset
        slideComponent.resetSystemEncoders();
    }

    /**
     * Updates the slide movement based on operator input.
     *
     * @param extendTrigger Value of the right trigger (0.0 to 1.0) for extending the slide.
     * @param retractTrigger Value of the left trigger (0.0 to 1.0) for retracting the slide.
     */
    public void update(double extendTrigger, double retractTrigger) {
        // Calculate power based on triggers
        double slidePower = extendTrigger - retractTrigger;

        // Apply manual control
        slideComponent.setManualPower(slidePower * SLIDE_POWER);

        // Optionally, update telemetry for debugging
        telemetry.addData("Slide Position", slideComponent.getCurrentPosition());
    }

    /**
     * Moves the slide to a specific encoder position.
     *
     * @param position Target encoder position.
     */
    public void moveToPosition(int position) {
        position = Range.clip(position,
                SlideComponent.MIN_ENCODER_POSITION,
                SlideComponent.MAX_ENCODER_POSITION);
        this.targetPosition = position;

        slideComponent.setSlidePositionAsync(targetPosition, SLIDE_POWER);
    }

    /**
     * Stops the slide immediately.
     */
    public void stop() {
        slideComponent.stop();
    }

    /**
     * Gets the current encoder position of the slide.
     *
     * @return Current encoder position.
     */
    public int getCurrentPosition() {
        return slideComponent.getCurrentPosition();
    }
}
