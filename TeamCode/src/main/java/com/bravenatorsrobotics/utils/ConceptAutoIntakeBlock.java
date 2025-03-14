package com.bravenatorsrobotics.utils;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Config
@TeleOp(name="ConceptAutoIntakeBlock", group = "Concept")
public class ConceptAutoIntakeBlock extends LinearOpMode {

    private Robot robot;
    private BlockDetectVisionPipeline pipeline;

    private BlockDetectVisionPipeline.DetectionData data = null;

    private boolean isRunning = false;
    private boolean isDone = false;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        // Init Robot
        this.robot = new Robot(ControlSystemController.Strategy.AUTO, new Pose2d(new Vector2d(0, 0), Math.toDegrees(0)), this);
        this.robot.initialize();

        this.robot.intakeController.setFlipPositionToStandby();

        // Init Block Detection Pipeline
        this.pipeline = new BlockDetectVisionPipeline();
        this.pipeline.initialize(super.hardwareMap);

        super.waitForStart();

        while(super.opModeIsActive()) {

            if(super.gamepad1.a) { // A is being pressed

                if(!this.isRunning) {
                    timer.reset();
                    this.isRunning = true;
                }

                this.data = this.pipeline.detectBlock();

                if(this.data != null) {
                    if(!this.isDone) {
                        this.updatePath();
                    } else {
                        this.robot.intakeController.intakeSample();
                        this.robot.intakeController.setFlipPositionToIntake();
                    }
                }
            }

            if(!super.gamepad1.a) {
                this.data = null;
                this.isRunning = false;
                this.isDone = false;
                this.robot.drive.setMotorPowers(0, 0, 0, 0);
            }

            // Telemetry
            if(this.data != null) {
                this.data.telemetry(super.telemetry);
            }

            super.telemetry.update();

        }

    }

    public static double Y0_P = 0.0005;
    public static double C_P = 0.0006;

    public static double Y0_D = 0.0005;
    public static double C_D = 0.0001;

    public static double TARGET_Y0 = 500.0;
    public static double TARGET_C = 945.0;

    private static final double DX_FILTER = 0.1;

    private double errorY0 = 0;
    private double errorC = 0;

    private double dxY0 = 0;
    private double dxC = 0;

    public static double TOL = 0.05;

    private void updatePath() {

        double errorY0 = TARGET_Y0 - this.data.y0;
        double errorC = TARGET_C - this.data.c;

        double dxY0 = DX_FILTER * (errorY0 - this.errorY0) + (1 - DX_FILTER) * this.dxY0;
        double dxC = DX_FILTER * (errorC - this.errorC) + (1 - DX_FILTER) * this.dxC;

        this.errorY0 = errorY0;
        this.errorC = errorC;

        this.dxY0 = dxY0;
        this.dxC = dxC;

        double y = (Y0_P * errorY0) + (dxY0 * Y0_D);
        double x = C_P * errorC + (dxC * C_D);

        // Calculate Drive
        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);

        double flPower  = Range.clip((y - x) / denominator, -1.0, 1.0);
        double blPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
        double frPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
        double brPower =  Range.clip((y - x) / denominator, -1.0, 1.0);

        this.robot.drive.setMotorPowerByVoltage(flPower, blPower, brPower, frPower);

        // Calculate Intake Movement
        this.robot.intakeController.setPivotOffsetPosition(this.data.angle == 90 ? 1 : 0.5);

        if(!(y < TOL && x < TOL))
            timer.reset();

        if(y < TOL && x < TOL && timer.seconds() > 2.0)
            isDone = true;

    }

}
