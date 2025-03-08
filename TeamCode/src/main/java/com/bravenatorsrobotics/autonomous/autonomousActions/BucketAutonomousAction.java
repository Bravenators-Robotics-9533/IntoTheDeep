package com.bravenatorsrobotics.autonomous.autonomousActions;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.robot.Robot;

public class BucketAutonomousAction extends AbstractAutonomousAction {

    private Action driveToBucketAction;
    private Action driveToSlideFirstBlockAction;
    private Action driveToBucketSecondAction;

    public BucketAutonomousAction(Robot robot, Pose2d initialPosition) {
        super(robot, initialPosition);
    }

    @Override
    protected void initialize() {

        this.driveToBucketAction = this.robot.drive.actionBuilder(super.initialPosition)
                .splineToLinearHeading(new Pose2d(55, 54, Math.toRadians(405)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToSlideFirstBlockAction = this.robot.drive.actionBuilder(new Pose2d(55.0, 54, Math.toRadians(405)))
                .strafeToLinearHeading(new Vector2d(45.5, 44), Math.toRadians(450), this.robot.drive.slowVelConstraint)
                .build();

        this.driveToBucketSecondAction = this.robot.drive.actionBuilder(new Pose2d(new Vector2d(45, 44), Math.toRadians(450)))
                .splineToLinearHeading(new Pose2d(52.0, 52, Math.toRadians(405)), Math.toRadians(0), this.robot.drive.slowVelConstraint)
                .build();
    }

    @NonNull
    @Override
    public Action primaryAction() {

        return new SequentialAction(

                new ParallelAction(
                        // Lift Basket and Flip Pass Off Servo
                        this.robot.liftController.liftToTopBasketAction(),
                        new InstantAction(this.robot.outtakeController::setPassOffPivotScore),

                        // Drive to Bucket
                        new SequentialAction(
                                new SleepAction(0.5),
                                this.driveToBucketAction
                        )

                ),

                new SleepAction(0.25), // Settle Robot
                new InstantAction(this.robot.outtakeController::setPassOffClawOpen), // Open Claw
                new SleepAction(0.5), // Wait for stop

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
                new SleepAction(2),

                // Bring in Intake
                new ParallelAction(
                        new InstantAction(this.robot.intakeController::stopIntake),
                        new InstantAction(this.robot.intakeController::setFlipPositionToPassOff)
                ),

                // Wait for Flip In
                new SleepAction(1.0),

                // Bring Slide In
                this.robot.slideController.slideOutAction(0.0),

                new ParallelAction(

                        this.driveToBucketSecondAction,

                        new SequentialAction(
                                // Pass off grab
                                new InstantAction(this.robot.outtakeController::setPassOffClawClosed),
                                new SleepAction(0.75), // Wait for grab

                                // Spit out block and lift
                                new ParallelAction(
                                        new InstantAction(this.robot.intakeController::expelSample),
                                        this.robot.liftController.liftToTopBasketAction(),

                                        new SequentialAction(
                                                new SleepAction(0.75),
                                                new InstantAction(this.robot.outtakeController::setPassOffPivotScore)
                                        )
                                ),

                                new SleepAction(0.2), // Wait for robot settle

                                new InstantAction(this.robot.outtakeController::setPassOffClawOpen),
                                new SleepAction(0.5) // Wait for stop
                        )

                )

        );

    }

}
