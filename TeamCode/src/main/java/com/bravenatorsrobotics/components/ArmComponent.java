package com.bravenatorsrobotics.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class ArmComponent extends AbstractComponent {

    public DcMotorEx shoulderMotor;
    public DcMotorEx elbowMotor;

    public ArmComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Get Motors
        this.shoulderMotor  = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.SHOULDER_MOTOR);
        this.elbowMotor     = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.ELBOW_MOTOR);

        // Reset Encoders
        this.resetSystemEncoders();

        // Reverse Motors
        this.shoulderMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.elbowMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set Zero Power Behavior to brake
        this.shoulderMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.elbowMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Resets the encoders to 0. Warning don't do this after initialization and know
     * this will kill the position holds. Sets to RUN_USING_ENCODERS at method end
     */
    public void resetSystemEncoders() {
        shoulderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elbowMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shoulderMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        elbowMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * @param position encoder position
     * @param power positive value
     */
    public void setShoulderMotorPositionAsync(int position, double power) {

        this.shoulderMotor.setTargetPosition(position);
        this.shoulderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.shoulderMotor.setPower(Math.abs(power));

    }

    /**
     * @param position encoder position
     * @param power positive value
     */
    public void setElbowMotorPositionAsync(int position, double power) {

        this.elbowMotor.setTargetPosition(position);
        this.elbowMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.elbowMotor.setPower(Math.abs(power));

    }

}
