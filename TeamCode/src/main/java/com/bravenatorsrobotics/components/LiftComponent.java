package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LiftComponent extends AbstractComponent {

    private static final double MAX_MOTOR_VELOCITY = 2800;

    protected final DcMotorEx liftMotor;

    public LiftComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.liftMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.LIFT_MOTOR);

        this.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setMode(DcMotor.RunMode runMode) {
        this.liftMotor.setMode(runMode);
    }

    /**
     * @param power [-1.0, 1.0]
     */
    public void setPower(double power) {
        this.liftMotor.setVelocity(MAX_MOTOR_VELOCITY * power);
    }

    /**
     * @param power range [-1.0, 1.0]
     * @param position all real integers
     */
    public void goToPositionAsync(double power, int position) {

        this.liftMotor.setTargetPosition(position);
        this.liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.liftMotor.setVelocity(MAX_MOTOR_VELOCITY * power);

    }
    public void setTargetPosition(int targetPosition) { this.liftMotor.setTargetPosition(targetPosition); }
    public int getTargetPosition() { return this.liftMotor.getTargetPosition(); }
    public int getCurrentPosition() { return this.liftMotor.getCurrentPosition(); }

    public boolean isMotorBusy() { return this.liftMotor.isBusy(); }

}
