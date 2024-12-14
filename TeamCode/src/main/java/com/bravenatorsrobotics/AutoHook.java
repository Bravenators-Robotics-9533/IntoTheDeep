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
import com.bravenatorsrobotics.controllers.OuttakeController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.MecanumDrive;

@Config
@Autonomous(name="AutoHook", group="Competition")
public class AutoHook extends LinearOpMode {


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

        this.outtakeController.wallClawClosed();

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

        this.outtakeController.wallClawClosed();
        sleep(100);
        this.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
        sleep(200);


        drive.setPoseEstimate(new Pose2d(8.5, -63.5, Math.toRadians(270)));

        Trajectory moveOffWall = drive.trajectoryBuilder(new Pose2d(8.5, -63.5, Math.toRadians(270)))
                .lineTo(new Vector2d(8.5, -19.0))
                .build();

        drive.followTrajectoryAsync(moveOffWall);
        this.loopUntilDriveDone(); // Stuck until trajectory done.

        sleep(750);
        liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
        sleep(2000);

        Trajectory moveToScore = drive.trajectoryBuilder(new Pose2d(8.5, -19, Math.toRadians(270)))
                .lineTo(new Vector2d(8.5, -63.5))
                .build();

        drive.followTrajectoryAsync(moveToScore);
        this.loopUntilDriveDone();


        Trajectory backUp = drive.trajectoryBuilder(new Pose2d(8.5, -63.5, Math.toRadians(270)))
                .lineTo(new Vector2d(63.5, -63.5))
                .build();

        drive.followTrajectoryAsync(backUp);
        this.loopUntilDriveDone();

        while(opModeIsActive()) {
            this.liftController.update();
        }

    }
}
