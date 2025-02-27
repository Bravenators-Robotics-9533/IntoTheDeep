package com.bravenatorsrobotics.hardware.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

// TODO: Change motors to run by velocity

@Config
public class LiftComponent extends AbstractComponent {

    public DcMotorEx lLiftMotor;
    public DcMotorEx rLiftMotor;

    public LiftComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Get Motors
        this.lLiftMotor     = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.LEFT_LIFT_MOTOR);
        this.rLiftMotor     = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.RIGHT_LIFT_MOTOR);

        // Reset Encoders
        this.resetSystemEncoders();

        // Reverse Motors (if needed
        this.lLiftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rLiftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set Zero Power Behavior
        this.rLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.lLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    /**
     * Resets the encoders to 0. Warning don't do this after initialization and know
     * this will kill the position holds. Sets to RUN_USING_ENCODERS at method end
     */
    public void resetSystemEncoders() {
        lLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * @param position encoder position
     * @param power positive value
     */
    public void setLiftPositionAsync(int position, double power) {

        this.lLiftMotor.setTargetPosition(position);
        this.rLiftMotor.setTargetPosition(position);

        this.lLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.rLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.lLiftMotor.setPower(Math.abs(power));
        this.rLiftMotor.setPower(Math.abs(power));

    }

    public boolean isBusy() {
        return this.lLiftMotor.isBusy() || this.rLiftMotor.isBusy();
    }

}
