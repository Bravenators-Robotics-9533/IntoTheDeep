package com.bravenatorsrobotics.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.components.LiftComponent;
import com.bravenatorsrobotics.hardware.components.SlideComponent;
import com.bravenatorsrobotics.hardware.components.ControlSystemComponent;
import com.bravenatorsrobotics.hardware.components.IntakeComponent;
import com.bravenatorsrobotics.hardware.components.OuttakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.hardware.controllers.LiftController;
import com.bravenatorsrobotics.hardware.controllers.SlideController;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.hardware.controllers.IntakeController;
import com.bravenatorsrobotics.hardware.controllers.OuttakeController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.bravenatorsrobotics.teleop.drivecontrollers.FieldCentricDriveController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import roadrunner.drive.MecanumDrive;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class TeleopOpMode extends LinearOpMode {

    private MecanumDrive drive;
    private FieldCentricDriveController driveController;

    // Create the GamePads
    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    // Controllers
    private ControlSystemController controlSystemController;
    private IntakeController intakeController;
    private OuttakeController outtakeController;
    private LiftController liftController;
    private SlideController slideController;

    private boolean shouldAutoDisableSlowMode = false;

    private void initialize() {

        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

        // Setup the GamePads
        this.driverGamePad = new FtcGamePad("Driver", gamepad1, this::onDriverGamePadChange);
        this.operatorGamePad = new FtcGamePad("Operator", gamepad2, this::onOperatorGamePadChange);

        // Create Mecanum Drive
        this.drive = new MecanumDrive(super.hardwareMap);
        this.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Create Drive Controller
        this.driveController = new FieldCentricDriveController(super.gamepad1, this.drive);

        // Initialize our components
        ControlSystemComponent controlSystemComponent = new ControlSystemComponent(super.hardwareMap);
        IntakeComponent intakeComponent = new IntakeComponent(super.hardwareMap);
        OuttakeComponent outtakeComponent = new OuttakeComponent(super.hardwareMap);
        LiftComponent liftComponent = new LiftComponent(super.hardwareMap);
        SlideComponent slideComponent = new SlideComponent(super.hardwareMap);

        // Create the controllers
        this.controlSystemController = new ControlSystemController(controlSystemComponent, ControlSystemController.Strategy.MANUAL);
        this.intakeController = new IntakeController(intakeComponent);
        this.outtakeController = new OuttakeController(outtakeComponent);
        this.liftController = new LiftController(liftComponent, telemetry);
        this.slideController = new SlideController(slideComponent, telemetry);

        // Initialize the controllers
        this.controlSystemController.initialize();
        this.intakeController.initialize();
        this.outtakeController.initialize();
        this.liftController.initialize();
        this.slideController.initialize();

        // Initialize the vision system (if needed)

        // Initialize any autonomous controlled teleop sequences
        telemetry.addData("Status", "Initialized!");
        telemetry.update();

    }

    private void runUpdateLoop() {

        while(opModeIsActive()) { // Loop until stop pressed

            // Revalidate Component Cache
            this.controlSystemController.update();

            // Handle the GamePads
            this.driverGamePad.update();
            this.operatorGamePad.update();

            // Update Status LED
            this.updateStatusLED();

            // Update the component controllers
            this.liftController.update();
            this.intakeController.update();
            this.outtakeController.update();

            this.handlePassOffPivotServo();
            this.handlePivotServoX();
            this.handleSlide();

            this.driveController.update();

        }

    }

    private void onStop() {

    }

    // Main method that gets called when init is pressed
    @Override
    public void runOpMode() throws InterruptedException {

        // Init Teleop
        this.initialize();

        // Wait for the start button to be pressed
        super.waitForStart(); // Blocks thread

        // Wait and capture the program for the update loop (while running this will block thread)
        this.runUpdateLoop();

        // Handle Close Down Sequence
        this.onStop();

    }

    private void onDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    this.driveController.resetOffsetHeading();
                break;



            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.driveController.toggleSlowMode();
                    shouldAutoDisableSlowMode = false;
                }

                break;

            case FtcGamePad.GAMEPAD_Y:
                if(isPressed) {
                    liftController.setLiftEncodersTo0();
                }

                break;

        }

    }

    private void autoDisableSlowMode() {

        if(shouldAutoDisableSlowMode) {

            this.driveController.setSlowModeEnabled(false);
            this.shouldAutoDisableSlowMode = false;

        }

    }

    private void onOperatorGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_DPAD_DOWN:
                if(isPressed) {

                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
                    this.outtakeController.setPassOffClawOpen();
                    this.outtakeController.setPassOffPivotInitial();
                    this.intakeController.goToPassOffPivotYPosition();
                    this.intakeController.goToInitialPivotXPosition();
                    this.slideController.disableSlowMode();
                    autoDisableSlowMode();

                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                if(isPressed) {
                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.LOW_BAR);
                    this.intakeController.goToPassOffPivotYPosition();
                    this.slideController.disableSlowMode();
                    autoDisableSlowMode();
                }
                break;



            case FtcGamePad.GAMEPAD_DPAD_UP:
                if(isPressed) {
                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
                    this.intakeController.release();
                    this.intakeController.goToPassOffPivotYPosition();
                    this.slideController.disableSlowMode();
                    autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(isPressed) {
                    this.intakeController.togglePivotYPosition();

                }

                break;

            case FtcGamePad.GAMEPAD_A:
                if(isPressed) {
                    this.intakeController.toggleTensionPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_B:
                if(isPressed) {
                    this.intakeController.tensionServosOff();
                }

                break;

            case FtcGamePad.GAMEPAD_X:

                if(isPressed) {

                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.BOTTOM_BASKET);
                    this.outtakeController.setPassOffClawClosed();
                    this.intakeController.goToPassOffPivotYPosition();
                    this.intakeController.release();
                    this.slideController.disableSlowMode();
                    this.shouldAutoDisableSlowMode = true;

                    this.driveController.setSlowModeEnabled(true);

                }

                break;

            case FtcGamePad.GAMEPAD_Y:

                if(isPressed) {

                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET);
                    this.outtakeController.setPassOffClawClosed();
                    this.intakeController.goToPassOffPivotYPosition();
                    this.intakeController.release();
                    this.slideController.disableSlowMode();
                    this.shouldAutoDisableSlowMode = true;

                    this.driveController.setSlowModeEnabled(true);

                }

                break;

            case FtcGamePad.GAMEPAD_LBUMPER:
                if(isPressed) {
                    this.outtakeController.toggleWallClawServoPosition();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.intakeController.release();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_RSTICK_BTN:
                if(isPressed) {
                    this.intakeController.togglePivotXPosition();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_LSTICK_BTN:
                if(isPressed) {
                    this.outtakeController.togglePassOffClaw();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    slideController.toggleSlowMode();
                break;


        }

    }

    private void handlePassOffPivotServo() {
        double leftJoystickValue = gamepad2.left_stick_y;
        if (leftJoystickValue > 0.075) { // Joystick input detected
            outtakeController.setPassOffPivotInitial();
        } else if (leftJoystickValue < -0.075) {
            outtakeController.setPassOffPivotScore();
        }
    }

    private void handlePivotServoX() {
        double joystickValue = gamepad2.right_stick_x;

        if (Math.abs(joystickValue) > 0.02) { // Joystick input detected
            intakeController.resetPivotXManualOverride(); // Disable manual override
            intakeController.updatePivotXPositionFromJoystick(joystickValue);
        }
    }

    private void updateStatusLED() {

        if(this.intakeController.isInReleasePosition()) {
            this.gamepad1.rumble(100);
            this.gamepad2.rumble(100);
            this.gamepad1.setLedColor(180, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);
            this.gamepad2.setLedColor(180, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);
        } else {
            this.gamepad1.setLedColor(0, 0, 255, Gamepad.LED_DURATION_CONTINUOUS);
            this.gamepad2.setLedColor(255, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
        }


    }

    private void handleSlide() {

        double extendTrigger = gamepad2.right_trigger; // Extend with the right trigger
        double retractTrigger = gamepad2.left_trigger; // Retract with the left trigger

        // Update dynamic minimum position periodically
        slideController.update(extendTrigger, retractTrigger);
        slideController.update(extendTrigger, retractTrigger);

    }

}