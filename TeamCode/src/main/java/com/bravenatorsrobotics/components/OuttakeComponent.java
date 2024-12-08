package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeComponent extends AbstractComponent {


    protected final Servo passOffServo;
    protected final Servo wallClawServo;

    public OuttakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        passOffServo    = hardwareMap.get(Servo.class, HardwareMapIdentities.PASS_OFF_CLAW);
        wallClawServo    = hardwareMap.get(Servo.class, HardwareMapIdentities.WALL_CLAW);
    }



    /**
     * @param position range [0.0, 1.0]
     */
    public void setPassOffServoPosition(double position) {
        passOffServo.setPosition(position);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setWallClawServoPosition(double position) {
        wallClawServo.setPosition(position);
    }

    public double getTargetWallClawServoPosition() { return wallClawServo.getPosition(); }
    public double getTargetOuttakeServoPosition() { return passOffServo.getPosition(); }

}
