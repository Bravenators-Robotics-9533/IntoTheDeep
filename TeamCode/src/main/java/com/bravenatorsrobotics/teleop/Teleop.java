package com.bravenatorsrobotics.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.robot.Robot;
import com.bravenatorsrobotics.teleop.controlAdapters.StatusLEDControlAdapter;
import com.bravenatorsrobotics.teleop.controlAdapters.TeleopManualControlAdapter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class Teleop extends LinearOpMode {

    private final TeleopStateManager stateManager;

    private Robot robot;

    private StatusLEDControlAdapter statusLEDControlAdapter;
    private TeleopManualControlAdapter manualControlAdapter;

    public Teleop() {

        this.stateManager = new TeleopStateManager();

    }

    private void initialize() {

        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

        this.robot = new Robot(this, this::onUpdate);
        this.robot.initialize();

        this.statusLEDControlAdapter = new StatusLEDControlAdapter(this.gamepad1, this.gamepad2);
        this.statusLEDControlAdapter.initialize();

        // Create Manual Control Adapter
        this.manualControlAdapter = new TeleopManualControlAdapter(this, this.robot, this.statusLEDControlAdapter);
        this.manualControlAdapter.initialize();

    }

    private void onUpdate() {

        // Update Controller Adapters
        if(this.stateManager.getState() == TeleopState.MANUAL)
            this.manualControlAdapter.update();

        this.statusLEDControlAdapter.update();

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