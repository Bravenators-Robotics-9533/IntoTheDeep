package com.bravenatorsrobotics.teleop.drivecontrollers;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import roadrunner.drive.MecanumDrive;

public class FieldCentricDriveController extends DriveController {

    private static final int DRIVER_CONTROLLER_EASE_POW = 1;
    private static final double ROBOT_SPEED_LIMIT = 1.0;
    private static final double ROBOT_SLOW_SPEED_LIMIT = 0.2;

    protected final MecanumDrive drive;

    private double offsetHeading = 0.0;
    private boolean isSlowModeEnabled = false;

    public FieldCentricDriveController(Gamepad gamepad, MecanumDrive drive) {

        super(gamepad);

        this.drive = drive;

    }

    public void update() {

        double y    = -Range.clip(Math.pow(-gamepad.left_stick_y, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);
        double xt   = Math.pow(gamepad.right_trigger, DRIVER_CONTROLLER_EASE_POW) - Math.pow(gamepad.left_trigger, DRIVER_CONTROLLER_EASE_POW);
        double x    = -Range.clip(Math.pow(gamepad.left_stick_x, DRIVER_CONTROLLER_EASE_POW) + xt, -1.0, 1.0);
        double rx   = Range.clip(Math.pow(gamepad.right_stick_x, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);

        double botHeading = offsetHeading - this.drive.getRawExternalHeading();

        double rotX = (x * Math.cos(botHeading)) - (y * Math.sin(botHeading));
        double rotY = (x * Math.sin(botHeading)) + (y * Math.cos(botHeading));

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double adjustedSpeedLimit = (isSlowModeEnabled ? ROBOT_SLOW_SPEED_LIMIT : ROBOT_SPEED_LIMIT);

        double flPower  = Range.clip((rotY + rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double blPower  = Range.clip((rotY - rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double frPower  = Range.clip((rotY - rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double brPower =  Range.clip((rotY + rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);

        this.drive.setMotorPowers(flPower, blPower, brPower, frPower);

    }

    public void setSlowModeEnabled(boolean isSlowModeEnabled) { this.isSlowModeEnabled = isSlowModeEnabled; }
    public void toggleSlowMode() { this.isSlowModeEnabled = !this.isSlowModeEnabled; }

    public boolean getIsSlowModeEnabled() { return this.isSlowModeEnabled; }

    public void resetOffsetHeading() { this.offsetHeading = this.drive.getRawExternalHeading(); }

}
