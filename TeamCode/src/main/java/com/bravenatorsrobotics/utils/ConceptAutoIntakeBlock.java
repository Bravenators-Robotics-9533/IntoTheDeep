package com.bravenatorsrobotics.utils;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

@Config
@TeleOp(name="ConceptAutoIntakeBlock", group = "Concept")
public class ConceptAutoIntakeBlock extends LinearOpMode {

    private Robot robot;
    private BlockDetectVisionPipeline pipeline;

    private BlockDetectVisionPipeline.DetectionData data = null;

    private boolean isRunning = false;

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

                this.data = this.pipeline.detectBlock();

                if(this.data != null) {
                    this.updatePath();
                }
            }

            if(!super.gamepad1.a) {
                this.data = null;
                this.robot.drive.setMotorPowers(0, 0, 0, 0);
            }

            // Telemetry
            if(this.data != null) {
                this.data.telemetry(super.telemetry);
            }

            super.telemetry.update();

        }

    }

    public static double Y0_P = 0.0003;
    public static double C_P = 0.0005;

    public static double TARGET_Y0 = 425.0;
    public static double TARGET_C = 945.0;

    private void updatePath() {

        double errorY0 = TARGET_Y0 - this.data.y0;
        double errorC = TARGET_C - this.data.c;

        double y = Y0_P * errorY0;
        double x = C_P * errorC;

        // Calculate Drive
        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);

        double flPower  = Range.clip((y - x) / denominator, -1.0, 1.0);
        double blPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
        double frPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
        double brPower =  Range.clip((y - x) / denominator, -1.0, 1.0);

        this.robot.drive.setMotorPowers(flPower, blPower, brPower, frPower);

    }

}
