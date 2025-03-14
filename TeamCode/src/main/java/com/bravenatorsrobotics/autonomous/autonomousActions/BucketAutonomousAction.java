package com.bravenatorsrobotics.autonomous.autonomousActions;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.robot.Robot;

public class BucketAutonomousAction extends AbstractAutonomousAction {

    private Action driveToBucketAction;
    private Action driveToSlideFirstBlockAction;
    private Action driveToBucketSecondAction;
    private Action driveToSlideSecondBlockAction;
    private Action driveToBucketScoreSecondFieldBlockAction;
    private Action driveToSlideThirdBlockAction;
    private Action driveToBucketScoreThirdBlockAction;
    private Action parkAction;

    public BucketAutonomousAction(Robot robot, Pose2d initialPosition) {
        super(robot, initialPosition);
    }

    @Override
    protected void initialize() {

        this.driveToBucketAction = this.robot.drive.actionBuilder(super.initialPosition)
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(415)), Math.toRadians(0))
                .build();

        this.driveToSlideFirstBlockAction = this.robot.drive.actionBuilder(new Pose2d(55, 55, Math.toRadians(405)))
                .strafeToLinearHeading(new Vector2d(46, 44), Math.toRadians(450))
                .build();

        this.driveToBucketSecondAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(46, 44), Math.toRadians(450)))
                .splineToLinearHeading(new Pose2d(53.5, 53.5, Math.toRadians(420)), Math.toRadians(0))
                .build();

        this.driveToSlideSecondBlockAction = this.robot.drive.actionBuilder(new Pose2d(53.5, 53.5, Math.toRadians(420)))
                .splineToLinearHeading(new Pose2d(new Vector2d(56.5, 46), Math.toRadians(445)), Math.toRadians(0))
                .build();

        this.driveToBucketScoreSecondFieldBlockAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(56.5, 46), Math.toRadians(445)))
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(415)), Math.toRadians(0))
                .build();

        this.driveToSlideThirdBlockAction = this.robot.drive.actionBuilder(new Pose2d(55, 55, Math.toRadians(415)))
                .splineToLinearHeading(new Pose2d(new Vector2d(54, 43), Math.toRadians(480)), Math.toRadians(0))
                .build();

        this.driveToBucketScoreThirdBlockAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(54, 43), Math.toRadians(480)))
                .splineToLinearHeading(new Pose2d(53.5, 54.5, Math.toRadians(415)), Math.toRadians(0))
                .build();

        this.parkAction = this.robot.drive.actionBuilder(new Pose2d(53.5, 54.5, Math.toRadians(415)))
                .strafeToLinearHeading(new Vector2d(40, 10), Math.toRadians(180), this.robot.drive.fastVelConstraint)
                .strafeToConstantHeading(new Vector2d(20, 10), this.robot.drive.fastVelConstraint)
                .build();

    }

    @NonNull
    @Override
    public Action primaryAction() {

        this.robot.outtakeController.openWallClawServo();

        return new SequentialAction(

                // =================================================================================
                // Drive and Score Starting Block
                // =================================================================================

                new ParallelAction(
                        // Lift Basket and Flip Pass Off Servo
                        this.robot.liftController.liftToTopBasketAction(),
                        new InstantAction(this.robot.outtakeController::setPassOffPivotScore),

                        // Drive to Bucket
                        new SequentialAction(
                                new SleepAction(0.7),
                                this.driveToBucketAction
                        )

                ),

                new InstantAction(this.robot.outtakeController::setPassOffClawOpen), // Open Claw
                new SleepAction(0.25), // Wait for robot to drop claw

                // =================================================================================
                // Drive and Grab First Block from Field
                // =================================================================================

//                new ParallelAction(
//
//                        // Reset Pass-off Claw
//                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
//                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
//
//                        // Drive robot and slide out
//                        this.robot.slideController.slideOutAction(0.15),
//
//                        // Bring Lift Down
//                        new RaceAction(
//                            this.robot.liftController.liftToRestAction(),
//
//                            new SequentialAction(
//
//                                    this.driveToSlideFirstBlockAction,
//
//                                    new SleepAction(0.25),
//
//                                    // Start Intake
//                                    new ParallelAction(
//                                            new InstantAction(this.robot.intakeController::intakeSample),
//                                            new InstantAction(this.robot.intakeController::setFlipPositionToIntake)
//                                    )
//                            )
//                        )
//
//                ),

                new ParallelAction(

                        this.robot.liftController.liftToRestAction(),

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),

                        // Drive robot and slide out
                        this.robot.slideController.slideOutAction(0.15),

                        // Bring Lift Down
                        this.robot.liftController.liftToRestAction(),

                        this.driveToSlideFirstBlockAction,

                        new SequentialAction(
                                new SleepAction(0.55), // Wait for slide

                                // Start Intake
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::intakeSample),
                                        new InstantAction(this.robot.intakeController::setFlipPositionToIntake)
                                ),

                                new SleepAction(1.45),

                                // Bring in Intake
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::stopIntake),
                                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
                                ),

                                new ParallelAction(

                                        this.driveToBucketSecondAction,

                                        new SequentialAction(

                                                // Wait for Flip In
                                                new SleepAction(1.2),

                                                // Bring Slide In
                                                this.robot.slideController.slideOutAction(0),

                                                new SleepAction(0.2), // Wait for Slide

                                                // Pass off grab
                                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
                                                new SleepAction(0.35), // Wait for grab

                                                // Spit out block and lift
                                                new ParallelAction(
                                                        new InstantAction(this.robot.intakeController::expelSample),
                                                        this.robot.liftController.liftToTopBasketAction(),

                                                        new SequentialAction(
                                                                new SleepAction(0.45),
                                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore),

                                                                new SleepAction(1.1), // Wait for robot settle

                                                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                                                new InstantAction(this.robot.intakeController::stopIntake)
                                                        )
                                                ),

                                                new SleepAction(0.35) // Wait for block to drop

                                        )

                                )
                        )

                ),

                // Wait for Block to Intake
