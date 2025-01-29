package com.bravenatorsrobotics.teleop.controlAdapters;

import com.bravenatorsrobotics.hardware.controllers.LiftController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.bravenatorsrobotics.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class TeleopManualControlAdapter implements IControlAdapter {

    private final OpMode opMode;
    private final Robot robot;
    private final StatusLEDControlAdapter statusLEDControlAdapter;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    private FieldCentricDriveControlAdapter driveAdapter;

    private boolean shouldAutoDisableSlowMode = false;

    public TeleopManualControlAdapter(OpMode opMode, Robot robot, StatusLEDControlAdapter statusLEDControlAdapter) {

        this.opMode = opMode;
        this.robot = robot;
        this.statusLEDControlAdapter = statusLEDControlAdapter;

    }

    @Override
    public void initialize() {

        // Setup GamePads
        this.driverGamePad      = new FtcGamePad("Primary Driver", this.opMode.gamepad1, this::onPrimaryDriverGamePadChange);
        this.operatorGamePad    = new FtcGamePad("Secondary Driver", this.opMode.gamepad2, this::onSecondaryDriverGamePadChange);

        this.driveAdapter       = new FieldCentricDriveControlAdapter(this.opMode.gamepad1, this.robot.drive);

    }

    @Override
    public void update() {

        this.driverGamePad.update();
        this.operatorGamePad.update();

        this.driveAdapter.update();
        this.handlePassOffPivotServo();
        this.handlePivotServoX();
        this.handleSlide();

        this.updateStatusLED();

    }

    private void onPrimaryDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    this.driveAdapter.resetOffsetHeading();
                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.driveAdapter.toggleSlowMode();
                    this.shouldAutoDisableSlowMode = false;
                }

                break;

            case FtcGamePad.GAMEPAD_Y:
                if(isPressed) {
                    this.robot.liftController.setLiftEncodersTo0();
                }

                break;

        }

    }

    private void autoDisableSlowMode() {

        if(shouldAutoDisableSlowMode) {

            this.driveAdapter.setSlowModeEnabled(false);
            this.shouldAutoDisableSlowMode = false;

        }

    }

    private void onSecondaryDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_DPAD_DOWN:
                if(isPressed) {

                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
                    this.robot.outtakeController.setPassOffClawOpen();
                    this.robot.outtakeController.setPassOffPivotInitial();
                    this.robot.intakeController.goToPassOffPivotYPosition();
                    this.robot.intakeController.goToInitialPivotXPosition();
                    this.robot.slideController.disableSlowMode();

                    this.autoDisableSlowMode();

                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                if(isPressed) {

                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.LOW_BAR);
                    this.robot.intakeController.goToPassOffPivotYPosition();
                    this.robot.slideController.disableSlowMode();

                    this.autoDisableSlowMode();
                }
                break;

            case FtcGamePad.GAMEPAD_DPAD_UP:

                if(isPressed) {

                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
                    this.robot.intakeController.release();
                    this.robot.intakeController.goToPassOffPivotYPosition();
                    this.robot.slideController.disableSlowMode();

                    this.autoDisableSlowMode();

                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(isPressed) {
                    this.robot.intakeController.togglePivotYPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_A:
                if(isPressed) {
                    this.robot.intakeController.toggleTensionPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_B:
                if(isPressed) {
                    this.robot.intakeController.tensionServosOff();
                }

                break;

            case FtcGamePad.GAMEPAD_X:

                if(isPressed) {

                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.BOTTOM_BASKET);
                    this.robot.outtakeController.setPassOffClawClosed();
                    this.robot.intakeController.goToPassOffPivotYPosition();
                    this.robot.intakeController.release();
                    this.robot.slideController.disableSlowMode();
                    this.shouldAutoDisableSlowMode = true;

                    this.driveAdapter.setSlowModeEnabled(true);

                }

                break;

            case FtcGamePad.GAMEPAD_Y:

                if(isPressed) {

                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET);
                    this.robot.outtakeController.setPassOffClawClosed();
                    this.robot.intakeController.goToPassOffPivotYPosition();
                    this.robot.intakeController.release();
                    this.robot.slideController.disableSlowMode();
                    this.shouldAutoDisableSlowMode = true;

                    this.driveAdapter.setSlowModeEnabled(true);

                }

                break;

            case FtcGamePad.GAMEPAD_LBUMPER:
                if(isPressed) {
                    this.robot.outtakeController.toggleWallClawServoPosition();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.robot.intakeController.release();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_RSTICK_BTN:
                if(isPressed) {
                    this.robot.intakeController.togglePivotXPosition();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_LSTICK_BTN:
                if(isPressed) {
                    this.robot.outtakeController.togglePassOffClaw();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    this.robot.slideController.toggleSlowMode();
                break;

        }

    }

    private void handlePassOffPivotServo() {

        double leftJoystickValue = this.opMode.gamepad2.left_stick_y;

        if (leftJoystickValue > 0.075) { // Joystick input detected
            this.robot.outtakeController.setPassOffPivotInitial();
        } else if (leftJoystickValue < -0.075) {
            this.robot.outtakeController.setPassOffPivotScore();
        }

    }

    private void handlePivotServoX() {

        double joystickValue = this.opMode.gamepad2.right_stick_x;

        if (Math.abs(joystickValue) > 0.02) { // Joystick input detected
            this.robot.intakeController.resetPivotXManualOverride(); // Disable manual override
            this.robot.intakeController.updatePivotXPositionFromJoystick(joystickValue);
        }

    }

    private void updateStatusLED() {

        StatusLEDControlAdapter.State ledState = this.statusLEDControlAdapter.getState();
        boolean isInReleasePosition = this.robot.intakeController.isInReleasePosition();

        if(ledState == StatusLEDControlAdapter.State.DEFAULT && isInReleasePosition)
            this.statusLEDControlAdapter.setState(StatusLEDControlAdapter.State.INDICATE_RELEASE_POSITION);
        else if(ledState == StatusLEDControlAdapter.State.INDICATE_RELEASE_POSITION && !isInReleasePosition)
            this.statusLEDControlAdapter.setState(StatusLEDControlAdapter.State.DEFAULT);

    }

    private void handleSlide() {

        double extendTrigger = this.opMode.gamepad2.right_trigger; // Extend with the right trigger
        double retractTrigger = this.opMode.gamepad2.left_trigger; // Retract with the left trigger

        // Update dynamic minimum position periodically
        this.robot.slideController.update(extendTrigger, retractTrigger);

    }

}
