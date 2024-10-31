package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmComponent extends AbstractComponent {

    public DcMotorEx shoulderMotor;
    public DcMotorEx elbowMotor;

    public ArmComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        shoulderMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.SHOULDER_MOTOR);
        elbowMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.ELBOW_MOTOR);

        shoulderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shoulderMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        elbowMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elbowMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        shoulderMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        elbowMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shoulderMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        elbowMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     *
     * @param position encoder position
     * @param power positive value
     */
    public void setShoulderMotorPositionAsync(int position, double power) {

        this.shoulderMotor.setTargetPosition(position);
        this.shoulderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.shoulderMotor.setPower(Math.abs(power));

    }

    /**
     *
     * @param position encoder position
     * @param power positive value
     */
    public void setElbowMotorPositionAsync(int position, double power) {

        this.elbowMotor.setTargetPosition(position);
        this.elbowMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.elbowMotor.setPower(Math.abs(power));

    }

}
