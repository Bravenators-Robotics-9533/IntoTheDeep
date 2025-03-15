package com.bravenatorsrobotics.hardware.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class SlideComponent extends AbstractComponent {

    public static int MAX_ENCODER_POSITION = 1450; // Maximum slide extension
    public static int MIN_ENCODER_POSITION = 130;    // Minimum slide retraction

    public DcMotorEx slideMotor;

    public SlideComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Get Motor
        this.slideMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.SLIDE_MOTOR);

        // Reset Encoders
        this.resetEncoders();

        // Reverse Motor Direction if needed
        this.slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Set Zero Power Behavior
        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.slideMotor.setTargetPositionTolerance(5);
    }

    /**
     * Resets the encoder to 0. Should only be used during initialization.
     */
    private void resetEncoders() {
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Moves the slide to a target encoder position asynchronously.
     * Expects a target position of between 0 and 1. Will automatically clamp to
     * game encoder max/min positions. Not Actually 0 and 1 but whatever is set as max and min
     *
     * @param position Target encoder position
     * @param power    Motor power (positive value)
     */
    public void setSlidePositionAsync(double position, double power) {
        // Clamp position within encoder limits

        int targetPosition = (int) (position * (MAX_ENCODER_POSITION - MIN_ENCODER_POSITION)) + MIN_ENCODER_POSITION;

        slideMotor.setTargetPosition(targetPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(Math.abs(power));

    }

    /**
     * Returns the current position of the slide (encoder count).
     *
     * @return Current encoder position
     */
    public int getCurrentPosition() {
        return slideMotor.getCurrentPosition();
    }

    public boolean isBusy() { return this.slideMotor.isBusy(); }

    public void setEncoderPosition(int position) {
        slideMotor.setTargetPosition(position);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1.0);
    }

}
