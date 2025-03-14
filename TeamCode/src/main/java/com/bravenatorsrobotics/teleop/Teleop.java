package com.bravenatorsrobotics.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.hardware.controllers.SlideController;
import com.bravenatorsrobotics.robot.Robot;
import com.bravenatorsrobotics.teleop.controlAdapters.AutoIntakeControlAdapter;
import com.bravenatorsrobotics.teleop.controlAdapters.StatusLEDControlAdapter;
import com.bravenatorsrobotics.teleop.controlAdapters.TeleopManualControlAdapter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;

@Config
@TeleOp(name = "Teleop", group = "Competition")
public class Teleop extends LinearOpMode {

    private final TeleopStateManager stateManager = new TeleopStateManager();
    private final ActionQueue actionQueue = new ActionQueue();

    private Robot robot;

    private StatusLEDControlAdapter statusLEDControlAdapter;
    private TeleopManualControlAdapter manualControlAdapter;
//    private AutoIntakeControlAdapter autoIntakeControlAdapter;

    private void initialize() {

        // Load the current config
        ConfigMap.load(super.hardwareMap.appContext); // Must happen before you use static ConfigMap

        this.robot = new Robot(this, this::onUpdate);
        this.robot.initialize();

        this.statusLEDControlAdapter = new StatusLEDControlAdapter(this.gamepad1, this.gamepad2);
        this.statusLEDControlAdapter.initialize();

        // Create Manual Control Adapter
        this.manualControlAdapter = new TeleopManualControlAdapter(this, this.robot, this.statusLEDControlAdapter, this.actionQueue);
        this.manualControlAdapter.initialize();

//        this.autoIntakeControlAdapter = new AutoIntakeControlAdapter(this, this.robot);
//        this.autoIntakeControlAdapter.initialize();

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

    private void updateQueuedActions() {

        TelemetryPacket packet = new TelemetryPacket();

        this.actionQueue.printActions(super.telemetry);
        this.actionQueue.update(packet);

    }

    private void onUpdate() {

//        if(gamepad1.a && this.stateManager.getState() != TeleopState.AUTO_INTAKE_BLOCK) {
//            this.stateManager.setState(TeleopState.AUTO_INTAKE_BLOCK);
//        }

//        if(!gamepad1.a && this.stateManager.getState() != TeleopState.MANUAL) {
//            this.stateManager.setState(TeleopState.MANUAL);
//            this.autoIntakeControlAdapter.destroy();
//        }


        // Update Controller Adapters
//        if(this.stateManager.getState() == TeleopState.MANUAL)
            this.manualControlAdapter.update();
//        else if(this.stateManager.getState() == TeleopState.AUTO_INTAKE_BLOCK) {
//            this.autoIntakeControlAdapter.update();
//        }

        this.updateQueuedActions();

        this.statusLEDControlAdapter.update();

    }

    private void onStop() {

    }
}