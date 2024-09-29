package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SwingArmComponent extends AbstractComponent {

    private static final int SWING_ARM_TOLERANCE = 10;

    protected final DcMotorEx swingArmMotor;

    public SwingArmComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.swingArmMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.SWING_ARM_MOTOR);
        this.swingArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.swingArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.swingArmMotor.setTargetPositionTolerance(SWING_ARM_TOLERANCE);

        this.swingArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.swingArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     *
     * @param power range [-1.0, 1.0]
     */
    public void setPower(double power) {

        // TODO: RECALCULATE WITH VELOCITY
        this.swingArmMotor.setPower(power);

    }

    public int getTargetPosition() { return this.swingArmMotor.getTargetPosition(); }
    public int getCurrentPosition() { return this.swingArmMotor.getCurrentPosition(); }

    public boolean isMotorBusy() { return this.swingArmMotor.isBusy(); }

}
