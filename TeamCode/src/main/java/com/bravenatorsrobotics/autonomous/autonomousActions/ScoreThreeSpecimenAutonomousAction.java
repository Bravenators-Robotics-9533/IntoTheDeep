package com.bravenatorsrobotics.autonomous.autonomousActions;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.robot.Robot;

public class ScoreThreeSpecimenAutonomousAction extends AbstractAutonomousAction {

    private Action driveToHighBarAction; // Initial movement, drives to the high bar
    private Action pushBlocksAction; // Pushes the two blocks and sets up to grab spec. off wall
    private Action driveToFirstScoreAction; // Drives to the pole to score
    private Action driveToScoreBlockAgainAction; // Drives back to human player to grab another block
    private Action driveToSecondScoreAction; // Drives back to the pole to score
    private Action driveToParkAction;

    public ScoreThreeSpecimenAutonomousAction(Robot robot, Pose2d initialPosition) {
        super(robot, initialPosition);
    }

    @Override
    protected void initialize() {

        this.driveToHighBarAction = this.robot.drive.actionBuilder(super.initialPosition)
                .strafeTo(new Vector2d(-3, 29.5))
                .build();

        this.pushBlocksAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(-3, 30), Math.toRadians(270)))
                .strafeToConstantHeading(new Vector2d(-5, 35))
                .splineToConstantHeading(new Vector2d(-36, 20), new Rotation2d(Math.toRadians(0), Math.toRadians(-180)))
                .strafeTo(new Vector2d(-36, 15.5))
                .strafeTo(new Vector2d(-45, 14))
                .strafeTo(new Vector2d(-45, 52))
                .splineToLinearHeading(new Pose2d(new Vector2d(-55, 14), Math.toRadians(90)), Math.toRadians(180))
                .strafeTo(new Vector2d(-55, 52))
                .splineToConstantHeading(new Vector2d(-48, 63), Math.toRadians(110))
                .build();

        this.driveToFirstScoreAction = this.robot.drive.actionBuilder(new Pose2d(-48, 63, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-7, 50), Math.toRadians(270))
                .strafeToConstantHeading(new Vector2d(-7, 32))
                .build();

        this.driveToScoreBlockAgainAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(-7, 32), Math.toRadians(270)))
                .strafeTo(new Vector2d(-8, 38))
                .strafeToLinearHeading(new Vector2d(-43, 59), Math.toRadians(90))
                .strafeTo(new Vector2d(-43, 65))
                .build();

        this.driveToSecondScoreAction = this.robot.drive.actionBuilder(new Pose2d(-43, 65, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-9, 40), Math.toRadians(270))
                .strafeToConstantHeading(new Vector2d(-9, 32))
                .build();

        this.driveToParkAction = this.robot.drive.actionBuilder(new Pose2d(-10, 32, Math.toRadians(270)))
                .strafeTo(new Vector2d(-10, 45), this.robot.drive.fastVelConstraint)
                .strafeTo(new Vector2d(-52, 66), this.robot.drive.fastVelConstraint)
                .build();

    }

    @NonNull
    @Override
    public Action primaryAction() {

        return new SequentialAction(
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

                new ParallelAction(
                        this.robot.liftController.liftToRestAction(),
                        this.driveToParkAction
                )

        );

    }

}