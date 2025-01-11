package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.components.LiftComponent;
import com.bravenatorsrobotics.components.OuttakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.OuttakeController;
import com.bravenatorsrobotics.controllers.LiftController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@Config
@Autonomous(name="Auto2BlockPush", group="Competition")
public class Auto2BlockPush extends LinearOpMode {


    private LiftController liftController;
    private IntakeController intakeController;
    private OuttakeController outtakeController;

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

        OuttakeComponent outtakeComponent = new OuttakeComponent(super.hardwareMap);
        this.outtakeController = new OuttakeController(outtakeComponent);

        StaticEnvironment.ShouldZeroLift = false;

        this.outtakeController.setWallClawClosed();

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
        this.outtakeController.update();

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
        this.outtakeController.setPassOffPivotInitial();
        this.outtakeController.setPassOffClawOpen();
        this.outtakeController.setWallClawClosed();
        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
        sleep(500);


        drive.setPoseEstimate(new Pose2d(11, -63.5, Math.toRadians(270)));

        Trajectory moveOffWall = drive.trajectoryBuilder(new Pose2d(11, -63.5, Math.toRadians(270)))
                .lineTo(new Vector2d(11, -22.0))
                .build();

        drive.followTrajectoryAsync(moveOffWall);
        this.loopUntilDriveDone(); // Stuck until trajectory done.

        sleep(500);
        liftController.setTargetLiftPosition(LiftController.LiftPosition.LOW_BAR);
        sleep(1000);
        outtakeController.setWallClawOpen();
        sleep(200);
        liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);

        Trajectory moveToWall = drive.trajectoryBuilder(new Pose2d(11, -22, Math.toRadians(270)))
                .lineTo(new Vector2d(11, -32))
                .build();

        drive.followTrajectoryAsync(moveToWall);
        this.loopUntilDriveDone();


        Trajectory lineUpToPush = drive.trajectoryBuilder(new Pose2d(11, -42, Math.toRadians(270)))
                .lineTo(new Vector2d(45, -42))
                .build();

        drive.followTrajectoryAsync(lineUpToPush);
        this.loopUntilDriveDone();

        Trajectory driveUpTopPushBlock1 = drive.trajectoryBuilder(new Pose2d(45, -42, Math.toRadians(270)))
                .lineTo(new Vector2d(45, -12))
                .build();

        drive.followTrajectoryAsync(driveUpTopPushBlock1);
        this.loopUntilDriveDone();

        Trajectory moveToPushBlock1 = drive.trajectoryBuilder(new Pose2d(45, -12, Math.toRadians(270)))
                .lineTo(new Vector2d(55, -12))
                .build();

        drive.followTrajectoryAsync(moveToPushBlock1);
        this.loopUntilDriveDone();

        Trajectory pushBlock1 = drive.trajectoryBuilder(new Pose2d(55, -12, Math.toRadians(270)))
                .lineTo(new Vector2d(55, -61))
                .build();

        drive.followTrajectoryAsync(pushBlock1);
        this.loopUntilDriveDone();

        Trajectory driveUpToPushBlock2 = drive.trajectoryBuilder(new Pose2d(55, -61, Math.toRadians(270)))
                .lineTo(new Vector2d(55, -14))
                .build();

        drive.followTrajectoryAsync(driveUpToPushBlock2);
        this.loopUntilDriveDone();

        Trajectory moveToPushBlock2 = drive.trajectoryBuilder(new Pose2d(55, -14, Math.toRadians(270)))
                .lineTo(new Vector2d(70, -14))
                .build();

        drive.followTrajectoryAsync(moveToPushBlock2);
        this.loopUntilDriveDone();

        Trajectory pushBlock2 = drive.trajectoryBuilder(new Pose2d(70, -14, Math.toRadians(270)))
                .lineTo(new Vector2d(70, -61))
                .build();

        drive.followTrajectoryAsync(pushBlock2);
        this.loopUntilDriveDone();

        Trajectory driveUpForHuman = drive.trajectoryBuilder(new Pose2d(70, -61, Math.toRadians(270)))
                .lineTo(new Vector2d(70, -55))
                .build();

        drive.followTrajectoryAsync(driveUpForHuman);
        this.loopUntilDriveDone();

        Trajectory park = drive.trajectoryBuilder(new Pose2d(70, -55, Math.toRadians(270)))
                .lineTo(new Vector2d(70, -61))
                .build();

        drive.followTrajectoryAsync(park);
        this.loopUntilDriveDone();




        while(opModeIsActive()) {
            this.liftController.update();
        }

    }
}
