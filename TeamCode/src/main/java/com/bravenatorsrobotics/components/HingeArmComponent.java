package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HingeArmComponent extends AbstractComponent {

    private static final int HINGE_ARM_TOLERANCE = 10;

    protected final DcMotorEx hingeArmMotor;

    public HingeArmComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.hingeArmMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.HINGE_ARM_MOTOR);
        this.hingeArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.hingeArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.hingeArmMotor.setTargetPositionTolerance(HINGE_ARM_TOLERANCE);

        this.hingeArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.hingeArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     *
     * @param power range [-1.0, 1.0]
     */
    public void setPower(double power) {

        // TODO: RECALCULATE WITH VELOCITY
        this.hingeArmMotor.setPower(power);

    }

    public void setTargetPosition(int targetPosition) { this.hingeArmMotor.setTargetPosition(targetPosition); }
    public int getTargetPosition() { return this.hingeArmMotor.getTargetPosition(); }

    public void setMode(DcMotor.RunMode runMode) { this.hingeArmMotor.setMode(runMode); }
    public DcMotor.RunMode getMode() { return this.hingeArmMotor.getMode(); }

    public int getCurrentPosition() { return this.hingeArmMotor.getCurrentPosition(); }

    public boolean isMotorBusy() { return this.hingeArmMotor.isBusy(); }

}
