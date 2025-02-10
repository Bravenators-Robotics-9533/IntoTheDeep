package com.bravenatorsrobotics.hardware.components;

import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeComponent extends AbstractComponent {

    private final Servo passOffClawServo;
    private final Servo passOffPivotServo;
    private final Servo wallClawServo;

    public OuttakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.passOffClawServo  = hardwareMap.get(Servo.class, HardwareMapIdentities.PASS_OFF_CLAW);
        this.passOffPivotServo = hardwareMap.get(Servo.class, HardwareMapIdentities.PASS_OFF_PIVOT);
        this.wallClawServo     = hardwareMap.get(Servo.class, HardwareMapIdentities.WALL_CLAW);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setPassOffClawServoPosition(double position) {
        passOffClawServo.setPosition(position);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setPassOffPivotServoPosition(double position) {
        passOffPivotServo.setPosition(position);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setWallClawServoPosition(double position) {
        wallClawServo.setPosition(position);
    }

    public double getTargetWallClawServoPosition() { return wallClawServo.getPosition(); }
    public double getTargetPassOffPivotServoPosition() { return passOffClawServo.getPosition(); }
    public double getTargetPassOffClawServoPosition() { return passOffClawServo.getPosition(); }

}
