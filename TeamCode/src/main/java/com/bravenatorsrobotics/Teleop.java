package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.ArmComponent;
import com.bravenatorsrobotics.components.ControlSystemComponent;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.ArmController;
import com.bravenatorsrobotics.controllers.ControlSystemController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class Teleop extends LinearOpMode {

    public static final int DRIVER_CONTROLLER_EASE_POW = 1;
    public static final double ROBOT_SPEED_LIMIT = 1.0;
    public static final double ROBOT_SLOW_SPEED_LIMIT = 0.2;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    private MecanumDrive drive;

    private double offsetHeading = 0.0;

    // Controllers
    private ControlSystemController controlSystemController;
    private IntakeController intakeController;
    private ArmController armController;

    private boolean isSlowModeEnabled = false;
    private boolean shouldAutoDisableSlowMode = false;

    private void initialize() {

        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

        // Setup the GamePads
        this.driverGamePad      = new FtcGamePad("Driver", gamepad1, this::onDriverGamePadChange);
        this.operatorGamePad    = new FtcGamePad("Operator", gamepad2, this::onOperatorGamePadChange);

        // Create Mecanum Drive
        this.drive = new MecanumDrive(super.hardwareMap);
        this.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize our components
        ControlSystemComponent controlSystemComponent = new ControlSystemComponent(super.hardwareMap);
        IntakeComponent intakeComponent = new IntakeComponent(super.hardwareMap);
        ArmComponent armComponent = new ArmComponent(super.hardwareMap);

        // Create the controllers
        this.controlSystemController = new ControlSystemController(controlSystemComponent, ControlSystemController.Strategy.MANUAL);
        this.intakeController = new IntakeController(intakeComponent);
        this.armController = new ArmController(armComponent);

        // Initialize the controllers
        this.controlSystemController.initialize();
        this.intakeController.initialize();
        this.armController.initialize();

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

            // Update the component controllers
            this.armController.update();
            this.intakeController.update();

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

            case FtcGamePad.GAMEPAD_Y:
                if(isPressed)
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.HANG);
                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    isSlowModeEnabled = !isSlowModeEnabled;
                    shouldAutoDisableSlowMode = false;
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
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.REST);
                    this.intakeController.PivotYRestPosition();
                    autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.BOTTOM_BAR);
                    autoDisableSlowMode();
                }
                break;

            case FtcGamePad.GAMEPAD_DPAD_UP:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.HIGH_BAR);
                    autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(isPressed) {
                    this.intakeController.togglePivotXPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_A:
                if(isPressed) {
                    this.intakeController.toggleTensionPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_B:
                if(isPressed) {
                    this.intakeController.togglePivotYPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_X:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.BOTTOM_BASKET);
                    this.shouldAutoDisableSlowMode = true;
                    this.isSlowModeEnabled = true;
                }

                break;

            case FtcGamePad.GAMEPAD_Y:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.TOP_BASKET);
                    this.shouldAutoDisableSlowMode = true;
                    this.isSlowModeEnabled = true;
                }

                break;

            case FtcGamePad.GAMEPAD_LBUMPER:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.INTAKE);
                    this.intakeController.TelePivotYIntakePosition();
                    this.autoDisableSlowMode();
                }

                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.armController.setTargetArmPosition(ArmController.ArmPosition.CAPTURE);
                    this.intakeController.TelePivotYIntakePosition();
                    this.autoDisableSlowMode();
                }

                break;

        }

    }

    // Field-Centric Mecanum Logic
    private void handleDrive() {

        double y    = -Range.clip(Math.pow(-gamepad1.left_stick_y, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);
        double xt   = Math.pow(gamepad1.right_trigger, DRIVER_CONTROLLER_EASE_POW) - Math.pow(gamepad1.left_trigger, DRIVER_CONTROLLER_EASE_POW);
        double x    = -Range.clip(Math.pow(gamepad1.left_stick_x, DRIVER_CONTROLLER_EASE_POW) + xt, -1.0, 1.0);
        double rx   = Range.clip(Math.pow(gamepad1.right_stick_x, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);

        double botHeading = -drive.getRawExternalHeading() + offsetHeading;

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