//                new SleepAction(1.5),
//
//                // Bring in Intake
//                new ParallelAction(
//                        new InstantAction(this.robot.intakeController::stopIntake),
//                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
//                ),
//
//                // Wait for Flip In
//                new SleepAction(1.7),
//
//                // Bring Slide In
//                this.robot.slideController.slideOutAction(0),
//
//                // =================================================================================
//                // Drive and Score First Block from Field
//                // =================================================================================
//                new ParallelAction(
//
//                        this.driveToBucketSecondAction,
//
//                        new SequentialAction(
//
//                                new SleepAction(0.45), // Wait for Slide
//
//                                // Pass off grab
//                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
//                                new SleepAction(0.7), // Wait for grab
//
//                                // Spit out block and lift
//                                new ParallelAction(
//                                        new InstantAction(this.robot.intakeController::expelSample),
//                                        this.robot.liftController.liftToTopBasketAction(),
//
//                                        new SequentialAction(
//                                                new SleepAction(0.5),
//                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore)
//                                        )
//                                ),
//
//                                new SleepAction(1.6), // Wait for robot settle
//
//                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
//                                new InstantAction(this.robot.intakeController::stopIntake),
//
//                                new SleepAction(0.2) // Wait for block to drop
//
//                        )
//
//                ),

                // =================================================================================
                // Drive and Grab Second Block from Field
                // =================================================================================

                new ParallelAction(

                        this.robot.liftController.liftToRestAction(),

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),

                        // Drive robot and slide out
                        this.robot.slideController.slideOutAction(0.35),

                        // Bring Lift Down
                        this.robot.liftController.liftToRestAction(),

                        this.driveToSlideSecondBlockAction,

                        new SequentialAction(
                               new SleepAction(0.55), // Wait for slide

                                // Start Intake
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::intakeSample),
                                        new InstantAction(this.robot.intakeController::setFlipPositionToIntake)
                                ),

                                new SleepAction(1.45),

                                // Bring in Intake
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::stopIntake),
                                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
                                ),

                                new ParallelAction(

                                        this.driveToBucketScoreSecondFieldBlockAction,

                                        new SequentialAction(

                                                // Wait for Flip In
                                                new SleepAction(1.2),

                                                // Bring Slide In
                                                this.robot.slideController.slideOutAction(0),

                                                new SleepAction(0.3), // Wait for Slide

                                                // Pass off grab
                                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
                                                new SleepAction(0.35), // Wait for grab

                                                // Spit out block and lift
                                                new ParallelAction(
                                                        new InstantAction(this.robot.intakeController::expelSample),
                                                        this.robot.liftController.liftToTopBasketAction(),

                                                        new SequentialAction(
                                                                new SleepAction(0.45),
                                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore),

                                                                new SleepAction(1.1), // Wait for robot settle

                                                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                                                new InstantAction(this.robot.intakeController::stopIntake)
                                                        )
                                                ),

                                                new SleepAction(0.35) // Wait for block to drop

                                        )

                                )
                        )

                ),

                // =================================================================================
                // Drive and Score Second Block from Field
                // =================================================================================


                // =================================================================================
                // Drive and Grab Third Block from Field
                // =================================================================================

                new ParallelAction(

                        this.robot.liftController.liftToRestAction(),

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),

                        this.driveToSlideThirdBlockAction,

                        this.robot.slideController.slideOutAction(0.4),

                        new SequentialAction(
                                new SleepAction(0.5),
                                new InstantAction(this.robot.intakeController::setFlipPositionToIntake),
                                new InstantAction(this.robot.intakeController::intakeSample)
                        )
                ),

                new SleepAction(0.6),

                new ParallelAction(
                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff),
                        new InstantAction(this.robot.intakeController::stopIntake)
                ),

                new ParallelAction(

                        this.driveToBucketScoreThirdBlockAction,

                        new SequentialAction(

                                // Wait for Flip In
                                new SleepAction(1.25),

                                // Bring Slide In
                                this.robot.slideController.slideOutAction(0),

                                new SleepAction(0.45), // Wait for Slide

                                // Pass off grab
                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
                                new SleepAction(0.4), // Wait for grab

                                // Spit out block and lift
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::expelSample),
                                        this.robot.liftController.liftToTopBasketAction(),

                                        new SequentialAction(
                                                new SleepAction(0.5),
                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore),

                                                new SleepAction(1.5), // Wait for robot settle

                                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                                new InstantAction(this.robot.intakeController::stopIntake)
                                        )
                                ),

                                new SleepAction(0.2) // Wait for block to drop

                        )

                ),

                new ParallelAction(

                        this.robot.liftController.liftToRestAction(),

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotFlippedUp),
                        new InstantAction(this.robot.outtakeController::setPassOffClawClosed),

                        this.parkAction
                )

        );

    }

}
