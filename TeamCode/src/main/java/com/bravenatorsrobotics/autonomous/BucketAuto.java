package com.bravenatorsrobotics.autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.bravenatorsrobotics.autonomous.autonomousActions.AbstractAutonomousAction;
import com.bravenatorsrobotics.autonomous.autonomousActions.BucketAutonomousAction;
import com.bravenatorsrobotics.autonomous.autonomousActions.ScoreThreeSpecimenAutonomousAction;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Bucket Auto", group="Competition")
public class BucketAuto extends LinearOpMode {

    // Pre-defined starting location of the robot.
    private static final Pose2d INITIAL_STARTING_POSITION = new Pose2d(new Vector2d(14.75 + 24, 62.5), Math.toRadians(360));

    private Robot robot;

    /**
     * Initializes the robot and the other objects needed for this opMode
     */
    private void initialize() {

        // Create and init robot class
        this.robot = new Robot(ControlSystemController.Strategy.AUTO, INITIAL_STARTING_POSITION, this);
        this.robot.initialize();

        this.robot.outtakeController.setPassOffClawClosed();

    }

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        this.initialize();

        // Create Autonomous Actions
        AbstractAutonomousAction autonomousAction = new BucketAutonomousAction(this.robot, INITIAL_STARTING_POSITION);
        Action primaryAction = autonomousAction.primaryAction();

        telemetry.addData("Status", "Standby");
        telemetry.update();

        super.waitForStart(); // Wait for start button to be pressed (blocks thread)

        // Run primary action blocking
        Actions.runBlocking(primaryAction);

    }

}
