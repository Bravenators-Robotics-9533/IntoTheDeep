package com.bravenatorsrobotics.autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Autonomous", group="Competition")
public class Auto extends LinearOpMode {

    private static final Pose2d INITIAL_STARTING_POSITION = new Pose2d(new Vector2d(-14.75, 62.5), Math.toRadians(270));

    private Robot robot;

    private Action driveToHighBarAction;
    private Action pushFirstBlockAction;

    private void initialize() {

        this.robot = new Robot(ControlSystemController.Strategy.AUTO, INITIAL_STARTING_POSITION, this);
        this.robot.initialize();

    }

    private void createActions() {

        this.driveToHighBarAction = this.robot.drive.actionBuilder(INITIAL_STARTING_POSITION)
                .splineTo(new Vector2d(-5, 32.5), Math.toRadians(270), this.robot.drive.defaultVelConstraint, this.robot.drive.defaultAccelConstraint)
        .build();

        this.pushFirstBlockAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(-5, 33), Math.toRadians(270)))
                .strafeToConstantHeading(new Vector2d(-5, 35))
                .splineToLinearHeading(new Pose2d(new Vector2d(-37.5, 10), Math.toRadians(90)), new Rotation2d(Math.toRadians(0), Math.toRadians(-180)))
        .build();

    }

    @Override
    public void runOpMode() throws InterruptedException {

        this.initialize();
        this.createActions();

        super.waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                this.driveToHighBarAction,
                                this.robot.liftController.liftToHighBarAction()
                        ),

                        // Go to low bar
                        this.robot.liftController.liftToHighBarReleasePosition(),
                        this.robot.outtakeController.openWallClawAction(),

                        // Open claw and lower lift
                        new ParallelAction(
                                this.robot.liftController.liftToRestAction(),
                                this.pushFirstBlockAction
                        )
                )
        );

        // Do autonomous code here


    }
    
}
