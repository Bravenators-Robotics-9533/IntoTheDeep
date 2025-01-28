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
import com.bravenatorsrobotics.robot.Robot;
import com.bravenatorsrobotics.teleop.controlAdapters.FieldCentricDriveControlAdapter;
import com.bravenatorsrobotics.teleop.controlAdapters.TeleopManualControlAdapter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import roadrunner.drive.MecanumDrive;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class TeleopOpMode extends LinearOpMode {

    private final TeleopStateManager stateManager;

    private Robot robot;
    private TeleopManualControlAdapter manualControlAdapter;

    public TeleopOpMode() {

        this.stateManager = new TeleopStateManager();

    }

    private void initialize() {

        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

        this.robot = new Robot(this, this::onUpdate);
        this.robot.initialize();

        // Create Manual Control Adapter
        this.manualControlAdapter = new TeleopManualControlAdapter(this, this.robot);
        this.manualControlAdapter.initialize();

    }

    private void onUpdate() {

        // Update Controller Adapters
        if(this.stateManager.getState() == TeleopState.MANUAL)
            this.manualControlAdapter.update();

    }

    private void onStop() {

    }

    // Main method that gets called when init is pressed
    @Override
    public void runOpMode() throws InterruptedException {

        // Init
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        this.initialize();

        telemetry.addData("Status", "Initialized!");
        telemetry.update();

        // Wait for the start button to be pressed
        super.waitForStart(); // Blocks thread

        // Wait and capture the program for the update loop (while running this will block thread)
        while(opModeIsActive()) {

            this.telemetry.addData("State", this.stateManager.getState().name());

            this.robot.update();

            this.telemetry.update();

        }

        // Handle Close Down Sequence
        this.onStop();

    }

}