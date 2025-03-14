package com.bravenatorsrobotics.teleop.controlAdapters;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.controllers.IntakeController;
import com.bravenatorsrobotics.robot.Robot;
import com.bravenatorsrobotics.utils.BlockDetectVisionPipeline;
import com.bravenatorsrobotics.utils.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

@Config
public class AutoIntakeControlAdapter implements IControlAdapter {

    public static double TOLERANCE = 0.1;

    public static double Y0_P = 0.0004;
    public static double C_P = 0.0004;

    public static double Y0_D = 0.0004;
    public static double C_D = 0.0004;

    public static double TARGET_Y0 = 600;
    public static double TARGET_C = 960;

    private static final double DX_FILTER = 0.1;

    private double errorY0 = 0;
    private double errorC = 0;

    private double dxY0 = 0;
    private double dxC = 0;

    private final LinearOpMode opMode;
    private final Robot robot;

    private enum State {

        DETECTING,
        INTAKING,
        DONE

    }

    private State state = State.DETECTING;

    private BlockDetectVisionPipeline pipeline;

    public AutoIntakeControlAdapter(LinearOpMode opMode, Robot robot) {
        this.opMode = opMode;
        this.robot = robot;
    }

    @Override
    public void initialize() {

        this.pipeline = new BlockDetectVisionPipeline();
        this.pipeline.initialize(this.opMode.hardwareMap);

    }

    @Override
    public void update() {

        switch (state) {

            case DETECTING:

                BlockDetectVisionPipeline.DetectionData data = this.pipeline.detectBlock();
                if(driveToBlock(data)) {
                    this.state = State.INTAKING;
                }

                break;

            case INTAKING:

                this.robot.drive.setMotorPowers(0, 0, 0, 0);

                this.robot.intakeController.intakeSample();
                this.robot.intakeController.setFlipPositionToIntake();

                if(this.robot.intakeController.getDistanceInMM() <= IntakeController.MIN_SAMPLE_POSITION) {

                    this.robot.intakeController.stopIntake();
                    this.robot.intakeController.setFlipPositionToStandby();

                    this.state = State.DONE;

                }

        }

    }

    public static PIDController pidY0 = new PIDController(0.0004, 0.0001, 0.0001, 0.0);
    public static PIDController pidC = new PIDController(0.0004, 0.0001, 0.0001, 0.0);

    public static double LAG_COEFFICIENT = 0.8;

    /**
     *
     * @return isDone driving to block
     */
    private boolean driveToBlock(BlockDetectVisionPipeline.DetectionData data) {

        if(data == null)
            return false;

        double errorY0 = TARGET_Y0 - data.y0;
        double errorC = TARGET_C - data.c;

        double predictedY0 = errorY0 * LAG_COEFFICIENT;  // Adjust as needed for lag compensation
        double predictedC = errorC * LAG_COEFFICIENT;

        double y = pidY0.calculate(errorY0, predictedY0);
        double x = pidC.calculate(errorC, predictedC);

        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);

        double flPower = Range.clip((y - x) / denominator, -1.0, 1.0);
        double blPower = Range.clip((y + x) / denominator, -1.0, 1.0);
        double frPower = Range.clip((y + x) / denominator, -1.0, 1.0);
        double brPower = Range.clip((y - x) / denominator, -1.0, 1.0);

        // Apply Power to Motors
        this.robot.drive.setMotorPowerByVoltage(flPower, blPower, brPower, frPower);

        // Set Intake Pivot Position Based on Block Angle
        this.robot.intakeController.setPivotOffsetPosition(data.angle == 90 ? 1 : 0.5);

        // Check if the robot is close enough to the target
        return Math.abs(errorY0) < TOLERANCE && Math.abs(errorC) < TOLERANCE;

//
//        double dxY0 = (errorY0 - this.errorY0) * this.dxY0;
//        double dxC = (errorC - this.errorC) * this.dxC;
//
//        this.errorY0 = errorY0;
//        this.errorC = errorC;
//
//        this.dxY0 = dxY0;
//        this.dxC = dxC;
//
//        double y = (Y0_P * errorY0) - (dxY0 * Y0_D);
//        double x = C_P * errorC - (dxC * C_D);
//
//        // Calculate Drive
//        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);
//
//        double flPower  = Range.clip((y - x) / denominator, -1.0, 1.0);
//        double blPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
//        double frPower  = Range.clip((y + x) / denominator, -1.0, 1.0);
//        double brPower =  Range.clip((y - x) / denominator, -1.0, 1.0);
//
//        this.robot.drive.setMotorPowerByVoltage(flPower, blPower, brPower, frPower);
//
//        // Calculate Intake Movement
//        this.robot.intakeController.setPivotOffsetPosition(data.angle == 90 ? 1 : 0.5);
//
//        return y < TOLERANCE && x < TOLERANCE;

    }

    public void destroy() {

        this.robot.intakeController.stopIntake();
        this.robot.intakeController.setFlipPositionToStandby();

        this.state = State.DETECTING;
    }

}
