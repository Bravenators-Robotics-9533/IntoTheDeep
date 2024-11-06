package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.bravenatorsrobotics.components.ArmComponent;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.config.ConfigMap;
import com.bravenatorsrobotics.controllers.ArmController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@Config
@Autonomous(name="Autonomous", group="Competition")
public class Auto extends LinearOpMode {


    private ArmController armController;
    private IntakeController intakeController;

    private MecanumDrive drive;

    private void initialize() {

        telemetry.addData("Status", "Initialize...");
        telemetry.update();

        ConfigMap.load(super.hardwareMap.appContext);

        drive = new MecanumDrive(super.hardwareMap);

        ArmComponent armComponent = new ArmComponent(super.hardwareMap);
        this.armController = new ArmController(armComponent);

        IntakeComponent intakeComponent = new IntakeComponent(super.hardwareMap);
        this.intakeController = new IntakeController(intakeComponent);

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
        this.armController.update();
        this.intakeController.update();

        this.drive.update();
    }

    private void loopUntilDriveDone() {

        while(opModeIsActive() && drive.isBusy()) {
            update();
        }

    }

    private void run() {

        if(!super.opModeIsActive())
            return;

        this.intakeController.tension();
        this.intakeController.CapturePivotYPosition();

        drive.setPoseEstimate(new Pose2d(-45, 65, Math.toRadians(90)));

        Trajectory moveOffWall = drive.trajectoryBuilder(new Pose2d(45, 65, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(49, 44, Math.toRadians(225)))
                // 52 50
                .addDisplacementMarker(1.0,
                        () -> this.armController.setTargetArmPosition(ArmController.ArmPosition.BOTTOM_BASKET))
                .build();

        drive.followTrajectoryAsync(moveOffWall);
        this.loopUntilDriveDone(); // Stuck until trajectory done.

        Trajectory moveToScore = drive.trajectoryBuilder(new Pose2d(49, 44, Math.toRadians(225)))
                .lineTo(new Vector2d(56.0, 46.0))
                .build();

        drive.followTrajectoryAsync(moveToScore);
        this.loopUntilDriveDone();

        // Drop the thing
        this.intakeController.release();

        sleep(500);
        this.intakeController.IntakePivotPosition();

        Trajectory backUp = drive.trajectoryBuilder(new Pose2d(56.0, 46.0, Math.toRadians(225)))
                .lineTo(new Vector2d(50.0, 50.0))
                .build();

        drive.followTrajectoryAsync(backUp);
        this.loopUntilDriveDone();

        this.armController.setTargetArmPosition(ArmController.ArmPosition.REST);

        sleep(1000);

        Trajectory swingAround = drive.trajectoryBuilder(new Pose2d(50.0, 50.0), Math.toRadians(225))
                .lineToLinearHeading(new Pose2d(49 + 15, 42, Math.toRadians(90)))
                .build();

        drive.followTrajectoryAsync(swingAround);
        this.loopUntilDriveDone();

    }

}
