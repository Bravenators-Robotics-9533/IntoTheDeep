package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.ArmComponent;
import com.bravenatorsrobotics.utils.MovementConstraint;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class ArmController extends AbstractController {

    // Safety Definitions
    private static final double SHOULDER_SAFE_POSITION = 1.0;
    private static final double ELBOW_SAFE_POSITION = 0.0;
    private static final MovementConstraint elbowMovementConstraint = new MovementConstraint(SHOULDER_SAFE_POSITION, ELBOW_SAFE_POSITION);

    public static double REST_SHOULDER = 0.0;
    public static double REST_ELBOW = 0.025;

    // Bottom Basket
    public static double BOTTOM_BASKET_SHOULDER = 0.8;
    public static double BOTTOM_BASKET_ELBOW = 0.5;

    // Top Basket
    public static double TOP_BASKET_SHOULDER = 1.0;
    public static double TOP_BASKET_ELBOW = 0.5;

    // Bottom Bar
    public static double BOTTOM_BAR_SHOULDER = 0.75;
    public static double BOTTOM_BAR_ELBOW = 0.72;

    // High Bar
    public static double HIGH_BAR_SHOULDER = 0.35;
    public static double HIGH_BAR_ELBOW = 0.2;

    // Intake Position
    public static double INTAKE_BASKET_SHOULDER = 0.0;
    public static double INTAKE_BASKET_ELBOW = 0.28;

    // Capture Position
    public static double CAPTURE_BASKET_SHOULDER = 0.0;
    public static double CAPTURE_BASKET_ELBOW = 0.2;

    public enum ArmPosition {

        REST(REST_SHOULDER, REST_ELBOW),
        INTAKE(INTAKE_BASKET_SHOULDER, INTAKE_BASKET_ELBOW),
        CAPTURE(CAPTURE_BASKET_SHOULDER, CAPTURE_BASKET_ELBOW),

        BOTTOM_BASKET(BOTTOM_BASKET_SHOULDER, BOTTOM_BASKET_ELBOW),
        TOP_BASKET(TOP_BASKET_SHOULDER, TOP_BASKET_ELBOW),

        BOTTOM_BAR(BOTTOM_BAR_SHOULDER, BOTTOM_BAR_ELBOW),
        HIGH_BAR(HIGH_BAR_SHOULDER, HIGH_BAR_ELBOW);

        public final double shoulderPosition;
        public final double elbowPosition;

        ArmPosition(double shoulderPosition, double elbowPosition) {
            this.shoulderPosition = shoulderPosition;
            this.elbowPosition = elbowPosition;
        }

    }

    //Set maximum power and postition for control
    private static final double SHOULDER_MAX_POWER = 0.5;
    private static final int SHOULDER_MAX_ENCODER_POSITION = 1245;

    private static final double ELBOW_MAX_POWER = 0.5;
    private static final int ELBOW_MAX_ENCODER_POSITION = 1335;

    private final ArmComponent armComponent;

    private ArmPosition targetArmPosition = ArmPosition.REST;
    private boolean shouldRunSafetyChecks = true;

    public ArmController(ArmComponent armComponent) {
        this.armComponent = armComponent;
    }

    @Override
    public void initialize() {
        this.armComponent.resetSystemEncoders();
    }

    @Override
    public void update() {

        double targetShoulderPosition = targetArmPosition.shoulderPosition;

        // TODO: REDEFINE CONSTRAINTS TO NOT NEED TO BE BYPASSED
        /* This shouldn't be conditional. Eventually the constraint system needs to be updated
         * enough so that the conditional is baked into the logic "shouldRunSafetyChecks" should not
         * happen. The safety check just needs to be smarter and know what is "safe" and "not safe"
         */
        double targetElbowPosition = shouldRunSafetyChecks
                ? elbowMovementConstraint.deriveDependentValue(this.armComponent.shoulderMotor.getCurrentPosition(), targetArmPosition.elbowPosition)
                : targetArmPosition.elbowPosition;

        this.armComponent.setShoulderMotorPositionAsync((int) (targetShoulderPosition * SHOULDER_MAX_ENCODER_POSITION), SHOULDER_MAX_POWER);
        this.armComponent.setElbowMotorPositionAsync((int) (targetElbowPosition * ELBOW_MAX_ENCODER_POSITION), ELBOW_MAX_POWER);

    }

    public void setTargetArmPosition(ArmPosition armPosition) {

        this.targetArmPosition = armPosition;

        // Run safety checks if and only if amr position is not INTAKE or CAPTURE (dangerous move for INTAKE and CAPTURE position)
        this.shouldRunSafetyChecks = !(armPosition == ArmPosition.INTAKE || armPosition == ArmPosition.CAPTURE);

    }

    public void printTelemetry(Telemetry telemetry) {

        telemetry.addData("Shoulder Motor", (double) armComponent.shoulderMotor.getCurrentPosition() / SHOULDER_MAX_ENCODER_POSITION);
        telemetry.addData("Elbow Motor", (double) armComponent.elbowMotor.getCurrentPosition() / ELBOW_MAX_ENCODER_POSITION);
        telemetry.update();

    }

}
