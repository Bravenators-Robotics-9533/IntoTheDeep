package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name="Max Velocity Test", group = "utils")
//@Disabled
public class MaxVelocityTestOpMode extends LinearOpMode {

    private static final double TEST_DURATION_SECONDS = 6.0;
    private static final double MAX_RECOMMENDED_VELOCITY_CONF_INT = 0.75; // 75%

    private FtcGamePad gamepad;
    private boolean shouldStartTest = false;

    private DcMotorEx motor = null;
    private VoltageSensor batteryVoltageSensor = null;

    private void initialize() {

        this.gamepad = new FtcGamePad("Gamepad1", super.gamepad1, this::handleGamepad);
        this.batteryVoltageSensor = super.hardwareMap.voltageSensor.iterator().next();

    }

    @Override
    public void runOpMode() throws InterruptedException {

        this.initialize();

        Telemetry telemetry = new MultipleTelemetry(super.telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addLine("Hey Marina, Before you start. You're welcome.");
        telemetry.addLine("");
        telemetry.addLine("No need to say thanks. But to be so FR");
        telemetry.addLine("Be sure you configured CORRECTLY and plugged in a motor to vtest");
        telemetry.addLine("It's important you CONFIGURE it with the correct name and type");
        telemetry.addLine("");
        telemetry.addLine("Press Start to Instantiate Motor...");
        telemetry.update();

        waitForStart();

        this.motor = super.hardwareMap.get(DcMotorEx.class, "vtest");
        this.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.clearAll();
        telemetry.update();

        telemetry.addLine("Congratulations. You didn't mess up catastrophically.");
        telemetry.addLine("I'm so proud of you... JK HAHAHA... You thought I was FR");
        telemetry.addLine("Anyway, the test will last for 6 seconds. Try to not to push stop.");
        telemetry.addLine("After the 15 seconds, it will spit out the results. WRITE THEM DOWN pls");
        telemetry.addLine("");
        telemetry.addLine("Press X to start...");
        telemetry.update();

        double maxVelocity = waitToStartTest(telemetry);
        double maxRecommendedVelocity = maxVelocity * MAX_RECOMMENDED_VELOCITY_CONF_INT;

        if(isStopRequested())
            return;

        telemetry.clearAll();
        telemetry.update();

        telemetry.addLine("ALL DONE! Results Below. Please Record ALL RESULTS");
        telemetry.addLine("");
        telemetry.addData("CONFIDENCE INTERVAL", MAX_RECOMMENDED_VELOCITY_CONF_INT);
        telemetry.addData("BATTERY VOLTAGE", this.batteryVoltageSensor.getVoltage());
        telemetry.addLine("");
        telemetry.addData("Max Velocity", maxVelocity);
        telemetry.addData("Max Recommended Velocity", maxRecommendedVelocity);
        telemetry.update();

        while(!super.isStopRequested() && super.opModeIsActive()) {
            super.idle();
        }

    }

    private double waitToStartTest(Telemetry telemetry) {

        while(super.opModeIsActive()) {
            if(this.shouldStartTest)
                break;

            this.gamepad.update();
        }

        if(!super.opModeIsActive())
            return 0;

        telemetry.clearAll();
        telemetry.addLine("Testing...");
        telemetry.update();

        return testMotor();

    }

    private double testMotor() {
        ElapsedTime timer = new ElapsedTime();
        double maxVelocity = 0.0;

        this.motor.setPower(1.0);

        while(!super.isStopRequested() && timer.seconds() < TEST_DURATION_SECONDS) {
            maxVelocity = Math.max(maxVelocity, this.motor.getVelocity());
        }

        this.motor.setPower(0.0);

        return maxVelocity;
    }

    private void handleGamepad(FtcGamePad gamePad, int button, boolean isPressed) {

        if(button == FtcGamePad.GAMEPAD_X && isPressed) {
            this.shouldStartTest = true;
        }

    }

}
