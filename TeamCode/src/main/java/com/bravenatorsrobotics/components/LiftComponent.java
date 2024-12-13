package com.bravenatorsrobotics.components;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Config
public class LiftComponent extends AbstractComponent {

    public DcMotorEx lLiftMotor;
    public DcMotorEx rLiftMotor;
    private TouchSensor touchSensor;

    public LiftComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Get Motors
        this.lLiftMotor  = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.LEFT_LIFT_MOTOR);
        this.rLiftMotor     = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.RIGHT_LIFT_MOTOR);

        // Reset Encoders
        this.resetSystemEncoders();

        // Reverse Motors (if needed
        this.lLiftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        //this.rLiftMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        // Set Zero Power Behavior
        this.rLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.lLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Initialize the Touch Sensor
        this.touchSensor = hardwareMap.get(TouchSensor.class, HardwareMapIdentities.LIFT_TOUCH_SENSOR);
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

    //Check if touch sensor pressed
    public boolean isTouchSensorPressed() {
        return touchSensor.isPressed();
    }

    /**
     * @param position encoder position
     * @param power positive value
     */
    public void setShoulderMotorPositionAsync(int position, double power) {

        this.lLiftMotor.setTargetPosition(position);
        this.lLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.lLiftMotor.setPower(Math.abs(power));

    }

    /**
     * @param position encoder position
     * @param power positive value
     */
    public void setElbowMotorPositionAsync(int position, double power) {

        this.rLiftMotor.setTargetPosition(position);
        this.rLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.rLiftMotor.setPower(Math.abs(power));

    }

}
