package com.bravenatorsrobotics;

import com.bravenatorsrobotics.components.ControlSystemComponent;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.ControlSystemController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@TeleOp(name = "Teleop", group = "Competition")
public class Teleop extends LinearOpMode {

    public static final int DRIVER_CONTROLLER_EASE_POW = 1;
    public static final double ROBOT_SPEED_LIMIT = 1.0;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    private MecanumDrive drive;

    private double offsetHeading = 0.0;

    // Components
    private IntakeComponent intakeComponent;

    // Controllers
    private ControlSystemController controlSystemController;
    private IntakeController intakeController;

    private void initialize() {
        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

/* ================================================================================================================================
 * INSTRUCTIONS FOR MARINA
 * HOW TO GET THE CURRENT ALLIANCE COLOR
 * LOOK BELOW
 */
        if(ConfigMap.getAllianceColor() == ConfigMap.AllianceColor.RED) {
            System.out.println("Hey it's configured to red");
        } else {
            System.out.println("Hey it's configured to blue");
        }
/*
 * DELETE THE ABOVE LINES WHEN YOU UNDERSTAND OR COMMENT THEM OUT
 * ================================================================================================================================
 */

        // Get the GamePads
        this.driverGamePad      = new FtcGamePad("Driver", gamepad1, this::onDriverGamePadChange);
        this.operatorGamePad    = new FtcGamePad("Operator", gamepad2, this::onOperatorGamePadChange);

        // Create Mecanum Drive
        this.drive = new MecanumDrive(super.hardwareMap);
        this.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize our components
        ControlSystemComponent controlSystemComponent = new ControlSystemComponent(super.hardwareMap);
        this.intakeComponent = new IntakeComponent(super.hardwareMap);

        // Create and Initialize our controllers
        this.controlSystemController = new ControlSystemController(controlSystemComponent, ControlSystemController.Strategy.MANUAL);
        this.controlSystemController.initialize();

        this.intakeController = new IntakeController(intakeComponent);
        this.intakeController.initialize();

        // Initialize the vision system (if needed)

        // Initialize any autonomous controlled teleop sequences
    }

    // Main method that gets called when press init & start
    @Override
    public void runOpMode() throws InterruptedException {

        // Print to screen
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

       this.initialize(); // Initialize all of the things

        // Print status
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart(); // Wait for the start button to be pressed

        while(opModeIsActive()) { // Loop until not active

            // Control System Controller Must Update First
            this.controlSystemController.update(); // FIRST THING THAT HAPPENS IN UPDATE LOOP

            this.handleDrive(); // Handle Drive

            // Handle the GamePads
            this.driverGamePad.update();
            this.operatorGamePad.update();

            // Update the component controllers
            this.intakeController.update();

        }

    }

    private void onDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    offsetHeading = drive.getRawExternalHeading();
                break;

        }

    }

    private void onOperatorGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_B:
                if(!isPressed) {
                    if (this.intakeComponent.getTargetPivotServoPosition() == IntakeController.INITIAL_PIVOT_POSITION) {
                        this.intakeController.pivotToFullPivot();
                    } else {
                        this.intakeController.pivotToInitialPivot();
                    }
                }
                break;

            case FtcGamePad.GAMEPAD_A:
                if(!isPressed) {
                    if(this.intakeComponent.getTargetTensionServoPosition() == IntakeController.INITIAL_TENSION_POSITION) {
                        this.intakeController.tensionToFullTension();
                    } else {
                        this.intakeController.tensionToInitialTension();
                    }
                }
                break;
        }

    }

    // Field-Centric Mecanum Logic
    private void handleDrive() {

        double y    = Range.clip(Math.pow(-gamepad1.left_stick_y, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);
        double xt   = Math.pow(gamepad1.right_trigger, DRIVER_CONTROLLER_EASE_POW) - Math.pow(gamepad1.left_trigger, DRIVER_CONTROLLER_EASE_POW);
        double x    = Range.clip(Math.pow(gamepad1.left_stick_x, DRIVER_CONTROLLER_EASE_POW) + xt, -1.0, 1.0);
        double rx   = Range.clip(Math.pow(gamepad1.right_stick_x, DRIVER_CONTROLLER_EASE_POW), -1.0, 1.0);

        double botHeading = -drive.getRawExternalHeading() + offsetHeading;

        double rotX = (x * Math.cos(botHeading)) - (y * Math.sin(botHeading));
        double rotY = (x * Math.sin(botHeading)) + (y * Math.cos(botHeading));

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double adjustedSpeedLimit = ROBOT_SPEED_LIMIT;

        // If the lift is up change the speed limit to 0.2

        double flPower  = Range.clip((rotY + rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double blPower  = Range.clip((rotY - rotX + rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double frPower  = Range.clip((rotY - rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);
        double brPower =  Range.clip((rotY + rotX - rx) / denominator, -adjustedSpeedLimit, adjustedSpeedLimit);

        drive.setMotorPowers(flPower, blPower, brPower, frPower);

    }

}
