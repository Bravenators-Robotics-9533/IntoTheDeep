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

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@Config
@Autonomous(name="AutoHook", group="Competition")
public class AutoHook extends LinearOpMode {


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

        sleep(8000);

        //this.intakeController.release();
        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
        sleep(750);
        this.intakeController.intakePivotYPosition();


        sleep(3000);


        drive.setPoseEstimate(new Pose2d(-22.5, 65, Math.toRadians(90)));

        Trajectory moveOffWall = drive.trajectoryBuilder(new Pose2d(22.5, 65, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(22.5, 54, Math.toRadians(90)))
                .build();

        drive.followTrajectoryAsync(moveOffWall);
        this.loopUntilDriveDone(); // Stuck until trajectory done.

        sleep(750);
        this.intakeController.capturePivotYPosition();
        sleep(500);

        Trajectory moveToScore = drive.trajectoryBuilder(new Pose2d(22.5, 54, Math.toRadians(90)))
                .lineTo(new Vector2d(22.5, 63))
                .build();

        drive.followTrajectoryAsync(moveToScore);
        this.loopUntilDriveDone();
        this.intakeController.tension();

        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);


        sleep(1500);

        Trajectory backUp = drive.trajectoryBuilder(new Pose2d(22.5, 53, Math.toRadians(90)))
                .lineTo(new Vector2d(-55.0, 55.0))
                .build();

        drive.followTrajectoryAsync(backUp);
        this.loopUntilDriveDone();
        this.intakeController.tensionServosOff();


        sleep(10000);


        while(opModeIsActive()) {
            this.liftController.update();
        }

    }
}
