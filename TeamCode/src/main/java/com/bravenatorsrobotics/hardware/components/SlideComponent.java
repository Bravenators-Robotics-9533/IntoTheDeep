package com.bravenatorsrobotics.hardware.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class SlideComponent extends AbstractComponent {

    public static int MAX_ENCODER_POSITION = 1500; // Maximum slide extension
    public static int MIN_ENCODER_POSITION = 250;    // Minimum slide retraction

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

        int targetPosition = (int) (position * (MAX_ENCODER_POSITION - MIN_ENCODER_POSITION)) + MAX_ENCODER_POSITION;

        slideMotor.setTargetPosition(targetPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(Math.abs(power));
    }

    /**
     * Sets the motor power directly for manual control.
     *
     * @param power Motor power (-1.0 to 1.0)
     */
    public void setManualPower(double power) {
        int currentPosition = slideMotor.getCurrentPosition();

        // Clamp movement based on encoder limits
        if ((power > 0 && currentPosition >= MAX_ENCODER_POSITION) ||
                (power < 0 && currentPosition <= MIN_ENCODER_POSITION)) {
            slideMotor.setPower(0);
        } else {
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor.setPower(power);
        }

    }

    /**
     * Returns the current position of the slide (encoder count).
     *
     * @return Current encoder position
     */
    public int getCurrentPosition() {
        return slideMotor.getCurrentPosition();
    }

}
