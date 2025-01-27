package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.LiftComponent;
import com.bravenatorsrobotics.components.SlideComponent;
import com.bravenatorsrobotics.components.ControlSystemComponent;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.components.OuttakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.LiftController;
import com.bravenatorsrobotics.controllers.SlideController;
import com.bravenatorsrobotics.controllers.ControlSystemController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.OuttakeController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import roadrunner.drive.MecanumDrive;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class Teleop extends LinearOpMode {

    //MechDrive Controlling
    public static final int DRIVER_CONTROLLER_EASE_POW = 1;
    public static final double ROBOT_SPEED_LIMIT = 1.0;
    public static final double ROBOT_SLOW_SPEED_LIMIT = 0.2;

    private MecanumDrive drive;

    private double offsetHeading = 0.0;


    //Create the gamepads
    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;


    // Controllers
    private ControlSystemController controlSystemController;
    private IntakeController intakeController;
    private OuttakeController outtakeController;
    private LiftController liftController;
    private SlideController slideController;

    private boolean isSlowModeEnabled = false;
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
            this.handleDrive(); // Handle Drive

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
                    offsetHeading = drive.getRawExternalHeading();
                break;



            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    isSlowModeEnabled = !isSlowModeEnabled;
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
            this.isSlowModeEnabled = false;
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
                    autoDisableSlowMode();

                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                if(isPressed) {
                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.LOW_BAR);
                    this.intakeController.goToPassOffPivotYPosition();
                    autoDisableSlowMode();
                }
                break;



            case FtcGamePad.GAMEPAD_DPAD_UP:
                if(isPressed) {
                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
                    this.intakeController.release();
                    this.intakeController.goToPassOffPivotYPosition();
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
                    this.shouldAutoDisableSlowMode = true;
                    this.isSlowModeEnabled = true;
                }

                break;

            case FtcGamePad.GAMEPAD_Y:
                if(isPressed) {
                    this.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET);
                    this.outtakeController.setPassOffClawClosed();
                    this.intakeController.goToPassOffPivotYPosition();
                    this.intakeController.release();
                    this.shouldAutoDisableSlowMode = true;
                    this.isSlowModeEnabled = true;

                }

                break;
            //TODO: This Case May Not Be Necessary
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

    // Field-Centric Mecanum Logic
    private void handleDrive() {

        double y    = -Range.clip(Math.pow(-gamepad1.left_stick_y, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);
        double xt   = Math.pow(gamepad1.right_trigger, DRIVER_CONTROLLER_EASE_POW) - Math.pow(gamepad1.left_trigger, DRIVER_CONTROLLER_EASE_POW);
        double x    = -Range.clip(Math.pow(gamepad1.left_stick_x, DRIVER_CONTROLLER_EASE_POW) + xt, -1.0, 1.0);
        double rx   = Range.clip(Math.pow(gamepad1.right_stick_x, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);

        double botHeading = offsetHeading;

        double rotX = (x * Math.cos(botHeading)) - (y * Math.sin(botHeading));
        double rotY = (x * Math.sin(botHeading)) + (y * Math.cos(botHeading));

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double adjustedSpeedLimit = (isSlowModeEnabled ? ROBOT_SLOW_SPEED_LIMIT : ROBOT_SPEED_LIMIT);

        double flPower  = Range.clip((rotY + rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double blPower  = Range.clip((rotY - rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double frPower  = Range.clip((rotY - rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double brPower =  Range.clip((rotY + rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);

        drive.setMotorPowers(flPower, blPower, brPower, frPower);

    }

}