package com.bravenatorsrobotics.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class SlideComponent extends AbstractComponent {

    public static int MAX_ENCODER_POSITION = 5000; // Maximum slide extension
    public static int MIN_ENCODER_POSITION = 800;    // Minimum slide retraction

    private int dynamicMinEncoderPosition = MIN_ENCODER_POSITION; // Dynamically updated min position

    public DcMotorEx slideMotor;

    public SlideComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Get Motor
        this.slideMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.SLIDE_MOTOR);

        // Reset Encoders
        this.resetSystemEncoders();

        // Reverse Motor Direction if needed
        this.slideMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set Zero Power Behavior
        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Resets the encoder to 0. Should only be used during initialization.
     */
    public void resetSystemEncoders() {
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Moves the slide to a target encoder position asynchronously.
     *
     * @param position Target encoder position
     * @param power    Motor power (positive value)
     */
    public void setSlidePositionAsync(int position, double power) {
        // Clamp position within encoder limits
        position = Math.max(dynamicMinEncoderPosition, Math.min(MAX_ENCODER_POSITION, position));

        slideMotor.setTargetPosition(position);
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
                (power < 0 && currentPosition <= dynamicMinEncoderPosition)) {
            slideMotor.setPower(0);
            return;
        }

        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setPower(power);
    }

    /**
     * Stops the slide motor.
     */
    public void stop() {
        slideMotor.setPower(0);
    }

    /**
     * Returns the current position of the slide (encoder count).
     *
     * @return Current encoder position
     */
    public int getCurrentPosition() {
        return slideMotor.getCurrentPosition();
    }

    /**
     * Gets the current dynamic minimum encoder position.
     *
     * @return Dynamic minimum encoder position.
     */
    public int getDynamicMinEncoderPosition() {
        return dynamicMinEncoderPosition;
    }
}
