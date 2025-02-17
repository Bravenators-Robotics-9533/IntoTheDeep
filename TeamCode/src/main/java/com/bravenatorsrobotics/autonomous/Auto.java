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
    private Action pushBlocksAction;
    private Action driveToFirstScoreAction;
    private Action driveToScoreBlockAgainAction;
    private Action driveToSecondScoreAction;

    private void initialize() {

        this.robot = new Robot(ControlSystemController.Strategy.AUTO, INITIAL_STARTING_POSITION, this);
        this.robot.initialize();

    }

    private void createActions() {

        this.driveToHighBarAction = this.robot.drive.actionBuilder(INITIAL_STARTING_POSITION)
                .strafeTo(new Vector2d(-3, 32.5))
        .build();

        this.pushBlocksAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(-3, 32.5), Math.toRadians(270)))
                .strafeToConstantHeading(new Vector2d(-5, 35))
                .splineToConstantHeading(new Vector2d(-39, 20), new Rotation2d(Math.toRadians(0), Math.toRadians(-180)))
                .strafeTo(new Vector2d(-39, 15))
                .strafeTo(new Vector2d(-45, 15))
                .strafeTo(new Vector2d(-45, 52))
                .strafeToLinearHeading(new Vector2d(-45, 15), Math.toRadians(90))
                .strafeTo(new Vector2d(-53, 15))
                .strafeTo(new Vector2d(-53, 52))
                .strafeTo(new Vector2d(-48, 52))
                .strafeTo(new Vector2d(-48, 63))
        .build();

        this.driveToFirstScoreAction = this.robot.drive.actionBuilder(new Pose2d(-48, 63, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-5, 50), Math.toRadians(270))
                .strafeToConstantHeading(new Vector2d(-5, 32))
        .build();

        this.driveToScoreBlockAgainAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(-5, 32), Math.toRadians(270)))
                .strafeTo(new Vector2d(-5, 38))
                .strafeToLinearHeading(new Vector2d(-43, 59), Math.toRadians(90))
                .strafeTo(new Vector2d(-43, 64))
        .build();

        this.driveToSecondScoreAction = this.robot.drive.actionBuilder(new Pose2d(-43, 64, Math.toRadians(90)))
                .strafeTo(new Vector2d(-43, 59))
                .splineToLinearHeading(new Pose2d(new Vector2d(-5, 32.5), Math.toRadians(270)), new Rotation2d(Math.toRadians(0), Math.toRadians(-90)))
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
                                this.pushBlocksAction
                        ),

                        this.robot.outtakeController.closeWallClawAction(),

                        new ParallelAction(
                                this.driveToFirstScoreAction,
                                this.robot.liftController.liftToHighBarAction()
                        ),

                        this.robot.liftController.liftToHighBarReleasePosition(),

                        new ParallelAction(
                                this.robot.outtakeController.openWallClawAction(),
                                this.robot.liftController.liftToRestAction(),
                                this.driveToScoreBlockAgainAction
                        ),

                        this.robot.outtakeController.closeWallClawAction(),

                        new ParallelAction(
                                this.driveToSecondScoreAction,
                                this.robot.liftController.liftToHighBarAction()
                        ),

                        this.robot.liftController.liftToHighBarReleasePosition(),
                        this.robot.outtakeController.openWallClawAction(),
                        this.robot.liftController.liftToRestAction()

                )
        );

        // Do autonomous code here

    }

}
