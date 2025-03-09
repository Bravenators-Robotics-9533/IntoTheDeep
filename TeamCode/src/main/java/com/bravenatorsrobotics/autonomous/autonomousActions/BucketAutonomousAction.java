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

    public BucketAutonomousAction(Robot robot, Pose2d initialPosition) {
        super(robot, initialPosition);
    }

    @Override
    protected void initialize() {

        this.driveToBucketAction = this.robot.drive.actionBuilder(super.initialPosition)
                .splineToLinearHeading(new Pose2d(54.5, 54.5, Math.toRadians(415)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToSlideFirstBlockAction = this.robot.drive.actionBuilder(new Pose2d(54.5, 54.5, Math.toRadians(405)))
                .strafeToLinearHeading(new Vector2d(46, 44), Math.toRadians(450), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToBucketSecondAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(46, 44), Math.toRadians(450)))
                .splineToLinearHeading(new Pose2d(53.5, 53.5, Math.toRadians(415)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToSlideSecondBlockAction = this.robot.drive.actionBuilder(new Pose2d(53.5, 53.5, Math.toRadians(415)))
                .splineToLinearHeading(new Pose2d(new Vector2d(56.5, 45), Math.toRadians(450)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToBucketScoreSecondFieldBlockAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(56.5, 45), Math.toRadians(450)))
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(415)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
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

                new SleepAction(0.05), // Settle Robot
                new InstantAction(this.robot.outtakeController::setPassOffClawOpen), // Open Claw
                new SleepAction(0.1), // Wait for robot to drop claw

                // =================================================================================
                // Drive and Grab First Block from Field
                // =================================================================================

                new ParallelAction(

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),

                        // Drive robot and slide out
                        this.driveToSlideFirstBlockAction,
                        this.robot.slideController.slideOutAction(0.25),

                        // Bring Lift Down
                        this.robot.liftController.liftToRestAction()

                ),

                // Start Intake
                new ParallelAction(
                        new InstantAction(this.robot.intakeController::intakeSample),
                        new InstantAction(this.robot.intakeController::setFlipPositionToIntake)
                ),

                // Wait for Block to Intake
                new SleepAction(1.75),

                // Bring in Intake
                new ParallelAction(
                        new InstantAction(this.robot.intakeController::stopIntake),
                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
                ),

                // Wait for Flip In
                new SleepAction(0.9),

                // Bring Slide In
                this.robot.slideController.slideOutAction(0),

                // =================================================================================
                // Drive and Score First Block from Field
                // =================================================================================
                new ParallelAction(

                        this.driveToBucketSecondAction,

                        new SequentialAction(

                                new SleepAction(0.5), // Wait for Slide

                                // Pass off grab
                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
                                new SleepAction(0.5), // Wait for grab

                                // Spit out block and lift
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::expelSample),
                                        this.robot.liftController.liftToTopBasketAction(),

                                        new SequentialAction(
                                                new SleepAction(0.5),
                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore)
                                        )
                                ),

                                new SleepAction(0.5), // Wait for robot settle

                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                new InstantAction(this.robot.intakeController::stopIntake),

                                new SleepAction(0.2) // Wait for block to drop

                        )

                ),

                // =================================================================================
                // Drive and Grab Second Block from Field
                // =================================================================================

                new ParallelAction(

                        this.robot.liftController.liftToRestAction(),

                        // Reset Pass-off Claw
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen),

                        // Drive robot and slide out
                        this.robot.slideController.slideOutAction(0.8),

                        // Bring Lift Down
                        this.robot.liftController.liftToRestAction(),

                        this.driveToSlideSecondBlockAction,

                        new SequentialAction(
                               new SleepAction(0.5), // Wait for slide

                                // Start Intake
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::intakeSample),
                                        new InstantAction(this.robot.intakeController::setFlipPositionToIntake)
                                )
                        )

                ),

                // Wait for Block to Intake
                new SleepAction(1.0),

                // Bring in Intake
                new ParallelAction(
                        new InstantAction(this.robot.intakeController::stopIntake),
                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
                ),

                // =================================================================================
                // Drive and Score Second Block from Field
                // =================================================================================

                new ParallelAction(

                        this.driveToBucketScoreSecondFieldBlockAction,

                        new SequentialAction(

                                // Wait for Flip In
                                new SleepAction(0.8),

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

                                                new SleepAction(1.0), // Wait for robot settle

                                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                                new InstantAction(this.robot.intakeController::stopIntake)
                                        )
                                ),

                                new SleepAction(0.2), // Wait for block to drop
                                this.robot.liftController.liftToRestAction()

                        )

                ),

                new ParallelAction(

                        // Reset Pass-off Position
                        new InstantAction(this.robot.outtakeController::setPassOffPivotInitial),
                        new InstantAction(this.robot.outtakeController::setPassOffClawOpen)

                )
        );

    }

}
