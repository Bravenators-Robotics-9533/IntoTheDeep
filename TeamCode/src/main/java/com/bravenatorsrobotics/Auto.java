package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.components.LiftComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.LiftController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import roadrunner.drive.MecanumDrive;

@Config
@Autonomous(name="Autonomous", group="Competition")
public class Auto extends LinearOpMode {


    private LiftController liftController;
    private IntakeController intakeController;

    private MecanumDrive drive;

    private void initialize() {

        telemetry.addData("Status", "Initialize...");
        telemetry.update();

        ConfigMap.load(super.hardwareMap.appContext);

        drive = new MecanumDrive(super.hardwareMap);

        LiftComponent armComponent = new LiftComponent(super.hardwareMap);
        this.liftController = new LiftController(armComponent, telemetry);

        IntakeComponent intakeComponent = new IntakeComponent(super.hardwareMap);
        this.intakeController = new IntakeController(intakeComponent);

        StaticEnvironment.ShouldZeroLift = false;

        telemetry.addData("Status", "Initialized!");
        telemetry.update();
    }

    // Make efficient
    private void onWaitForStartup() {

    }

    private void onStop() {

    }

    @Override
    public void runOpMode() throws InterruptedException {

        this.initialize();

        // Wait for startup
        waitForStart();

        this.run();

        this.onStop();

    }

    private void sleep(int milliseconds) {
        ElapsedTime timer = new ElapsedTime();

        while(opModeIsActive() && timer.milliseconds() < milliseconds) {
            this.update();
        }

    }

    private void update() {
        this.liftController.update();
        this.intakeController.update();

        this.drive.update();
    }

    private void loopUntilDriveDone() {

        while(opModeIsActive() && drive.isBusy()) {
            update();
        }

    }

    private void run() {

        if (!super.opModeIsActive())
            return;

        //this.intakeController.release();
        this.intakeController.goToCapturePivotYPosition();
        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);

        sleep(1500);

        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET);


        sleep(2000);

        drive.setPoseEstimate(new Pose2d(-45, 65, Math.toRadians(90)));

        Trajectory moveOffWall = drive.trajectoryBuilder(new Pose2d(45, 65, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(49.5, 47.5, Math.toRadians(220)))
                .build();

        drive.followTrajectoryAsync(moveOffWall);
        this.loopUntilDriveDone(); // Stuck until trajectory done.

        Trajectory moveToScore = drive.trajectoryBuilder(new Pose2d(49.5, 47.5, Math.toRadians(220)))
                .lineTo(new Vector2d(68, 58))
                .build();

        drive.followTrajectoryAsync(moveToScore);
        this.loopUntilDriveDone();

        // Drop the thing
        this.intakeController.goToCapturePivotYPosition();
        this.intakeController.tension();


        sleep(1500);

        Trajectory backUp = drive.trajectoryBuilder(new Pose2d(68, 58, Math.toRadians(225)))
                .lineTo(new Vector2d(45.0, 48.0))
                .build();

        drive.followTrajectoryAsync(backUp);
        this.loopUntilDriveDone();


        sleep(1000);
        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);

        Trajectory swingAround = drive.trajectoryBuilder(new Pose2d(45.0, 48.0), Math.toRadians(225))
                .lineToLinearHeading(new Pose2d(49 + 15, 42, Math.toRadians(90)))
                .build();

        drive.followTrajectoryAsync(swingAround);
        this.loopUntilDriveDone();

        sleep(2000);

        this.intakeController.tensionServosOff();

        Trajectory park = drive.trajectoryBuilder(new Pose2d(49 + 15, 40, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(49 + 15, 1, Math.toRadians(0)))
                .build();

        drive.followTrajectoryAsync(park);
        this.loopUntilDriveDone();

        Trajectory parkParkLOLNickWroteThis = drive.trajectoryBuilder(new Pose2d(49 + 15, 1))
                .lineTo(new Vector2d(49 + 15 - 25, 1))
                .build();

        drive.followTrajectoryAsync(parkParkLOLNickWroteThis);
        this.loopUntilDriveDone();

        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);

        while(opModeIsActive()) {
            this.liftController.update();
        }

    }
}
